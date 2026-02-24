package org.ivan.pokmanager.domain.usecase

import org.ivan.pokmanager.domain.repository.PokemonFirestoreRepository

class DeletePokemonUseCase(private val pokemonFirestoreRepository: PokemonFirestoreRepository) {
    suspend operator fun invoke(id: String, pokemonId: String): Boolean {
        return pokemonFirestoreRepository.delete(id, pokemonId)
    }
}

