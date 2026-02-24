package org.ivan.pokmanager.domain.usecase

import org.ivan.pokmanager.domain.model.Pokemon
import org.ivan.pokmanager.domain.repository.PokemonFirestoreRepository

class SavePokemonUseCase(private val pokemonFirestoreRepository: PokemonFirestoreRepository) {
    suspend operator fun invoke(uid: String, pokemon: Pokemon): Boolean {
        return pokemonFirestoreRepository.save(uid, pokemon)
    }
}

