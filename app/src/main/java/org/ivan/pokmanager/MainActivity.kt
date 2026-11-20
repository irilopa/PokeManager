package org.ivan.pokmanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import org.ivan.pokmanager.domain.repository.AuthRepository
import org.ivan.pokmanager.presentation.navigation.NavGraph
import org.koin.android.ext.android.inject
import org.ivan.pokmanager.presentation.ui.theme.PokéManagerTheme

/**
 * Actividad principal que inicializa la UI Compose y el grafo de navegacion.
 */
class MainActivity : ComponentActivity() {
    private val authRepository: AuthRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Si ya hay sesion de Firebase se entra directamente al equipo.
        val isLoggedIn = authRepository.currentUserId() != null
        setContent {
            PokéManagerTheme {
                val navController = rememberNavController()
                NavGraph(navController = navController, isLoggedIn = isLoggedIn)
            }
        }
    }
}
