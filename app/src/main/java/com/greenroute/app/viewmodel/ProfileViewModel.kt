package com.greenroute.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.greenroute.app.data.local.entities.User
import com.greenroute.app.data.local.entities.UserPreferences
import com.greenroute.app.data.repository.UserRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * UI state for the Profile screen.
 */
data class ProfileUiState(
    val user: User? = null,
    val preferences: UserPreferences? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)

/**
 * ViewModel for the Profile screen.
 */
class ProfileViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            // Combine user and preferences flows
            combine(
                userRepository.currentUser,
                userRepository.currentPreferences
            ) { user, prefs ->
                ProfileUiState(
                    user = user,
                    preferences = prefs,
                    isLoading = false
                )
            }.catch { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun updatePreferredTransport(transport: String) {
        viewModelScope.launch {
            userRepository.currentPreferences.firstOrNull()?.let { prefs ->
                userRepository.updatePreferences(prefs.copy(preferredTransport = transport))
            }
        }
    }

    fun toggleEcoMode(enabled: Boolean) {
        viewModelScope.launch {
            userRepository.currentPreferences.firstOrNull()?.let { prefs ->
                userRepository.updatePreferences(prefs.copy(ecoModeEnabled = enabled))
            }
        }
    }

    fun toggleNotifications(enabled: Boolean) {
        viewModelScope.launch {
            userRepository.currentPreferences.firstOrNull()?.let { prefs ->
                userRepository.updatePreferences(prefs.copy(notificationsEnabled = enabled))
            }
        }
    }

    companion object {
        fun provideFactory(
            userRepository: UserRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ProfileViewModel(userRepository) as T
            }
        }
    }
}
