package com.greenroute.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.greenroute.app.data.local.entities.Route
import com.greenroute.app.data.repository.RouteRepository
import com.greenroute.app.data.repository.UserRepository
import com.greenroute.app.ui.components.TransportOption
import kotlinx.coroutines.channels.Channel
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
    val isEcoMode: Boolean = false,
    val error: String? = null
)

/**
 * ViewModel for the Search screen.
 */
class SearchViewModel(
    application: Application,
    private val routeRepository: RouteRepository,
    private val userRepository: UserRepository
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    // Navigation event channel
    private val _navigationEvents = Channel<SearchNavigationEvent>()
    val navigationEvents = _navigationEvents.receiveAsFlow()

    init {
        loadDefaultOptions()
        observePreferences()
    }

    private fun observePreferences() {
         viewModelScope.launch {
             userRepository.currentPreferences.collect { prefs ->
                 val ecoMode = prefs?.ecoModeEnabled == true
                 _uiState.update { it.copy(isEcoMode = ecoMode) }
                 // Re-sort current options if preference changes
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
        // Sort based on current eco mode state (which might be default false initially until loaded)
        // But observePreferences runs concurrently.
        updateOptionsList(defaultOptions, _uiState.value.isEcoMode)
    }

    fun updateSearchQuery(query: String) {
        // Direct manual input without API
        _uiState.update { 
            it.copy(
                searchQuery = query,
                destination = query 
            ) 
        }
        // Simulate real-time search update if needed, or wait for selection
        if (query.length > 2) {
            searchRoutes(query)
        }
    }

    private fun searchRoutes(destination: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            // Just simulate a small delay
            kotlinx.coroutines.delay(300)

            val options = listOf(
                TransportOption("car", 9, 216.0, 1.8),
                TransportOption("bus", 19, 346.0, 5.1),
                TransportOption("train", 23, 174.0, 5.0),
                TransportOption("walk", 56, 0.0, 4.5),
                TransportOption("metro", 17, 188.0, 4.7),
                TransportOption("bike", 18, 0.0, 4.5)
            )
            
            updateOptionsList(options, _uiState.value.isEcoMode)
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private fun updateOptionsList(options: List<TransportOption>, isEcoMode: Boolean) {
        // Find min CO2 to mark best choice
        val minCo2 = options.minOfOrNull { it.co2Emission } ?: 0.0
        
        // Update options with isBestEcoChoice flag
        val updatedOptions = options.map { 
            it.copy(isBestEcoChoice = isEcoMode && it.co2Emission <= minCo2)
        }

        val sortedOptions = if (isEcoMode) {
            updatedOptions.sortedBy { it.co2Emission }
        } else {
            // Default sorting (e.g., by duration or original order)
            updatedOptions.sortedBy { it.duration }
        }
        _uiState.update { it.copy(transportOptions = sortedOptions) }
    }

    fun selectTransport(type: String) {
        _uiState.update { it.copy(selectedTransport = type) }
    }

    fun startRoute(option: TransportOption) {
        viewModelScope.launch {
            val dest = _uiState.value.destination.ifEmpty { "Destino Personalizado" }
            val route = Route(
                startLocation = "Saldanha",
                endLocation = dest,
                transportType = option.type,
                co2Emission = option.co2Emission,
                distance = option.distance,
                duration = option.duration,
                dateTime = System.currentTimeMillis(),
                isSaved = false
            )
            routeRepository.insert(route)
            
            // Notify UI to navigate
            _navigationEvents.send(SearchNavigationEvent.NavigateToRecent)
        }
    }

    companion object {
        fun provideFactory(
            application: Application,
            routeRepository: RouteRepository,
            userRepository: UserRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SearchViewModel(application, routeRepository, userRepository) as T
            }
        }
    }
}

sealed class SearchNavigationEvent {
    object NavigateToRecent : SearchNavigationEvent()
}
