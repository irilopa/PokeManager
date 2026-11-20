package org.ivan.pokmanager.presentation.state

/**
 * Estado del guardado de un formulario.
 */
sealed interface SaveState {
    data object Idle : SaveState
    data object Saving : SaveState
    data object Success : SaveState
    data class Error(val message: String) : SaveState
}
