package com.greenroute.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.greenroute.app.data.local.entities.Route
import com.greenroute.app.data.remote.DirectionsRepository
import com.greenroute.app.data.repository.AutocompleteResult
import com.greenroute.app.data.repository.PlaceRepository
import com.greenroute.app.data.repository.RouteRepository
import com.greenroute.app.data.repository.UserRepository
import com.greenroute.app.ui.components.TransportOption
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class SearchUiState(
    val searchQuery: String = "",
    val destination: String = "",
    val transportOptions: List<TransportOption> = emptyList(),
    val locationPredictions: List<AutocompleteResult> = emptyList(),
    val selectedTransport: String? = null,
    val isLoading: Boolean = false,
    val isLoadingPredictions: Boolean = false,
    val isStartingRoute: Boolean = false,
    val isEcoMode: Boolean = false,
    // GPS location as "lat,lng" string — null until permission granted
    val currentLocationString: String? = null,
    val hasLocationPermission: Boolean = false,
    val autocompleteError: String? = null,
    val error: String? = null
)

class SearchViewModel(
    application: Application,
    private val routeRepository: RouteRepository,
    private val userRepository: UserRepository,
    private val placeRepository: PlaceRepository,
    private val directionsRepository: DirectionsRepository
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val _navigationEvents = Channel<SearchNavigationEvent>()
    val navigationEvents = _navigationEvents.receiveAsFlow()

    private var searchJob: Job? = null

    init {
        // Do NOT pre-load default options — the list stays empty until a
        // destination is selected so the user sees a clean empty state
        observePreferences()
    }

    // ── Location ─────────────────────────────────────────────────────────────

    fun setCurrentLocation(lat: Double, lng: Double) {
        val locString = "$lat,$lng"
        _uiState.update { it.copy(currentLocationString = locString, hasLocationPermission = true) }
        val dest = _uiState.value.destination
        if (dest.isNotEmpty()) fetchTransportOptions(dest)
    }

    fun setLocationPermissionDenied() {
        _uiState.update { it.copy(hasLocationPermission = false, currentLocationString = null) }
    }

    // ── Search / autocomplete ─────────────────────────────────────────────────

    fun updateSearchQuery(query: String) {
        // Always clear previous destination/results when the user types manually
        _uiState.update {
            it.copy(
                searchQuery = query,
                destination = "",
                transportOptions = emptyList(),
                locationPredictions = emptyList(),
                autocompleteError = null,
                isLoadingPredictions = false
            )
        }

        searchJob?.cancel()

        if (query.length > 2) {
            searchJob = viewModelScope.launch {
                delay(300)
                _uiState.update { it.copy(isLoadingPredictions = true, autocompleteError = null) }

                val result = placeRepository.searchPlaces(query)
                result.fold(
                    onSuccess = { predictions ->
                        _uiState.update {
                            it.copy(
                                locationPredictions = predictions,
                                isLoadingPredictions = false,
                                autocompleteError = null
                            )
                        }
                    },
                    onFailure = { err ->
                        _uiState.update {
                            it.copy(
                                locationPredictions = emptyList(),
                                isLoadingPredictions = false,
                                autocompleteError = err.message
                            )
                        }
                    }
                )
            }
        }
    }

    fun clearPredictions() {
        _uiState.update { it.copy(locationPredictions = emptyList(), autocompleteError = null) }
    }

    fun selectPrediction(result: AutocompleteResult) {
        _uiState.update {
            it.copy(
                searchQuery = result.primaryText,
                destination = result.fullText,
                locationPredictions = emptyList(),
                autocompleteError = null,
                isLoadingPredictions = false
            )
        }
        placeRepository.resetSessionToken()
        fetchTransportOptions(result.fullText)
    }

    // ── Transport options ─────────────────────────────────────────────────────

    private fun observePreferences() {
        viewModelScope.launch {
            userRepository.currentPreferences.collect { prefs ->
                val ecoMode = prefs?.ecoModeEnabled == true
                _uiState.update { it.copy(isEcoMode = ecoMode) }
                val currentOptions = _uiState.value.transportOptions
                if (currentOptions.isNotEmpty()) updateOptionsList(currentOptions, ecoMode)
            }
        }
    }

    private fun fetchTransportOptions(destination: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val origin = _uiState.value.currentLocationString ?: DEFAULT_ORIGIN

            val transportTypes = listOf("car", "bus", "train", "metro", "walk", "bike")

            val options: List<TransportOption> = coroutineScope {
                transportTypes.map { type ->
                    async {
                        val res = directionsRepository.getDirections(origin, destination, type)
                        if (res != null && res.distanceMeters > 0) {
                            val distKm = res.distanceMeters / 1000.0
                            val durMin = (res.durationSeconds / 60.0).toInt().coerceAtLeast(1)
                            val co2 = routeRepository.calculateCo2Emission(type, distKm)
                            TransportOption(type = type, duration = durMin, co2Emission = co2, distance = distKm)
                        } else {
                            fallbackOption(type)
                        }
                    }
                }.awaitAll()
            }

            updateOptionsList(options, _uiState.value.isEcoMode)
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private fun fallbackOption(type: String) = when (type) {
        "car"   -> TransportOption("car",   12, 250.0, 1.8)
        "bus"   -> TransportOption("bus",   25, 380.0, 5.1)
        "train" -> TransportOption("train", 30, 190.0, 5.0)
        "metro" -> TransportOption("metro", 22, 200.0, 4.7)
        "walk"  -> TransportOption("walk",  80,   0.0, 4.5)
        "bike"  -> TransportOption("bike",  25,   0.0, 4.5)
        else    -> TransportOption(type,    20,   0.0, 2.0)
    }

    private fun updateOptionsList(options: List<TransportOption>, isEcoMode: Boolean) {
        val minCo2 = options.minOfOrNull { it.co2Emission } ?: 0.0
        val updated = options.map { it.copy(isBestEcoChoice = isEcoMode && it.co2Emission <= minCo2) }
        val sorted = if (isEcoMode) updated.sortedBy { it.co2Emission } else updated.sortedBy { it.duration }
        _uiState.update { it.copy(transportOptions = sorted) }
    }

    fun selectTransport(type: String) {
        _uiState.update { it.copy(selectedTransport = type) }
    }

    // ── Start route ───────────────────────────────────────────────────────────

    fun startRoute(option: TransportOption) {
        val dest = _uiState.value.destination
        val origin = _uiState.value.currentLocationString ?: DEFAULT_ORIGIN

        viewModelScope.launch {
            _uiState.update { it.copy(isStartingRoute = true) }

            val directions = if (dest.isNotEmpty()) {
                directionsRepository.getDirections(
                    origin = origin,
                    destination = dest,
                    transportType = option.type
                )
            } else null

            val distanceKm = directions?.let { it.distanceMeters / 1000.0 } ?: option.distance
            val durationMin = directions?.let { (it.durationSeconds / 60.0).toInt().coerceAtLeast(1) } ?: option.duration
            val co2Emission = if (distanceKm > 0) routeRepository.calculateCo2Emission(option.type, distanceKm)
                              else option.co2Emission

            val startLabel = if (_uiState.value.currentLocationString != null) "Minha Localização" else DEFAULT_ORIGIN

            val route = Route(
                startLocation = startLabel,
                endLocation = dest.ifEmpty { "Destino Personalizado" },
                transportType = option.type,
                co2Emission = co2Emission,
                distance = distanceKm,
                duration = durationMin,
                dateTime = System.currentTimeMillis(),
                isSaved = false,
                encodedPolyline = directions?.encodedPolyline,
                originLat = directions?.originLat,
                originLng = directions?.originLng,
                destLat = directions?.destLat,
                destLng = directions?.destLng
            )

            val routeId = routeRepository.insert(route)
            _uiState.update { it.copy(isStartingRoute = false) }
            _navigationEvents.send(SearchNavigationEvent.NavigateToRouteDetail(routeId.toInt()))
        }
    }

    companion object {
        const val DEFAULT_ORIGIN = "Lisboa, Portugal"

        fun provideFactory(
            application: Application,
            routeRepository: RouteRepository,
            userRepository: UserRepository,
            placeRepository: PlaceRepository,
            directionsRepository: DirectionsRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SearchViewModel(
                    application, routeRepository, userRepository, placeRepository, directionsRepository
                ) as T
            }
        }
    }
}

sealed class SearchNavigationEvent {
    data class NavigateToRouteDetail(val routeId: Int) : SearchNavigationEvent()
}
