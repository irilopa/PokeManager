package org.ivan.pokmanager.presentation.state

/**
 * Estado del envio del correo para restablecer la contrasena.
 */
sealed interface PasswordResetState {
    data object Idle : PasswordResetState
    data object Sending : PasswordResetState
    data object Sent : PasswordResetState
    data class Error(val message: String) : PasswordResetState
}
