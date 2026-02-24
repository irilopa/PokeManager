package org.ivan.pokmanager.di

import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val retrofitModule = module {
    // API retrofit
    single {
        Retrofit.Builder()
            // Se configura la URL del servicio REST
            // La IP 10.0.2.2 hace referencia la ip del host del emulador
            .baseUrl("http://10.0.2.2:8080")
            // Se configura la serialización con JSON
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}