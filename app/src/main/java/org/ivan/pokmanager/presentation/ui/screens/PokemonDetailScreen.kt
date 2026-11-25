package org.ivan.pokmanager.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CatchingPokemon
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import org.ivan.pokmanager.domain.model.Pokemon
import org.ivan.pokmanager.domain.model.PokemonStats
import org.ivan.pokmanager.domain.type.PokemonType
import org.ivan.pokmanager.domain.type.TypeChart
import org.ivan.pokmanager.domain.type.TypeMatchup
import org.ivan.pokmanager.presentation.navigation.Screen
import org.ivan.pokmanager.presentation.state.UiState
import org.ivan.pokmanager.presentation.ui.components.ErrorContent
import org.ivan.pokmanager.presentation.ui.components.LoadingContent
import org.ivan.pokmanager.presentation.ui.components.MessageContent
import org.ivan.pokmanager.presentation.ui.components.StatBar
import org.ivan.pokmanager.presentation.ui.components.TypeChip
import org.ivan.pokmanager.presentation.ui.theme.PokeColors
import org.ivan.pokmanager.presentation.ui.theme.TypeColors
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

    // Al volver de editar, el formulario deja una marca para recargar el detalle.
    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
    val updated by (savedStateHandle?.getStateFlow(POKEMON_UPDATED_KEY, false)
        ?: kotlinx.coroutines.flow.MutableStateFlow(false)).collectAsState()
    LaunchedEffect(updated) {
        if (updated) {
            viewModel.retry()
            savedStateHandle?.set(POKEMON_UPDATED_KEY, false)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle Pokémon") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    if ((uiState as? UiState.Success)?.data != null) {
                        IconButton(onClick = { navController.navigate(Screen.EditPokemon.createScreen(pokemonId)) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PokeColors.PokeRed,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
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
                    PokemonDetailContent(pokemon = pokemon, modifier = contentModifier)
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PokemonDetailContent(pokemon: Pokemon, modifier: Modifier = Modifier) {
    val types = PokemonType.parseList(pokemon.types)
    val mainColor = types.firstOrNull()?.let { TypeColors.background(it) } ?: PokeColors.PokeRed

    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Cabecera con el color del tipo principal
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(mainColor.copy(alpha = 0.25f))
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            if (pokemon.imageUrl.isNotBlank()) {
                AsyncImage(
                    model = pokemon.imageUrl,
                    contentDescription = "Imagen de ${pokemon.name}",
                    modifier = Modifier.size(180.dp)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.7f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.CatchingPokemon,
                        contentDescription = null,
                        tint = mainColor,
                        modifier = Modifier.size(88.dp)
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(pokemon.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                val header = buildList {
                    if (pokemon.pokedexNumber > 0) add("#${pokemon.pokedexNumber.toString().padStart(3, '0')}")
                    if (pokemon.level > 0) add("Nivel ${pokemon.level}")
                }.joinToString(" · ")
                if (header.isNotEmpty()) {
                    Text(header, style = MaterialTheme.typography.titleMedium, color = Color.Gray)
                }
            }

            if (types.isNotEmpty()) {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    types.forEach { TypeChip(it) }
                }
            } else if (pokemon.types.isNotBlank()) {
                Text(pokemon.types, style = MaterialTheme.typography.bodyMedium)
            }

            if (pokemon.description.isNotBlank()) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F7F7)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Notas del entrenador", style = MaterialTheme.typography.labelLarge, color = Color.Gray)
                        Text(pokemon.description, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            if (pokemon.heightM > 0 || pokemon.weightKg > 0) {
                Text("Altura: ${pokemon.heightM} m · Peso: ${pokemon.weightKg} kg", style = MaterialTheme.typography.bodyMedium)
            }

            HorizontalDivider()
            StatsSection(pokemon.stats)

            if (types.isNotEmpty()) {
                HorizontalDivider()
                DefenseSection(types)
            }

            if (pokemon.moves.isNotEmpty()) {
                HorizontalDivider()
                SectionTitle("Movimientos")
                Text(pokemon.moves.joinToString(), style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
}

@Composable
private fun StatsSection(stats: PokemonStats) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SectionTitle("Estadísticas base")
        StatBar("PS", stats.hp)
        StatBar("Ataque", stats.attack)
        StatBar("Defensa", stats.defense)
        StatBar("At. Esp.", stats.specialAttack)
        StatBar("Def. Esp.", stats.specialDefense)
        StatBar("Velocidad", stats.speed)
        val total = stats.hp + stats.attack + stats.defense +
            stats.specialAttack + stats.specialDefense + stats.speed
        Text("Total: $total", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DefenseSection(types: List<PokemonType>) {
    val profile = TypeChart.defensiveProfile(types)
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionTitle("Debilidades y resistencias")
        MatchupGroup("Débil a", profile.weaknesses)
        MatchupGroup("Resistente a", profile.resistances)
        if (profile.immunities.isNotEmpty()) {
            Text("Inmune a", style = MaterialTheme.typography.labelLarge, color = Color.Gray)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                profile.immunities.forEach { TypeChip(it, suffix = "×0") }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MatchupGroup(title: String, matchups: List<TypeMatchup>) {
    if (matchups.isEmpty()) return
    Text(title, style = MaterialTheme.typography.labelLarge, color = Color.Gray)
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        matchups.forEach { TypeChip(it.type, suffix = formatMultiplier(it.multiplier)) }
    }
}

private fun formatMultiplier(value: Double): String = when (value) {
    4.0 -> "×4"
    2.0 -> "×2"
    0.5 -> "×½"
    0.25 -> "×¼"
    else -> "×$value"
}

@Preview(showBackground = true)
@Composable
fun PokemonDetailScreenPreview() {
    PokemonDetailContent(
        pokemon = Pokemon(
            pokedexNumber = 1,
            name = "Bulbasaur",
            types = "Planta / Veneno",
            level = 12,
            description = "Una semilla nace en su lomo.",
            stats = PokemonStats(45, 49, 49, 65, 65, 45)
        )
    )
}
