package com.greenroute.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.greenroute.app.data.local.entities.Route
import com.greenroute.app.data.remote.DirectionsRepository
import com.greenroute.app.data.remote.DirectionsStep
import com.greenroute.app.data.repository.RouteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RouteDetailUiState(
    val route: Route? = null,
    val polylinePoints: List<LatLng> = emptyList(),
    val steps: List<DirectionsStep> = emptyList(),
    val isLoadingDirections: Boolean = false,
    val isLoading: Boolean = true,
    val error: String? = null
)

class RouteDetailViewModel(
    private val routeRepository: RouteRepository,
    private val directionsRepository: DirectionsRepository,
    private val routeId: Int
) : ViewModel() {

    private val _uiState = MutableStateFlow(RouteDetailUiState())
    val uiState: StateFlow<RouteDetailUiState> = _uiState

    init {
        loadRoute()
    }

    private fun loadRoute() {
        viewModelScope.launch {
            val route = routeRepository.getRouteById(routeId)
            if (route == null) {
                _uiState.value = RouteDetailUiState(isLoading = false, error = "Rota não encontrada")
                return@launch
            }

            // Show route immediately with any stored polyline
            val storedPoints = if (!route.encodedPolyline.isNullOrEmpty())
                DirectionsRepository.decodePolyline(route.encodedPolyline) else emptyList()

            _uiState.value = RouteDetailUiState(
                route = route,
                polylinePoints = storedPoints,
                isLoading = false,
                isLoadingDirections = true
            )

            // Always fetch fresh directions to get steps + accurate polyline
            fetchDirections(route)
        }
    }

    private suspend fun fetchDirections(route: Route) {
        val destination = route.endLocation
        if (destination.isNullOrEmpty() || destination == "Destino Personalizado") {
            _uiState.update { it.copy(isLoadingDirections = false) }
            return
        }

        // Prefer stored GPS coords; fall back to startLocation text; last resort: Lisbon
        val origin = when {
            route.originLat != null && route.originLng != null ->
                "${route.originLat},${route.originLng}"
            !route.startLocation.isNullOrEmpty() &&
            route.startLocation != "Minha Localização" -> route.startLocation
            else -> DEFAULT_ORIGIN
        }

        val result = directionsRepository.getDirections(
            origin = origin,
            destination = destination,
            transportType = route.transportType ?: "car"
        )

        if (result != null) {
            val polylinePoints = DirectionsRepository.decodePolyline(result.encodedPolyline)

            // Update DB with fresh route data
            val updatedRoute = route.copy(
                encodedPolyline = result.encodedPolyline,
                originLat = result.originLat,
                originLng = result.originLng,
                destLat = result.destLat,
                destLng = result.destLng,
                distance = result.distanceMeters / 1000.0,
                duration = (result.durationSeconds / 60.0).toInt().coerceAtLeast(1)
            )
            routeRepository.update(updatedRoute)

            _uiState.update {
                it.copy(
                    route = updatedRoute,
                    polylinePoints = polylinePoints,
                    steps = result.steps,
                    isLoadingDirections = false
                )
            }
        } else {
            _uiState.update { it.copy(isLoadingDirections = false) }
        }
    }

    fun toggleSaved() {
        viewModelScope.launch {
            val route = _uiState.value.route ?: return@launch
            val newSaved = !route.isSaved
            routeRepository.toggleSaved(route.id, newSaved)
            _uiState.update { it.copy(route = it.route?.copy(isSaved = newSaved)) }
        }
    }

    companion object {
        private const val DEFAULT_ORIGIN = "Lisboa, Portugal"

        fun provideFactory(
            routeRepository: RouteRepository,
            directionsRepository: DirectionsRepository,
            routeId: Int
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return RouteDetailViewModel(routeRepository, directionsRepository, routeId) as T
            }
        }
    }
}
