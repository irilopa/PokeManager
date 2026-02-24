package org.ivan.pokmanager.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.ivan.pokmanager.domain.repository.AuthRepository

class LoginScreenViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _email = MutableStateFlow("")
    private val _password = MutableStateFlow("")
    private val _message = MutableStateFlow<String?>(null)

    val email: StateFlow<String> = _email.asStateFlow()
    val password: StateFlow<String> = _password.asStateFlow()
    val message: StateFlow<String?> = _message.asStateFlow()

    fun setEmail(value: String) {
        _email.value = value
    }

    fun setPassword(value: String) {
        _password.value = value
    }

    fun login() {

        val currentEmail = _email.value.trim()
        val currentPassword = _password.value

        if (currentEmail.isBlank() || currentPassword.isBlank()) {
            _message.value = "Campos vacíos"
            return
        }

        viewModelScope.launch {

            val result = authRepository.login(
                email = currentEmail,
                password = currentPassword
            )

            result
                .onSuccess {
                    _message.value = "SUCCESS"
                }
                .onFailure {
                    _message.value = it.message ?: "Error al iniciar sesión"
                }
        }
    }

    fun clearMessage() {
        _message.value = null
    }
}