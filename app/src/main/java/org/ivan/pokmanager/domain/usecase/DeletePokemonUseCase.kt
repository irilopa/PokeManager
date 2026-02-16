package org.ivan.pokmanager.domain.usecase

import org.ivan.pokmanager.domain.repository.PokemonRepository
import org.ivan.pokmanager.domain.util.Result

/**
 * Use case for deleting a Pokemon
 */
class DeletePokemonUseCase(private val pokemonRepository: PokemonRepository) {
    /**
     * Execute delete Pokemon by Pokedex number
     * @param pokedexNumber Pokemon's Pokedex number
     * @return Result indicating success or error
     */
    suspend operator fun invoke(pokedexNumber: Int): Result<Unit> {
        if (pokedexNumber <= 0) {
            return Result.Error(Exception("Número de Pokédex inválido"))
        }
        return pokemonRepository.deletePokemon(pokedexNumber)
    }
}
