package org.ivan.pokmanager.domain.usecase


import kotlinx.coroutines.flow.Flow
import org.ivan.pokmanager.domain.model.Pokemon
import org.ivan.pokmanager.domain.repository.PokemonFirestoreRepository

class GetPokemonUseCase(private val pokemonFirestoreRepository: PokemonFirestoreRepository) {
    operator fun invoke(): Flow<List<Pokemon>> {
        return pokemonFirestoreRepository.list()
    }
}


