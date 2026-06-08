package com.greenroute.app.viewmodel

import android.content.Context
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
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

    private val WEB_CLIENT_ID = "133619565909-44ljquqmi7okdjahfs7a0acqd746itfj.apps.googleusercontent.com"

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
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

    fun signInWithGoogle(context: Context) {
        val credentialManager = CredentialManager.create(context)
        
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(WEB_CLIENT_ID)
            .setAutoSelectEnabled(true)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        viewModelScope.launch {
            try {
                val result = credentialManager.getCredential(context, request)
                val credential = result.credential
                
                if (credential is GoogleIdTokenCredential) {
                    handleSignInSuccess(credential)
                }
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Login failed", e)
                _uiState.update { it.copy(error = "Falha no login: ${e.message}") }
            }
        }
    }

    private suspend fun handleSignInSuccess(credential: GoogleIdTokenCredential) {
        val email = credential.id
        val name = credential.displayName ?: credential.givenName
        val photoUri = credential.profilePictureUri?.toString()
        val googleId = credential.id // Use email or a specific ID

        // Check if user exists
        val existingUser = userRepository.getUserByEmail(email)
        
        if (existingUser != null) {
            userRepository.updateUser(existingUser.copy(
                name = name,
                profileImageUri = photoUri,
                lastActive = System.currentTimeMillis()
            ))
        } else {
            val newUser = User(
                googleId = googleId,
                name = name,
                email = email,
                profileImageUri = photoUri,
                joinedAt = System.currentTimeMillis(),
                lastActive = System.currentTimeMillis()
            )
            val userId = userRepository.insertUser(newUser)
            
            // Create default preferences for new user
            userRepository.insertPreferences(UserPreferences(userId = userId.toInt()))
        }
    }

    fun logout(context: Context) {
        viewModelScope.launch {
            val credentialManager = CredentialManager.create(context)
            credentialManager.clearCredentialState(ClearCredentialStateRequest())
            userRepository.logout()
        }
    }

    fun toggleEcoMode(enabled: Boolean) {
        viewModelScope.launch {
            uiState.value.preferences?.let { prefs ->
                userRepository.updatePreferences(prefs.copy(ecoModeEnabled = enabled))
            }
        }
    }

    fun toggleNotifications(enabled: Boolean) {
        viewModelScope.launch {
            uiState.value.preferences?.let { prefs ->
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
