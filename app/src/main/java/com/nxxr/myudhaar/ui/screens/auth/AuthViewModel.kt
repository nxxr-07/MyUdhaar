package com.nxxr.myudhaar.ui.screens.auth

import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.nxxr.myudhaar.R
import com.nxxr.myudhaar.data.repository.AuthRepository
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application){

    private val authRepository = AuthRepository()
    private val _authState = MutableLiveData<AuthState>(AuthState.Unauthenticated)
    val authState: LiveData<AuthState> = _authState

    init {
        // If already signed in, mark authenticated
        if (authRepository.currentUserId() != null) {
            _authState.value = AuthState.Success
        }
    }


    /**
     * Sign in using a Google credential.
     */
    fun signInWithGoogleCredential(credential: AuthCredential) {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            val result = authRepository.signInWithCredential(credential)
            if (result.isSuccess) {
                _authState.postValue(AuthState.Success)
            } else {
                val error = result.exceptionOrNull()
                Log.e("GoogleSignIn", "Firebase sign-in failed", error)
                _authState.postValue(AuthState.Error("Sign-in failed: ${error?.localizedMessage ?: error.toString()}"))

            }
        }
    }

    /**
     * Sign out the current user.
     */
    fun logout() {
        authRepository.logout()
        _authState.value = AuthState.Unauthenticated
    }
}


