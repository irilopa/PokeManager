package org.ivan.pokmanager.presentation.ui.navigation

// Dentro de la sealed class definimos un object por cada ruta existenten
sealed class Screen(val route: String) {

    data object Login : Screen("login")

    data object Register : Screen("register")

    data object PokemonList : Screen("pokemonList")

    data object AddPokemon : Screen("addPokemon")

    data object PokemonDetail : Screen("pokemonDetail/{pokemonId}") {
        fun createScreen(pokemonId: Int) = "pokemonDetail/$pokemonId"
    }
}