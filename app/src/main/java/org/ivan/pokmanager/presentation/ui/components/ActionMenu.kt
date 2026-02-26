package org.ivan.pokmanager.presentation.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import org.ivan.pokmanager.presentation.ui.theme.PokeColors

/**
 * TopAppBar con menu overflow para anadir Pokemon y cerrar sesion.
 *
 * @param showBackButton muestra la flecha de regreso si se necesita.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionMenu(
    title: String,
    modifier: Modifier = Modifier,
    showBackButton: Boolean = false,
    onBack: () -> Unit = {},
    onAddPokemon: () -> Unit,
    onLogout: () -> Unit,
    containerColor: Color = PokeColors.PokeRed
) {
    var expanded by remember { mutableStateOf(false) }

    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            if (showBackButton) {
                IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás") }
            }
        },
        actions = {
            IconButton(onClick = { expanded = !expanded }) {
                Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "Más opciones")
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Añadir Pokémon") },
                    leadingIcon = { Icon(Icons.Filled.Add, contentDescription = null) },
                    onClick = {
                        expanded = false
                        onAddPokemon()
                    }
                )

                DropdownMenuItem(
                    text = { Text("Cerrar sesión") },
                    leadingIcon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null) },
                    onClick = {
                        expanded = false
                        onLogout()
                    }
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = containerColor,
            titleContentColor = Color.White,
            navigationIconContentColor = Color.White
        ),
        modifier = modifier
    )
}
