package org.ivan.pokmanager.presentation.navigation

/**
 * Rutas de navegacion usadas por el NavHost.
 */
sealed class Screen(val route: String) {

    data object Login : Screen("login")

    data object Register : Screen("register")

    data object PokemonList : Screen("pokemonList")

    data object AddPokemon : Screen("addPokemon")

    data object PokemonDetail : Screen("pokemonDetail/{pokemonId}") {
        /**
         * Construye la ruta de detalle con el id de la Pokedex.
         */
        fun createScreen(pokemonId: Int) = "pokemonDetail/$pokemonId"
    }
}