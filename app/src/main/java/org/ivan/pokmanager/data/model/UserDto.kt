package org.ivan.pokmanager.data.model

/**
 * DTO de perfil de usuario almacenado en Firestore.
 */
data class UserDto(
    val name: String = "",
    val email: String = ""
)
