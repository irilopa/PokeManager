package org.ivan.pokmanager.presentation.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.ivan.pokmanager.domain.repository.AuthRepository
import org.ivan.pokmanager.presentation.state.PasswordResetState

/**
 * Envia el correo para restablecer la contrasena.
 */
class ForgotPasswordViewModel(
    private val authRepository: AuthRepository,
    /** Valida el formato del email; inyectable para los tests JVM, donde Patterns no existe. */
    private val isValidEmail: (String) -> Boolean = { Patterns.EMAIL_ADDRESS.matcher(it).matches() }
) : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _state = MutableStateFlow<PasswordResetState>(PasswordResetState.Idle)
    val state: StateFlow<PasswordResetState> = _state.asStateFlow()

    fun setEmail(value: String) {
        _email.value = value
        if (_state.value !is PasswordResetState.Sending) _state.value = PasswordResetState.Idle
    }

    fun sendReset() {
        if (_state.value is PasswordResetState.Sending) return
        val email = _email.value.trim()
        if (!isValidEmail(email)) {
            _state.value = PasswordResetState.Error("Introduce un email válido.")
            return
        }
        _state.value = PasswordResetState.Sending
        viewModelScope.launch {
            _state.value = authRepository.sendPasswordReset(email).fold(
                onSuccess = { PasswordResetState.Sent },
                onFailure = { PasswordResetState.Error(it.message ?: "No se pudo enviar el correo.") }
            )
        }
    }

    companion object {
        const val SENT_MESSAGE =
            "Si hay una cuenta con ese email, recibirás un correo con un enlace para crear una contraseña nueva."
    }
}
