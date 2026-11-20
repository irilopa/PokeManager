package org.ivan.pokmanager.domain.usecase

import kotlinx.coroutines.flow.Flow
import org.ivan.pokmanager.domain.model.Pokemon
import org.ivan.pokmanager.domain.repository.PokemonRepository

/**
 * Observa la lista de Pokemon del usuario.
 */
class GetPokemonUseCase(private val pokemonRepository: PokemonRepository) {
    operator fun invoke(uid: String): Flow<List<Pokemon>> = pokemonRepository.list(uid)
}
