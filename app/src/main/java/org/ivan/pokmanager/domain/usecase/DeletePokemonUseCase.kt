package org.ivan.pokmanager.domain.usecase

import org.ivan.pokmanager.domain.repository.PokemonRepository

/**
 * Elimina un Pokemon del equipo del usuario.
 */
class DeletePokemonUseCase(private val pokemonRepository: PokemonRepository) {
    suspend operator fun invoke(uid: String, pokemonId: String): Result<Unit> =
        pokemonRepository.delete(uid, pokemonId)
}
