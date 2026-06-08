package com.greenroute.app.viewmodel

import android.content.Context
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.NoCredentialException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.greenroute.app.data.local.entities.User
import com.greenroute.app.data.local.entities.UserPreferences
import com.greenroute.app.data.repository.UserRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class ProfileUiState(
    val user: User? = null,
    val preferences: UserPreferences? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)

class ProfileViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // Web Client ID (type 3) from google-services.json
    private val WEB_CLIENT_ID = "52455350881-7nmgk6382uj6qoqp4i785ru845t15kqf.apps.googleusercontent.com"

    init {
        loadProfile()
        // If Firebase already has a logged-in user, sync to Room DB
        auth.currentUser?.let { firebaseUser ->
            viewModelScope.launch { syncFirebaseUser(firebaseUser) }
        }
    }

    private fun loadProfile() {
        viewModelScope.launch {
            combine(
                userRepository.currentUser,
                userRepository.currentPreferences
            ) { user, prefs ->
                ProfileUiState(user = user, preferences = prefs, isLoading = false)
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
            .setAutoSelectEnabled(false)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, error = null) }

                val result = credentialManager.getCredential(context, request)
                val credential = result.credential

                when {
                    // Modern approach: CustomCredential with Google ID token type
                    credential is CustomCredential &&
                    credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL -> {
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                        firebaseSignIn(googleIdTokenCredential.idToken)
                    }
                    // Fallback: direct GoogleIdTokenCredential (older versions)
                    credential is GoogleIdTokenCredential -> {
                        firebaseSignIn(credential.idToken)
                    }
                    else -> {
                        Log.e("ProfileViewModel", "Credencial inesperada: ${credential.type}")
                        _uiState.update { it.copy(isLoading = false, error = "Tipo de credencial não suportado") }
                    }
                }
            } catch (e: GetCredentialCancellationException) {
                // User cancelled — not an error
                _uiState.update { it.copy(isLoading = false, error = null) }
            } catch (e: NoCredentialException) {
                Log.e("ProfileViewModel", "Sem credenciais Google", e)
                _uiState.update {
                    it.copy(isLoading = false, error = "Nenhuma conta Google encontrada. Adiciona uma conta Google no dispositivo.")
                }
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Login falhou", e)
                _uiState.update { it.copy(isLoading = false, error = "Falha no login: ${e.localizedMessage}") }
            }
        }
    }

    private suspend fun firebaseSignIn(idToken: String) {
        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
        val authResult = auth.signInWithCredential(firebaseCredential).await()
        val firebaseUser = authResult.user
        if (firebaseUser != null) {
            syncFirebaseUser(firebaseUser)
        } else {
            _uiState.update { it.copy(isLoading = false, error = "Login falhou — tenta novamente") }
        }
    }

    private suspend fun syncFirebaseUser(firebaseUser: FirebaseUser) {
        val email = firebaseUser.email ?: run {
            _uiState.update { it.copy(isLoading = false, error = "Conta Google sem e-mail") }
            return
        }
        val name = firebaseUser.displayName
        val photoUrl = firebaseUser.photoUrl?.toString()
        val uid = firebaseUser.uid

        val existingUser = userRepository.getUserByEmail(email)
        if (existingUser != null) {
            userRepository.updateUser(
                existingUser.copy(
                    name = name,
                    profileImageUri = photoUrl,
                    lastActive = System.currentTimeMillis()
                )
            )
        } else {
            val newUser = User(
                googleId = uid,
                name = name,
                email = email,
                profileImageUri = photoUrl,
                joinedAt = System.currentTimeMillis(),
                lastActive = System.currentTimeMillis()
            )
            val userId = userRepository.insertUser(newUser)
            userRepository.insertPreferences(UserPreferences(userId = userId.toInt()))
        }
    }

    fun logout(context: Context) {
        viewModelScope.launch {
            try {
                // Sign out from Firebase
                auth.signOut()
                // Clear Google credential state
                val credentialManager = CredentialManager.create(context)
                credentialManager.clearCredentialState(ClearCredentialStateRequest())
                // Clear local DB
                userRepository.logout()
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Logout falhou", e)
            }
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
