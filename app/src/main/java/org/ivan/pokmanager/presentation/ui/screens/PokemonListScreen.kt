package org.ivan.pokmanager.presentation.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CatchingPokemon
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import org.ivan.pokmanager.domain.type.PokemonType
import org.ivan.pokmanager.presentation.navigation.Screen
import org.ivan.pokmanager.presentation.state.UiState
import org.ivan.pokmanager.presentation.ui.components.ActionMenu
import org.ivan.pokmanager.presentation.ui.components.ErrorContent
import org.ivan.pokmanager.presentation.ui.components.LoadingContent
import org.ivan.pokmanager.presentation.ui.components.MessageContent
import org.ivan.pokmanager.presentation.ui.theme.PokeColors
import org.ivan.pokmanager.presentation.ui.theme.TypeColors
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

    val uiState by viewModel.uiState.collectAsState()
    val visiblePokemons by viewModel.visiblePokemons.collectAsState()
    val query by viewModel.query.collectAsState()
    val typeFilter by viewModel.typeFilter.collectAsState()
    val goToLogin = {
        navController.navigate(Screen.Login.route) {
            // Vacia toda la pila para que «Atras» no vuelva al equipo.
            popUpTo(navController.graph.id) { inclusive = true }
            launchSingleTop = true
        }
    }
    val deleteError by viewModel.deleteError.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val primaryColor = PokeColors.PokeRed
    val goToAddPokemon = { navController.navigate(Screen.AddPokemon.route) }

    LaunchedEffect(deleteError) {
        deleteError?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearDeleteError()
        }
    }

    Scaffold(
        topBar = {
            ActionMenu(
                title = "Mi Equipo Pokémon",
                onAddPokemon = goToAddPokemon,
                onLogout = {
                    viewModel.logout()
                    goToLogin()
                },
                onDeleteAccount = { navController.navigate(Screen.DeleteAccount.route) }
            )
        },
        floatingActionButton = {
            if (uiState is UiState.Success) {
                FloatingActionButton(
                    onClick = goToAddPokemon,
                    containerColor = primaryColor,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Añadir Pokémon")
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.White
    ) { innerPadding ->
        val contentModifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
            .background(Color.White)

        when (val state = uiState) {
            UiState.Loading -> LoadingContent(contentModifier)

            is UiState.Error -> ErrorContent(
                message = state.message,
                onRetry = viewModel::retry,
                modifier = contentModifier
            )

            is UiState.Success -> if (state.data.isEmpty()) {
                MessageContent(
                    icon = Icons.Default.CatchingPokemon,
                    message = "Todavía no tienes Pokémon en tu equipo.\n¡Registra el primero para empezar!",
                    actionLabel = "Añadir mi primer Pokémon",
                    onAction = goToAddPokemon,
                    modifier = contentModifier
                )
            } else {
                Column(modifier = contentModifier) {
                    SearchAndFilters(
                        query = query,
                        selectedType = typeFilter,
                        onQueryChange = viewModel::setQuery,
                        onTypeClick = viewModel::toggleTypeFilter,
                        onClear = viewModel::clearFilters
                    )
                    if (visiblePokemons.isEmpty()) {
                        MessageContent(
                            icon = Icons.Default.SearchOff,
                            message = "No se encontraron Pokémon con esos filtros.",
                            actionLabel = "Limpiar filtros",
                            onAction = viewModel::clearFilters
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 24.dp),
                            contentPadding = PaddingValues(top = 8.dp, bottom = 88.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(visiblePokemons, key = { it.id }) { pokemon ->
                                PokemonCard(pokemon, viewModel, primaryColor) {
                                    navController.navigate(Screen.PokemonDetail.createScreen(pokemon.id))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Buscador por nombre y fila de chips de tipo. La «X» limpia ambos filtros.
 */
@Composable
private fun SearchAndFilters(
    query: String,
    selectedType: PokemonType?,
    onQueryChange: (String) -> Unit,
    onTypeClick: (PokemonType) -> Unit,
    onClear: () -> Unit
) {
    Column(modifier = Modifier.padding(top = 12.dp)) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = { Text("Buscar en tu equipo") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (query.isNotEmpty() || selectedType != null) {
                    IconButton(onClick = onClear) {
                        Icon(Icons.Default.Close, contentDescription = "Limpiar filtros")
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(28.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PokeColors.PokeRed,
                unfocusedBorderColor = Color.Gray.copy(alpha = 0.4f)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(PokemonType.entries) { type ->
                val selected = type == selectedType
                FilterChip(
                    selected = selected,
                    onClick = { onTypeClick(type) },
                    label = { Text(type.spanishName) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = TypeColors.background(type),
                        selectedLabelColor = TypeColors.content(type)
                    )
                )
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
                    text = if (pokemon.level > 0) "Nv. ${pokemon.level} · ${pokemon.types}" else pokemon.types,
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