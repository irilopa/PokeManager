package org.ivan.pokmanager.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import org.ivan.pokmanager.domain.model.Pokemon
import org.ivan.pokmanager.presentation.ui.theme.PokeColors
import org.ivan.pokmanager.presentation.viewmodel.PokemonDetailViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonDetailScreen(navController: NavController, pokemonId: Int) {
    val viewModel: PokemonDetailViewModel = koinViewModel(parameters = { parametersOf(pokemonId) })
    val pokemon by viewModel.pokemon.collectAsState()
    val primaryColor = PokeColors.PokeRed

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle Pokémon") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = primaryColor,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            PokemonDetailContent(pokemon = pokemon)

        }
    }
}

@Composable
private fun PokemonDetailContent(pokemon: Pokemon?) {
    if (pokemon == null) {
        Text("No se encontro el Pokemon seleccionado.", style = MaterialTheme.typography.titleMedium)
        return
    }

    if (pokemon.imageUrl.isNotBlank()) {
        AsyncImage(
            model = pokemon.imageUrl,
            contentDescription = "Imagen de ${pokemon.name}",
            modifier = Modifier.size(160.dp)
        )
    }

    Text(pokemon.name, style = MaterialTheme.typography.titleLarge)
    Text("#${pokemon.pokedexNumber} · ${pokemon.types}", style = MaterialTheme.typography.bodyMedium)

    if (pokemon.description.isNotBlank()) {
        Text(pokemon.description, style = MaterialTheme.typography.bodyMedium)
    }

    Text("Altura: ${pokemon.heightM} m · Peso: ${pokemon.weightKg} kg", style = MaterialTheme.typography.bodyMedium)
    Text(
        "HP: ${pokemon.stats.hp} | Atk: ${pokemon.stats.attack} | Def: ${pokemon.stats.defense}",
        style = MaterialTheme.typography.bodySmall
    )
    Text(
        "SpA: ${pokemon.stats.specialAttack} | SpD: ${pokemon.stats.specialDefense} | Vel: ${pokemon.stats.speed}",
        style = MaterialTheme.typography.bodySmall
    )

    if (pokemon.moves.isNotEmpty()) {
        Text("Movimientos: ${pokemon.moves.joinToString()}", style = MaterialTheme.typography.bodySmall)
    }
}

@Preview(showBackground = true)
@Composable
fun PokemonDetailScreenPreview() {
    PokemonDetailContent(
        pokemon = Pokemon(
            pokedexNumber = 1,
            name = "Bulbasaur",
            types = "Planta / Veneno",
            description = "Una semilla nace en su lomo.",
            heightM = 0.7,
            weightKg = 6.9,
            imageUrl = "",
            moves = listOf("Placaje", "Látigo cepa")
        )
    )
}
