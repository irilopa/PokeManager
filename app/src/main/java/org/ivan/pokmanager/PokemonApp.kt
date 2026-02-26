package org.ivan.pokmanager

import android.app.Application
import org.ivan.pokmanager.di.appModule
import org.ivan.pokmanager.di.retrofitModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

/**
 * Application que inicializa Koin con los modulos del proyecto.
 */
class PokemonApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@PokemonApp)
            modules(
                appModule,
                retrofitModule
            )
        }
    }
}