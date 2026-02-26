package org.ivan.pokmanager.domain.usecase


import kotlinx.coroutines.flow.Flow
import org.ivan.pokmanager.domain.model.Pokemon
import org.ivan.pokmanager.domain.repository.PokemonFirestoreRepository

/**
 * Observa la lista de Pokemon del usuario.
 */
class GetPokemonUseCase(private val pokemonFirestoreRepository: PokemonFirestoreRepository) {
    /** Retorna un flujo con los Pokemon del usuario. */
    operator fun invoke(uid: String): Flow<List<Pokemon>> {
        return pokemonFirestoreRepository.list(uid)
    }
}
