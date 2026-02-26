package org.ivan.pokmanager.presentation.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import org.ivan.pokmanager.domain.model.Pokemon
import org.ivan.pokmanager.presentation.navigation.Screen
import org.ivan.pokmanager.presentation.ui.components.ActionMenu
import org.ivan.pokmanager.presentation.ui.theme.PokeColors
import org.ivan.pokmanager.presentation.viewmodel.PokemonListViewModel
import org.koin.androidx.compose.koinViewModel

/**
 * Pantalla que muestra el equipo Pokemon del usuario y permite navegar al detalle.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonListScreen(
    navController: NavController,
    viewModel: PokemonListViewModel = koinViewModel()
) {

    val pokemons by viewModel.pokemons.collectAsState()
    val primaryColor = PokeColors.PokeRed
    Scaffold(

        topBar = {
            ActionMenu(
                title = "Mi Equipo Pokémon",
                onAddPokemon = {
                    navController.navigate(Screen.AddPokemon.route)
                },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.AddPokemon.route) },
                containerColor = primaryColor,
                contentColor = Color.White
            ) {
                Text("+")
            }
        },
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color.White)
                .padding(horizontal = 32.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(pokemons, key = { it.id.ifBlank { it.pokedexNumber.toString() } }) { pokemon ->
                    PokemonCard(pokemon, viewModel, primaryColor) {
                        navController.navigate(Screen.PokemonDetail.createScreen(pokemon.pokedexNumber))
                    }
                }
            }
        }
    }
}

/**
 * Tarjeta interactiva que resume un Pokemon y permite eliminarlo.
 */
@Composable
fun PokemonCard(
    pokemon: Pokemon,
    viewModel: PokemonListViewModel,
    accentColor: Color,
    onClick: () -> Unit = {}
) {
    var showDialog by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (pokemon.imageUrl.isNotBlank()) {
                AsyncImage(
                    model = pokemon.imageUrl,
                    contentDescription = "Imagen de ${pokemon.name}",
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
                Spacer(modifier = Modifier.size(12.dp))
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = pokemon.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = pokemon.types,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                Text(
                    text = "HP: ${pokemon.stats.hp} | Atk: ${pokemon.stats.attack}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray.copy(alpha = 0.9f)
                )
            }
            IconButton(onClick = { showDialog = true }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar Pokémon",
                    tint = accentColor
                )
            }

            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text("Liberar Pokemon") },
                    text = { Text("¿Estás seguro que deseas liberar al pokemon?") },
                    confirmButton = {
                        Button(onClick = {
                            showDialog = false

                            viewModel.removePokemon(pokemon)
                        }) {
                            Text("Aceptar")
                        }
                    },
                    dismissButton = {
                        Button(onClick = { showDialog = false }) { Text("Cancelar") }
                    }
                )
            }


        }
    }
}


@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun PokemonListScreenPreview() {
    PokemonListScreen(
        navController = rememberNavController()
    )
}