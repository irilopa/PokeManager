package org.ivan.pokmanager

import android.app.Application
import org.ivan.pokmanager.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class PokemonApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@PokemonApp)
            modules(appModule)
        }
    }
}