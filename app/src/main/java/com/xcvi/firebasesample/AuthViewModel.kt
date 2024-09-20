package com.xcvi.firebasesample

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authService: AuthenticationService
) : ViewModel() {

    var email by mutableStateOf("ras@gmail.com")
    var password by mutableStateOf("12345678")

    var user: User? by mutableStateOf(null)

    init {
        authService.isLoggedIn {
            user = it
        }
    }

    fun onEmailInput(value: String) {
        email = value
    }

    fun onPasswordInput(value: String) {
        password = value
    }

    fun register(
        onSuccess: (user: User) -> Unit, onFailure: (Exception) -> Unit
    ) {
        viewModelScope.launch {
            authService.register(
                email = email,
                password = password,
                onSuccess = {
                    onSuccess(it)
                },
                onFailure = {
                    onFailure(it)
                }
            )
        }
    }

    fun login(onSuccess: (user: User) -> Unit, onFailure: (Exception) -> Unit) {
        viewModelScope.launch{
            authService.login(
                email = email,
                password = password,
                onSuccess = {
                    onSuccess(it)
                },
                onFailure = {
                    onFailure(it)
                }
            )
        }
    }

    fun logout(){
        viewModelScope.launch {
            authService.logout()
        }
    }

}





















