package org.ivan.pokmanager.domain.repository

/**
 * Operaciones de autenticacion. Devuelve uids para no acoplar el dominio a Firebase.
 */
interface AuthRepository {
    /** Crea una cuenta y devuelve el uid del usuario. */
    suspend fun register(email: String, password: String): Result<String>

    /** Inicia sesion y devuelve el uid del usuario. */
    suspend fun login(email: String, password: String): Result<String>

    /** Cierra la sesion actual. */
    fun logout()

    /** Uid del usuario autenticado, o null si no hay sesion. */
    fun currentUserId(): String?
}
