package org.ivan.pokmanager.domain.usecase

import org.ivan.pokmanager.domain.repository.PokemonFirestoreRepository

/**
 * Elimina un Pokemon del equipo del usuario.
 */
class DeletePokemonUseCase(private val pokemonFirestoreRepository: PokemonFirestoreRepository) {
    /** Ejecuta la eliminacion por uid y id del Pokemon. */
    suspend operator fun invoke(id: String, pokemonId: String): Boolean {
        return pokemonFirestoreRepository.delete(id, pokemonId)
    }
}
