package org.ivan.pokmanager.fakes

import org.ivan.pokmanager.data.model.remote.dto.PokemonResponse
import org.ivan.pokmanager.data.model.repository.PokemonApiService

/** PokeAPI falsa: devuelve las respuestas registradas o falla. */
class FakePokemonApiService(
    private val responses: Map<String, PokemonResponse> = emptyMap()
) : PokemonApiService {
    override suspend fun getPokemonByName(name: String): PokemonResponse =
        responses[name] ?: throw IllegalArgumentException("404")
}
