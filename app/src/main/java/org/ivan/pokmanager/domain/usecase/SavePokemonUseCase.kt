package org.ivan.pokmanager.domain.usecase

import org.ivan.pokmanager.domain.model.Pokemon
import org.ivan.pokmanager.domain.repository.PokemonFirestoreRepository

/**
 * Guarda un Pokemon en el equipo del usuario.
 */
class SavePokemonUseCase(private val pokemonFirestoreRepository: PokemonFirestoreRepository) {
    /** Ejecuta el guardado por uid. */
    suspend operator fun invoke(uid: String, pokemon: Pokemon): Boolean {
        return pokemonFirestoreRepository.save(uid, pokemon)
    }
}
