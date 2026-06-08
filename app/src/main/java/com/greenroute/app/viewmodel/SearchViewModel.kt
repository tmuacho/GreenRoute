package com.greenroute.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.greenroute.app.data.local.entities.Route
import com.greenroute.app.data.remote.DirectionsRepository
import com.greenroute.app.data.repository.PlaceRepository
import com.greenroute.app.data.repository.RouteRepository
import com.greenroute.app.data.repository.UserRepository
import com.greenroute.app.ui.components.TransportOption
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class SearchUiState(
    val searchQuery: String = "",
    val destination: String = "",
    val transportOptions: List<TransportOption> = emptyList(),
    val locationPredictions: List<AutocompletePrediction> = emptyList(),
    val selectedTransport: String? = null,
    val isLoading: Boolean = false,
    val isStartingRoute: Boolean = false,
    val isEcoMode: Boolean = false,
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
        loadDefaultOptions()
        observePreferences()
    }

    private fun observePreferences() {
        viewModelScope.launch {
            userRepository.currentPreferences.collect { prefs ->
                val ecoMode = prefs?.ecoModeEnabled == true
                _uiState.update { it.copy(isEcoMode = ecoMode) }
                val currentOptions = _uiState.value.transportOptions
                if (currentOptions.isNotEmpty()) {
                    updateOptionsList(currentOptions, ecoMode)
                }
            }
        }
    }

    private fun loadDefaultOptions() {
        val defaultOptions = listOf(
            TransportOption("car", 9, 216.0, 1.8),
            TransportOption("bus", 19, 346.0, 5.1),
            TransportOption("train", 23, 174.0, 5.0),
            TransportOption("walk", 56, 0.0, 4.5),
            TransportOption("metro", 17, 188.0, 4.7),
            TransportOption("bike", 18, 0.0, 4.5)
        )
        updateOptionsList(defaultOptions, _uiState.value.isEcoMode)
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }

        searchJob?.cancel()
        if (query.length > 2) {
            searchJob = viewModelScope.launch {
                delay(300)
                val predictions = placeRepository.searchPlaces(getApplication(), query)
                _uiState.update { it.copy(locationPredictions = predictions) }
            }
        } else {
            _uiState.update { it.copy(locationPredictions = emptyList()) }
        }
    }

    fun selectPrediction(prediction: AutocompletePrediction) {
        val destinationName = prediction.getPrimaryText(null).toString()
        _uiState.update {
            it.copy(
                searchQuery = destinationName,
                destination = destinationName,
                locationPredictions = emptyList()
            )
        }
        fetchTransportOptions(destinationName)
    }

    private fun fetchTransportOptions(destination: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            delay(300)
            val options = listOf(
                TransportOption("car", 12, 250.0, 1.8),
                TransportOption("bus", 25, 380.0, 5.1),
                TransportOption("train", 30, 190.0, 5.0),
                TransportOption("walk", 80, 0.0, 4.5),
                TransportOption("metro", 22, 200.0, 4.7),
                TransportOption("bike", 25, 0.0, 4.5)
            )
            updateOptionsList(options, _uiState.value.isEcoMode)
            _uiState.update { it.copy(isLoading = false) }
        }
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

    fun startRoute(option: TransportOption) {
        val dest = _uiState.value.destination

        viewModelScope.launch {
            _uiState.update { it.copy(isStartingRoute = true) }

            val directions = if (dest.isNotEmpty()) {
                directionsRepository.getDirections(
                    origin = DEFAULT_ORIGIN,
                    destination = dest,
                    transportType = option.type
                )
            } else null

            val distanceKm = directions?.let { it.distanceMeters / 1000.0 } ?: option.distance
            val durationMin = directions?.let { (it.durationSeconds / 60.0).toInt().coerceAtLeast(1) } ?: option.duration
            val co2Emission = if (distanceKm > 0) {
                routeRepository.calculateCo2Emission(option.type, distanceKm)
            } else {
                option.co2Emission
            }

            val route = Route(
                startLocation = "Minha Localização",
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
        private const val DEFAULT_ORIGIN = "Lisboa, Portugal"

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
