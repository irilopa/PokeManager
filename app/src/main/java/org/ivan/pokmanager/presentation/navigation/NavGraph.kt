package org.ivan.pokmanager.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import org.ivan.pokmanager.presentation.ui.screens.AddPokemonScreen
import org.ivan.pokmanager.presentation.ui.screens.DeleteAccountScreen
import org.ivan.pokmanager.presentation.ui.screens.ForgotPasswordScreen
import org.ivan.pokmanager.presentation.ui.screens.LoginScreen
import org.ivan.pokmanager.presentation.ui.screens.PokemonDetailScreen
import org.ivan.pokmanager.presentation.ui.screens.PokemonListScreen
import org.ivan.pokmanager.presentation.ui.screens.RegisterScreen


@Composable
fun NavGraph(navController: NavHostController, isLoggedIn: Boolean) {
    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) Screen.PokemonList.route else Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(navController)
        }
        composable(Screen.Register.route) {
            RegisterScreen(navController)
        }
        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(navController)
        }
        composable(Screen.PokemonList.route) {
            PokemonListScreen(navController)
        }
        composable(Screen.AddPokemon.route) {
            AddPokemonScreen(navController)
        }
        composable(Screen.EditPokemon.route) { backStackEntry ->
            val pokemonId = backStackEntry.arguments?.getString("pokemonId").orEmpty()
            AddPokemonScreen(navController, pokemonId = pokemonId)
        }
        composable(Screen.DeleteAccount.route) {
            DeleteAccountScreen(navController)
        }
        composable(Screen.PokemonDetail.route) { backStackEntry ->
            val pokemonId = backStackEntry.arguments?.getString("pokemonId").orEmpty()
            PokemonDetailScreen(navController, pokemonId)
        }
    }
}
