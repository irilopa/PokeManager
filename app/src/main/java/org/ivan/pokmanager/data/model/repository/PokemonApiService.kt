package org.ivan.pokmanager.data.model.repository

import retrofit2.http.GET
import retrofit2.http.Path

interface PokemonApiService {
    @GET("pokemon/{id}")
    suspend fun getPokemonStats(@Path("id") id: Int): PokemonResponse
}