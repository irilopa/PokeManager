package org.ivan.pokmanager.presentation.ui.navigation

// Dentro de la sealed class definimos un object por cada ruta existenten
sealed class Screen(val route: String) {
    // Definimos la pantalla Home en la ruta home
    data object Home : Screen("home")

    //  Definimos la pantalla Login en la ruta login
    data object Login : Screen("login")
}