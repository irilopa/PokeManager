package org.ivan.pokmanager.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.ivan.pokmanager.data.model.UserDto
import org.ivan.pokmanager.domain.repository.AuthRepository
import org.ivan.pokmanager.domain.repository.UserRepository

/**
 * Maneja el estado y el flujo de registro con Firebase Auth y Firestore.
 */
class RegisterScreenViewModel(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _name = MutableStateFlow("")
    private val _email = MutableStateFlow("")
    private val _birthdate = MutableStateFlow("")
    private val _password = MutableStateFlow("")

    private val _isLoading = MutableStateFlow(false)
    private val _message = MutableStateFlow<String?>(null)
    private val _registerSuccess = MutableStateFlow(false)

    /** Nombre ingresado por el usuario. */
    val name: StateFlow<String> = _name.asStateFlow()
    /** Email ingresado por el usuario. */
    val email: StateFlow<String> = _email.asStateFlow()
    /** Fecha de nacimiento ingresada por el usuario. */
    val birthdate: StateFlow<String> = _birthdate.asStateFlow()
    /** Contrasena ingresada por el usuario. */
    val password: StateFlow<String> = _password.asStateFlow()
    /** Indica si el registro esta en curso. */
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    /** Mensaje de error o advertencia del proceso de registro. */
    val message: StateFlow<String?> = _message.asStateFlow()
    /** Indica si el registro finalizo con exito. */
    val registerSuccess: StateFlow<Boolean> = _registerSuccess.asStateFlow()

    fun setName(name: String) { _name.value = name }
    fun setEmail(email: String) { _email.value = email }
    fun setBirthdate(birthdate: String) { _birthdate.value = birthdate }
    fun setPassword(password: String) { _password.value = password }
    /** Limpia el mensaje mostrado por la UI. */
    fun clearMessage() { _message.value = null }

    /**
     * Crea la cuenta en Auth y guarda los datos de perfil en Firestore.
     */
    fun registerUser() {
        val currentEmail = _email.value.trim()
        val currentPassword = _password.value
        val currentName = _name.value.trim()
        val currentBirthdate = _birthdate.value.trim()

        _isLoading.value = true
        _message.value = null

        viewModelScope.launch {
            val authResult = authRepository.register(currentEmail, currentPassword)

            authResult
                .onSuccess { firebaseUser ->
                    val userDto = UserDto(
                        name = currentName,
                        email = currentEmail,
                        birthDay = currentBirthdate
                    )
                    runCatching { userRepository.saveUserData(firebaseUser.uid, userDto) }
                        .onFailure {
                            _message.value = "Cuenta creada, pero no se pudieron guardar los datos de perfil."
                        }

                    _registerSuccess.value = true
                }
                .onFailure { error ->
                    _message.value = when {
                        error.message?.contains("email address is already in use") == true ->
                            "Este email ya está registrado"
                        error.message?.contains("badly formatted") == true ->
                            "El email no tiene un formato válido"
                        error.message?.contains("weak-password") == true ->
                            "La contraseña es demasiado débil"
                        else -> error.message ?: "Error al registrarse. Inténtalo de nuevo."
                    }
                }

            _isLoading.value = false
        }
    }
}