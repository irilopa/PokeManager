package org.ivan.pokmanager.presentation.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.NavController
import org.ivan.pokmanager.presentation.ui.navigation.Screen

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun MenuAction(
    navController: NavController,
    title: String = "Menú de acciones",
    showBackButton: Boolean = false,
    currentRoute: String? = navController.currentDestination?.route
) {
    var expanded by remember { mutableStateOf(false) }

    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            if (showBackButton) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver"
                    )
                }
            }
        },
        actions = {
            IconButton(onClick = { expanded = true }) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menú"
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                // Opción: Listar Pokémon (solo si no estamos ya en esa pantalla)
                if (currentRoute != Screen.PokemonList.route) {
                    DropdownMenuItem(
                        text = { Text("Listar Pokémon") },
                        onClick = {
                            expanded = false
                            navController.navigate(Screen.PokemonList.route) {
                                // Si estamos en Login/Register, limpiamos el stack
                                if (currentRoute == Screen.Login.route ||
                                    currentRoute == Screen.Register.route
                                ) {
                                    popUpTo(Screen.PokemonList.route) { inclusive = true }
                                }
                            }
                        }
                    )
                    HorizontalDivider()
                }

                // Opción: Cerrar sesión (solo si estamos autenticados)
                if (currentRoute != Screen.Login.route &&
                    currentRoute != Screen.Register.route
                ) {
                    DropdownMenuItem(
                        text = {
                            Text("Cerrar sesión", color = MaterialTheme.colorScheme.error)
                        },
                        onClick = {
                            expanded = false
                            // Navega al Login y limpia toda la pila
                            navController.navigate(Screen.Login.route) {
                                popUpTo(0) { inclusive = true } // Limpia toda la navegación
                            }
                        }
                    )
                }
            }
        }
    )
}