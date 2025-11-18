package org.ivan.pokmanager.presentation.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class RegisterScreenViewModel : ViewModel() {
    private val _name = MutableStateFlow("")
    val username: StateFlow<String> = _name

    private val _suname = MutableStateFlow("")
    val password: StateFlow<String> = _suname

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email
    private val _birthdate = MutableStateFlow("")
    val birthdate: StateFlow<String> = _birthdate

    fun setName(name: String) {
        _name.value = name
    }

    fun setSuname(suname: String) {
        _suname.value = suname
    }
    /*
    fun setUsername(username: String) {
        _username.value = username
    }

    fun setPassword(password: String) {
        _password.value = password
    }
*/

}