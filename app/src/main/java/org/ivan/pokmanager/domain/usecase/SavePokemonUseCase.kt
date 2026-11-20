package org.ivan.pokmanager.domain.usecase

import org.ivan.pokmanager.domain.model.Pokemon
import org.ivan.pokmanager.domain.repository.PokemonRepository

/**
 * Guarda un Pokemon en el equipo del usuario.
 */
class SavePokemonUseCase(private val pokemonRepository: PokemonRepository) {
    suspend operator fun invoke(uid: String, pokemon: Pokemon): Result<Unit> =
        pokemonRepository.save(uid, pokemon)
}
