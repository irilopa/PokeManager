package org.ivan.pokmanager.data.model

/**
 * DTO de perfil de usuario almacenado en Firestore.
 */
data class UserDto(
    val name: String = "",
    val surname: String = "",
    val email: String = "",
    val birthDay: String = ""
)
