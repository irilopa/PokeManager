package org.ivan.pokmanager.domain.repository

import kotlinx.coroutines.flow.Flow
import org.ivan.pokmanager.domain.model.Pokemon

/**
 * Acceso al equipo Pokemon de un usuario.
 */
interface PokemonRepository {
    /** Observa los Pokemon del usuario. El flujo falla si la fuente de datos falla. */
    fun list(uid: String): Flow<List<Pokemon>>

    /** Obtiene un Pokemon por el id de su documento. `null` dentro de success = no existe. */
    suspend fun getById(uid: String, id: String): Result<Pokemon?>

    /** Guarda un Pokemon nuevo. */
    suspend fun save(uid: String, pokemon: Pokemon): Result<Unit>

    /** Elimina un Pokemon por el id de su documento. */
    suspend fun delete(uid: String, id: String): Result<Unit>
}
