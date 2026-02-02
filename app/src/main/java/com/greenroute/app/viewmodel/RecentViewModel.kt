package com.greenroute.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.greenroute.app.data.local.entities.Route
import com.greenroute.app.data.repository.RouteRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * UI state for the Recent routes screen.
 */
data class RecentUiState(
    val recentRoutes: List<Route> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

/**
 * ViewModel for the Recent routes screen.
 */
class RecentViewModel(
    private val routeRepository: RouteRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecentUiState())
    val uiState: StateFlow<RecentUiState> = _uiState.asStateFlow()

    init {
        loadRecentRoutes()
    }

    private fun loadRecentRoutes() {
        viewModelScope.launch {
            routeRepository.allRoutes
                .catch { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = e.message
                        )
                    }
                }
                .collect { routes ->
                    _uiState.update {
                        it.copy(
                            recentRoutes = routes,
                            isLoading = false,
                            error = null
                        )
                    }
                }
        }
    }

    fun toggleSaveRoute(route: Route) {
        viewModelScope.launch {
            try {
                routeRepository.toggleSaved(route.id, !route.isSaved)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun deleteRoute(route: Route) {
        viewModelScope.launch {
            try {
                routeRepository.delete(route)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    companion object {
        fun provideFactory(
            routeRepository: RouteRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return RecentViewModel(routeRepository) as T
            }
        }
    }
}
