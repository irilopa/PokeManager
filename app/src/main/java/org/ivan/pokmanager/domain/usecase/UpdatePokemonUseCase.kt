package org.ivan.pokmanager.domain.usecase

import org.ivan.pokmanager.domain.model.Pokemon
import org.ivan.pokmanager.domain.repository.PokemonRepository

/**
 * Actualiza un Pokemon existente del equipo (mismo documento, mismo id).
 */
class UpdatePokemonUseCase(private val pokemonRepository: PokemonRepository) {
    suspend operator fun invoke(uid: String, pokemon: Pokemon): Result<Unit> {
        require(pokemon.id.isNotBlank()) { "El Pokémon a actualizar necesita id" }
        return pokemonRepository.update(uid, pokemon)
    }
}
