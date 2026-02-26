package org.ivan.pokmanager.di

import org.ivan.pokmanager.data.model.repository.PokemonApiService
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Modulo Koin que provee Retrofit y el servicio de la PokeAPI.
 */
val retrofitModule = module {
    single {
        Retrofit.Builder()
            .baseUrl("https://pokeapi.co/api/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single { get<Retrofit>().create(PokemonApiService::class.java) }
}