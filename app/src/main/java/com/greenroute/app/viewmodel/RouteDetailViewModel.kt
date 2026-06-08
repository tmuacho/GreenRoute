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
    val isLoading: Boolean = true,
    val error: String? = null
)

class RouteDetailViewModel(
    private val routeRepository: RouteRepository,
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
            if (route != null) {
                val polylinePoints = if (!route.encodedPolyline.isNullOrEmpty()) {
                    DirectionsRepository.decodePolyline(route.encodedPolyline)
                } else emptyList()
                _uiState.value = RouteDetailUiState(
                    route = route,
                    polylinePoints = polylinePoints,
                    isLoading = false
                )
            } else {
                _uiState.value = RouteDetailUiState(isLoading = false, error = "Rota não encontrada")
            }
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
        fun provideFactory(
            routeRepository: RouteRepository,
            routeId: Int
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return RouteDetailViewModel(routeRepository, routeId) as T
            }
        }
    }
}
