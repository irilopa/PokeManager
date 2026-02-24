package org.ivan.pokmanager.di

import org.ivan.pokmanager.data.model.repository.PokemonApiService
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val retrofitModule = module {
    // API retrofit
    single {
        Retrofit.Builder()
            // Se configura la URL del servicio REST
            .baseUrl("https://pokeapi.co/api/v2/")
            // Se configura la serialización con JSON
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Servicio Retrofit para la PokeAPI
    single { get<Retrofit>().create(PokemonApiService::class.java) }
}