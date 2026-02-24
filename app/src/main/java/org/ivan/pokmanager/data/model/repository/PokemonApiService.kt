package org.ivan.pokmanager.data.model.repository

import org.ivan.pokmanager.data.model.remote.dto.PokemonResponse
import retrofit2.http.GET
import retrofit2.http.Path

@Suppress("unused")
interface PokemonApiService {
    @GET("pokemon/{name}")
    suspend fun getPokemonByName(
        @Path("name") name: String
    ): PokemonResponse
}