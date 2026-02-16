package org.ivan.pokmanager.domain.usecase

import kotlinx.coroutines.flow.Flow
import org.ivan.pokmanager.data.model.Pokemon
import org.ivan.pokmanager.domain.repository.PokemonRepository
import org.ivan.pokmanager.domain.util.Result

/**
 * Use case for getting all Pokemon
 */
class GetPokemonsUseCase(private val pokemonRepository: PokemonRepository) {
    /**
     * Execute get all Pokemon
     * @return Flow emitting Result with list of Pokemon
     */
    operator fun invoke(): Flow<Result<List<Pokemon>>> {
        return pokemonRepository.getPokemons()
    }
}
