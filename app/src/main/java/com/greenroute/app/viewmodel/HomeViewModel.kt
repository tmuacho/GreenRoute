package com.greenroute.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.greenroute.app.data.local.entities.Route
import com.greenroute.app.data.local.entities.User
import com.greenroute.app.data.repository.RouteRepository
import com.greenroute.app.data.repository.UserRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * UI state for the Home screen.
 */
data class HomeUiState(
    val user: User? = null,
    val recentRoutes: List<Route> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

/**
 * ViewModel for the Home screen.
 */
class HomeViewModel(
    private val routeRepository: RouteRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // Load user
            userRepository.currentUser.collect { user ->
                _uiState.update { it.copy(user = user) }
            }
        }

        viewModelScope.launch {
            // Load recent routes (limit 3 for home screen)
            routeRepository.getRecentRoutes(3).collect { routes ->
                _uiState.update { 
                    it.copy(
                        recentRoutes = routes,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun toggleSaveRoute(route: Route) {
        viewModelScope.launch {
            routeRepository.toggleSaved(route.id, !route.isSaved)
        }
    }

    fun deleteRoute(route: Route) {
        viewModelScope.launch {
            routeRepository.delete(route)
        }
    }

    companion object {
        fun provideFactory(
            routeRepository: RouteRepository,
            userRepository: UserRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return HomeViewModel(routeRepository, userRepository) as T
            }
        }
    }
}
