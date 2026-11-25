package org.ivan.pokmanager.di

import org.ivan.pokmanager.data.model.repository.PokemonApiService
import okhttp3.OkHttpClient
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Modulo Koin que provee Retrofit y el servicio de la PokeAPI.
 */
val retrofitModule = module {
    single {
        // Tiempos de espera explícitos: con mala cobertura la búsqueda falla con un mensaje
        // claro en lugar de quedarse cargando.
        OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .callTimeout(20, TimeUnit.SECONDS)
            .build()
    }

    single {
        Retrofit.Builder()
            .baseUrl("https://pokeapi.co/api/v2/")
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single { get<Retrofit>().create(PokemonApiService::class.java) }
}