package org.ivan.pokmanager.presentation.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class RegisterScreenViewModel : ViewModel() {
     private val _name = MutableStateFlow("")
    private val _email = MutableStateFlow("")
    private val _birthdate = MutableStateFlow("")
    private val _password = MutableStateFlow("")


    val name: StateFlow<String> = _name.asStateFlow()
    val email: StateFlow<String> = _email.asStateFlow()
    val birthdate: StateFlow<String> = _birthdate.asStateFlow()
    val password: StateFlow<String> = _password.asStateFlow()

    fun setName(name: String) {
        _name.value = name
    }

    fun setEmail(email: String) {
        _email.value = email
    }

    fun setBirthdate(birthdate: String) {
        _birthdate.value = birthdate
    }

    fun setPassword(password: String) {
        _password.value = password
    }

    // --- 4. Lógica de Negocio ---

    fun registerUser() {
        // Aquí obtienes los valores actuales de los flujos
        val currentName = _name.value
        val currentEmail = _email.value
        val currentBirthdate = _birthdate.value
        val currentPassword = _password.value

        // TODO: Aquí conectarás con tu base de datos (MySQL o PostgreSQL)
        // Por ahora, simulamos el registro imprimiendo en consola
        println("--- Iniciando Registro ---")
        println("Nombre: $currentName")
        println("Email: $currentEmail")
        println("F. Nacimiento: $currentBirthdate")
        println("Password: $currentPassword") // (Solo para debug, nunca imprimir en prod)
        println("--- Guardando en Base de Datos... ---")
    }
}