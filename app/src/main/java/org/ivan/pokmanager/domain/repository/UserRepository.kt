package org.ivan.pokmanager.domain.repository

import org.ivan.pokmanager.data.model.UserDto

/**
 * Perfil del usuario.
 */
interface UserRepository {
    /** Guarda el perfil del usuario bajo su uid. */
    suspend fun saveUserData(uid: String, user: UserDto)

    /** Borra el perfil del usuario. */
    suspend fun deleteUserData(uid: String): Result<Unit>
}
