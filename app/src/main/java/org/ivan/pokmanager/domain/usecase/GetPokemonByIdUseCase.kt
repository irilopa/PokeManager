package org.ivan.pokmanager.domain.usecase

import org.ivan.pokmanager.domain.model.Pokemon
import org.ivan.pokmanager.domain.repository.PokemonRepository

/**
 * Obtiene un Pokemon concreto del equipo por el id de su documento en Firestore.
 */
class GetPokemonByIdUseCase(private val pokemonRepository: PokemonRepository) {
    suspend operator fun invoke(uid: String, id: String): Result<Pokemon?> =
        pokemonRepository.getById(uid, id)
}
