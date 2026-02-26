package org.ivan.pokmanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import org.ivan.pokmanager.presentation.navigation.NavGraph
import org.ivan.pokmanager.presentation.ui.theme.PokéManagerTheme

/**
 * Actividad principal que inicializa la UI Compose y el grafo de navegacion.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PokéManagerTheme {
                val navController = rememberNavController()
                NavGraph(navController = navController)
            }
        }
    }
}
