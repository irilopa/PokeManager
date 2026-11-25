package org.ivan.pokmanager.presentation.state

/**
 * Estado del borrado de cuenta.
 */
sealed interface DeleteAccountState {
    data object Idle : DeleteAccountState
    data object Deleting : DeleteAccountState
    data object Deleted : DeleteAccountState
    data class Error(val message: String) : DeleteAccountState
}
