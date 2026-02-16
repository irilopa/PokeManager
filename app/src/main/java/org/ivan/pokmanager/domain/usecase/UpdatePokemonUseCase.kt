package org.ivan.pokmanager.domain.usecase

import org.ivan.pokmanager.data.model.Pokemon
import org.ivan.pokmanager.domain.repository.PokemonRepository
import org.ivan.pokmanager.domain.util.Result

/**
 * Use case for updating an existing Pokemon
 */
class UpdatePokemonUseCase(private val pokemonRepository: PokemonRepository) {
    /**
     * Execute update Pokemon with validation
     * @param pokemon Pokemon to update
     * @return Result indicating success or error
     */
    suspend operator fun invoke(pokemon: Pokemon): Result<Unit> {
        if (pokemon.name.isBlank()) {
            return Result.Error(Exception("El nombre del Pokémon no puede estar vacío"))
        }
        if (pokemon.types.isBlank()) {
            return Result.Error(Exception("El tipo del Pokémon no puede estar vacío"))
        }
        if (pokemon.pokedexNumber <= 0) {
            return Result.Error(Exception("El número de Pokédex debe ser mayor a 0"))
        }
        return pokemonRepository.updatePokemon(pokemon)
    }
}
