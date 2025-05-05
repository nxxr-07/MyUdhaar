package com.nxxr.myudhaar.ui.screens.auth

import android.content.Context
import android.content.IntentSender
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.nxxr.myudhaar.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel (context: Context): ViewModel() {

    private val authRepository = AuthRepository(context) // Avoid direct instantiation if using DI in future

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun launchOneTap() {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val intent = authRepository.beginSignIn()
                val sender = intent?.getParcelableExtra<IntentSender>("INTENT_SENDER")
                _authState.value = sender?.let { AuthState.SignInIntent(it) }
                    ?: AuthState.Error("Could not retrieve IntentSender.")
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.localizedMessage ?: "Unknown error occurred.")
            }
        }
    }

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val user = authRepository.signInWithGoogle(idToken)
                _authState.value = user?.let { AuthState.Success(it) }
                    ?: AuthState.Error("Sign-in failed. User is null.")
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.localizedMessage ?: "Google sign-in failed.")
            }
        }
    }

    fun signOut() {
        authRepository.signOut()
        _authState.value = AuthState.Idle
    }
}

// Recommended: Define state outside the ViewModel
sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class SignInIntent(val intentSender: IntentSender) : AuthState()
    data class Success(val user: FirebaseUser) : AuthState()
    data class Error(val message: String) : AuthState()
}
