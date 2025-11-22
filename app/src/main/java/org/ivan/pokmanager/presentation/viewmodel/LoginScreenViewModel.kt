package org.ivan.pokmanager.presentation.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LoginScreenViewModel : ViewModel() {

    // --- Estado Interno ---
    private val _username = MutableStateFlow("")
    private val _password = MutableStateFlow("")

    // --- Estado Público ---
    val username: StateFlow<String> = _username.asStateFlow()
    val password: StateFlow<String> = _password.asStateFlow()

    // --- Setters ---
    fun setUsername(user: String) {
        _username.value = user
    }

    fun setPassword(pass: String) {
        _password.value = pass
    }

    // --- Funciones ---

    fun login() {
        val currentUser = _username.value
        val currentPass = _password.value

        // TODO: Conectar con base de datos (MySQL/PostgreSQL) para verificar credenciales
        println("Intentando iniciar sesión con: $currentUser")
    }

    fun clear() {
        _username.value = ""
        _password.value = ""
    }
}