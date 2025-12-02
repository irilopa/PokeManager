package org.ivan.pokmanager.presentation.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LoginScreenViewModel : ViewModel() {

    private val _username = MutableStateFlow("")
    private val _password = MutableStateFlow("")

    val username: StateFlow<String> = _username.asStateFlow()
    val password: StateFlow<String> = _password.asStateFlow()

    fun setUsername(user: String) {
        _username.value = user
    }

    fun setPassword(pass: String) {
        _password.value = pass
    }

    fun login() {
        val currentUser = _username.value
        val currentPass = _password.value

        println("Intentando iniciar sesión con: $currentUser")
    }

    fun clear() {
        _username.value = ""
        _password.value = ""
    }
}