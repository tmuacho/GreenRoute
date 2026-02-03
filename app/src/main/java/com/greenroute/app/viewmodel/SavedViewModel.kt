package com.greenroute.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.greenroute.app.data.local.entities.Route
import com.greenroute.app.data.repository.RouteRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * UI state for the Saved routes screen.
 */
data class SavedUiState(
    val savedRoutes: List<Route> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

/**
 * ViewModel for the Saved routes screen.
 */
class SavedViewModel(
    private val routeRepository: RouteRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SavedUiState())
    val uiState: StateFlow<SavedUiState> = _uiState.asStateFlow()

    init {
        loadSavedRoutes()
    }

    private fun loadSavedRoutes() {
        viewModelScope.launch {
            try {
                routeRepository.savedRoutes.collect { routes ->
                    _uiState.value = SavedUiState(
                        savedRoutes = routes,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.value = SavedUiState(
                    savedRoutes = emptyList(),
                    isLoading = false,
                    error = e.message ?: "Erro desconhecido"
                )
            }
        }
    }

    fun removeSavedRoute(route: Route) {
        viewModelScope.launch {
            try {
                routeRepository.toggleSaved(route.id, false)
            } catch (e: Exception) {
                // Silently handle error
            }
        }
    }

    fun deleteRoute(route: Route) {
        viewModelScope.launch {
            try {
                routeRepository.delete(route)
            } catch (e: Exception) {
                // Silently handle error
            }
        }
    }

    companion object {
        fun provideFactory(
            routeRepository: RouteRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SavedViewModel(routeRepository) as T
            }
        }
    }
}
