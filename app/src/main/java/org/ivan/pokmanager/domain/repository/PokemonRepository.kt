package org.ivan.pokmanager.domain.repository

import kotlinx.coroutines.flow.Flow
import org.ivan.pokmanager.data.model.Pokemon
import org.ivan.pokmanager.domain.util.Result

/**
 * Interface for Pokemon repository.
 * Defines operations for managing Pokemon data using Firestore.
 */
interface PokemonRepository {
    /**
     * Get all Pokemon for the current user
     * @return Flow emitting Result with list of Pokemon
     */
    fun getPokemons(): Flow<Result<List<Pokemon>>>

    /**
     * Add a new Pokemon
     * @param pokemon Pokemon to add
     * @return Result indicating success or error
     */
    suspend fun addPokemon(pokemon: Pokemon): Result<Unit>

    /**
     * Update an existing Pokemon
     * @param pokemon Pokemon to update
     * @return Result indicating success or error
     */
    suspend fun updatePokemon(pokemon: Pokemon): Result<Unit>

    /**
     * Delete a Pokemon by Pokedex number
     * @param pokedexNumber Pokemon's Pokedex number
     * @return Result indicating success or error
     */
    suspend fun deletePokemon(pokedexNumber: Int): Result<Unit>

    /**
     * Get a Pokemon by Pokedex number
     * @param pokedexNumber Pokemon's Pokedex number
     * @return Result containing Pokemon or error
     */
    suspend fun getPokemon(pokedexNumber: Int): Result<Pokemon>
}
