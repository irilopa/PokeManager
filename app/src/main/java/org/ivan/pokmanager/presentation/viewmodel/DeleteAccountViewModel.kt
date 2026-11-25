package org.ivan.pokmanager.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.ivan.pokmanager.domain.usecase.DeleteAccountUseCase
import org.ivan.pokmanager.presentation.state.DeleteAccountState

/**
 * Gestiona el borrado de la cuenta y de todos los datos del usuario.
 */
class DeleteAccountViewModel(
    private val deleteAccountUseCase: DeleteAccountUseCase
) : ViewModel() {

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _state = MutableStateFlow<DeleteAccountState>(DeleteAccountState.Idle)
    val state: StateFlow<DeleteAccountState> = _state.asStateFlow()

    fun setPassword(value: String) {
        _password.value = value
        if (_state.value is DeleteAccountState.Error) _state.value = DeleteAccountState.Idle
    }

    /** Borra la cuenta tras comprobar la contrasena. Ignora pulsaciones repetidas. */
    fun deleteAccount() {
        val current = _state.value
        if (current is DeleteAccountState.Deleting || current is DeleteAccountState.Deleted) return
        if (_password.value.isBlank()) {
            _state.value = DeleteAccountState.Error("Introduce tu contraseña.")
            return
        }
        _state.value = DeleteAccountState.Deleting
        viewModelScope.launch {
            _state.value = deleteAccountUseCase(_password.value).fold(
                onSuccess = { DeleteAccountState.Deleted },
                onFailure = { DeleteAccountState.Error(it.message ?: "No se pudo eliminar la cuenta.") }
            )
        }
    }
}
