package org.ivan.pokmanager.presentation.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import org.ivan.pokmanager.presentation.ui.screens.AddPokemonScreen
import org.ivan.pokmanager.presentation.ui.screens.LoginScreen
import org.ivan.pokmanager.presentation.ui.screens.PokemonListScreen
import org.ivan.pokmanager.presentation.ui.screens.RegisterScreen


@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Login.route
) {
    NavHost(navController = navController, startDestination = startDestination) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.PokemonList.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onRegisterClick = { navController.navigate(Screen.Register.route) }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(onBackClick = { navController.popBackStack() })
        }

        composable(Screen.PokemonList.route) {
            PokemonListScreen(
                onBackClick = { /* no-op o logout */ },
                onAddPokemonClick = { navController.navigate(Screen.AddPokemon.route) }
            )
        }

        composable(Screen.AddPokemon.route) {
            AddPokemonScreen(onBackClick = { navController.popBackStack() })
        }
        // composable(Screen.EditPokemon.route) { backStackEntry ->
        //     val pokemonId = backStackEntry.arguments?.getString("pokemonId")?.toIntOrNull()
        //     if (pokemonId != null) {
        //         AddPokemonScreen(
        //             onBackClick = { navController.popBackStack() }
        //             // pasa pokemonId al VM o estado para editar
        //         )
        //     }
        // }
    }
}
