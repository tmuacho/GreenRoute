package com.greenroute.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.greenroute.app.data.local.entities.Route
import com.greenroute.app.data.repository.RouteRepository
import com.greenroute.app.ui.components.TransportOption
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * UI state for the Search screen.
 */
data class SearchUiState(
    val searchQuery: String = "",
    val destination: String = "",
    val transportOptions: List<TransportOption> = emptyList(),
    val selectedTransport: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

/**
 * ViewModel for the Search screen.
 */
class SearchViewModel(
    private val routeRepository: RouteRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    init {
        // Load default transport options
        loadDefaultOptions()
    }

    private fun loadDefaultOptions() {
        // Default transport options for demonstration
        val defaultOptions = listOf(
            TransportOption("car", 9, 216.0, 1.8),
            TransportOption("bus", 19, 346.0, 5.1),
            TransportOption("train", 23, 174.0, 5.0),
            TransportOption("walk", 56, 0.0, 4.5),
            TransportOption("metro", 17, 188.0, 4.7),
            TransportOption("bike", 18, 0.0, 4.5)
        )
        _uiState.update { it.copy(transportOptions = defaultOptions) }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun updateDestination(destination: String) {
        _uiState.update { it.copy(destination = destination) }
        // In a real app, this would trigger a search for routes
        searchRoutes(destination)
    }

    private fun searchRoutes(destination: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            // Simulate search - in real app, this would call an API
            // For now, just update the destination in options
            val options = listOf(
                TransportOption("car", 9, 216.0, 1.8),
                TransportOption("bus", 19, 346.0, 5.1),
                TransportOption("train", 23, 174.0, 5.0),
                TransportOption("walk", 56, 0.0, 4.5),
                TransportOption("metro", 17, 188.0, 4.7),
                TransportOption("bike", 18, 0.0, 4.5)
            )
            
            _uiState.update { 
                it.copy(
                    transportOptions = options,
                    isLoading = false
                )
            }
        }
    }

    fun selectTransport(type: String) {
        _uiState.update { it.copy(selectedTransport = type) }
    }

    fun startRoute(option: TransportOption) {
        viewModelScope.launch {
            // Create and save the route
            val route = Route(
                startLocation = "Saldanha", // Default start location
                endLocation = _uiState.value.destination.ifEmpty { "Metro Entrecampos" },
                transportType = option.type,
                co2Emission = option.co2Emission,
                distance = option.distance,
                duration = option.duration,
                dateTime = System.currentTimeMillis(),
                isSaved = false
            )
            routeRepository.insert(route)
        }
    }

    companion object {
        fun provideFactory(
            routeRepository: RouteRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SearchViewModel(routeRepository) as T
            }
        }
    }
}
