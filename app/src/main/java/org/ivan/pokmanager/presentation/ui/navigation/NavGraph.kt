package org.ivan.pokmanager.presentation.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.ivan.pokmanager.presentation.ui.screens.AddPokemonScreen
import org.ivan.pokmanager.presentation.ui.screens.LoginScreen
import org.ivan.pokmanager.presentation.ui.screens.PokemonDetailScreen
import org.ivan.pokmanager.presentation.ui.screens.PokemonListScreen
import org.ivan.pokmanager.presentation.ui.screens.RegisterScreen
/*
@Composable
fun NavGraph(startDestination: String = Screen.Login.route) {
    // Cargamos el navController

    val navController = rememberNavController()

    // Creamos un NavHost que arranque con la pantalla de inicio
    NavHost(navController = navController, startDestination = startDestination) {


        composable(Screen.Login.route) {
            LoginScreen(navController)
        }
        composable(Screen.Register.route) {
            RegisterScreen(navController)
        }
        composable(Screen.PokemonList.route) {
            PokemonListScreen(navController)
        }
        composable(Screen.AddPokemon.route) {
            AddPokemonScreen(navController)
        }
        composable(Screen.PokemonDetail.route) {
            PokemonDetailScreen(navController)
        }
    }
}
*/
