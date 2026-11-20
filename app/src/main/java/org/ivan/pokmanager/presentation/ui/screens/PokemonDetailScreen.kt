package org.ivan.pokmanager.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.SearchOff
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
import org.ivan.pokmanager.presentation.state.UiState
import org.ivan.pokmanager.presentation.ui.components.ErrorContent
import org.ivan.pokmanager.presentation.ui.components.LoadingContent
import org.ivan.pokmanager.presentation.ui.components.MessageContent
import org.ivan.pokmanager.presentation.ui.theme.PokeColors
import org.ivan.pokmanager.presentation.viewmodel.PokemonDetailViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

/**
 * Pantalla que muestra el detalle del Pokemon seleccionado.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonDetailScreen(navController: NavController, pokemonId: String) {
    val viewModel: PokemonDetailViewModel = koinViewModel(parameters = { parametersOf(pokemonId) })
    val uiState by viewModel.uiState.collectAsState()
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
        val contentModifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(innerPadding)

        when (val state = uiState) {
            UiState.Loading -> LoadingContent(contentModifier)

            is UiState.Error -> ErrorContent(
                message = state.message,
                onRetry = viewModel::retry,
                modifier = contentModifier
            )

            is UiState.Success -> {
                val pokemon = state.data
                if (pokemon == null) {
                    MessageContent(
                        icon = Icons.Default.SearchOff,
                        message = "Este Pokémon ya no está en tu equipo.",
                        actionLabel = "Volver al equipo",
                        onAction = { navController.navigateUp() },
                        modifier = contentModifier
                    )
                } else {
                    Column(
                        modifier = contentModifier
                            .verticalScroll(rememberScrollState())
                            .padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        PokemonDetailContent(pokemon = pokemon)
                    }
                }
            }
        }
    }
}

@Composable
private fun PokemonDetailContent(pokemon: Pokemon) {
    if (pokemon.imageUrl.isNotBlank()) {
        AsyncImage(
            model = pokemon.imageUrl,
            contentDescription = "Imagen de ${pokemon.name}",
            modifier = Modifier.size(160.dp)
        )
    }

    Text(pokemon.name, style = MaterialTheme.typography.titleLarge)
    val header = buildList {
        if (pokemon.pokedexNumber > 0) add("#${pokemon.pokedexNumber}")
        if (pokemon.level > 0) add("Nv. ${pokemon.level}")
        add(pokemon.types)
    }.joinToString(" · ")
    Text(header, style = MaterialTheme.typography.bodyMedium)

    if (pokemon.description.isNotBlank()) {
        Text(pokemon.description, style = MaterialTheme.typography.bodyMedium)
    }

    if (pokemon.heightM > 0 || pokemon.weightKg > 0) {
        Text("Altura: ${pokemon.heightM} m · Peso: ${pokemon.weightKg} kg", style = MaterialTheme.typography.bodyMedium)
    }
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
