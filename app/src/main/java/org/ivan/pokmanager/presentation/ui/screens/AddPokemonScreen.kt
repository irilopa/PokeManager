package org.ivan.pokmanager.presentation.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import org.ivan.pokmanager.domain.validation.PokemonField
import org.ivan.pokmanager.domain.validation.PokemonFormValidator
import org.ivan.pokmanager.presentation.state.SaveState
import org.ivan.pokmanager.presentation.ui.components.PokemonDropdown
import org.ivan.pokmanager.presentation.ui.theme.PokeColors
import org.ivan.pokmanager.presentation.viewmodel.AddPokemonScreenViewModel
import org.koin.androidx.compose.koinViewModel

/**
 * Pantalla para registrar un Pokemon y autocompletar datos desde la PokeAPI.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPokemonScreen(
    navController: NavController,
    viewModel: AddPokemonScreenViewModel? = null
) {
    val vm: AddPokemonScreenViewModel = viewModel ?: koinViewModel()

    val form by vm.form.collectAsState()
    val hp by vm.hp.collectAsState()
    val attack by vm.attack.collectAsState()
    val errors by vm.errors.collectAsState()
    val saveState by vm.saveState.collectAsState()
    val isLoadingFromApi by vm.isLoadingFromApi.collectAsState()
    val apiError by vm.apiError.collectAsState()
    val apiSuccess by vm.apiSuccess.collectAsState()

    val name = form.name
    val nameError = errors[PokemonField.NAME]
    val pokemonTypes = vm.pokemonTypes
    val isSaving = saveState is SaveState.Saving
    val snackbarHostState = remember { SnackbarHostState() }

    val primaryColor = PokeColors.PokeRed

    LaunchedEffect(apiSuccess) {
        if (apiSuccess) {
            kotlinx.coroutines.delay(3000)
            vm.clearApiSuccess()
        }
    }

    // Solo se vuelve a la lista cuando Firestore confirma el guardado.
    LaunchedEffect(saveState) {
        when (val state = saveState) {
            SaveState.Success -> navController.navigateUp()
            is SaveState.Error -> snackbarHostState.showSnackbar(state.message)
            else -> Unit
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Registrar Pokémon") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
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
                .background(PokeColors.PokeWhite)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 32.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Icon(
                imageVector = Icons.Default.Pets,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = primaryColor
            )
            Text(
                text = "¡Añade un nuevo Pokémon a tu equipo!",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = PokeColors.PokeDarkGray
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Top
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        vm.setName(it)
                        vm.clearApiError()
                    },
                    label = { Text("Nombre del Pokémon") },
                    placeholder = { Text("Ej. Pikachu") },
                    leadingIcon = {
                        Icon(Icons.Default.Pets, contentDescription = null, tint = primaryColor)
                    },
                    isError = nameError != null || apiError != null,
                    supportingText = {
                        when {
                            nameError != null -> Text(nameError, color = MaterialTheme.colorScheme.error)
                            apiError != null -> Text(apiError!!, color = MaterialTheme.colorScheme.error)
                            apiSuccess -> Text(
                                "✓ Datos autocompletados desde la Pokédex",
                                color = Color(0xFF388E3C)
                            )
                        }
                    },
                    trailingIcon = {
                        when {
                            nameError != null || apiError != null ->
                                Icon(Icons.Default.Warning, "Error", tint = MaterialTheme.colorScheme.error)
                            apiSuccess ->
                                Icon(Icons.Default.CheckCircle, "OK", tint = Color(0xFF388E3C))
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { vm.fetchPokemonFromApi() }),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = primaryColor,
                        unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f)
                    )
                )

                Box(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .size(56.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (isLoadingFromApi) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(32.dp),
                            color = primaryColor,
                            strokeWidth = 3.dp
                        )
                    } else {
                        IconButton(
                            onClick = { vm.fetchPokemonFromApi() },
                            enabled = name.isNotBlank()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Buscar en Pokédex",
                                tint = if (name.isNotBlank()) primaryColor else Color.Gray,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            }

            OutlinedTextField(
                value = form.pokedexNumber,
                onValueChange = { newValue ->
                    if (newValue.all { it.isDigit() } && newValue.length <= 5) {
                        vm.setPokedexNumber(newValue)
                    }
                },
                label = { Text("Nº Pokédex (opcional)") },
                placeholder = { Text("Se rellena al buscar") },
                isError = errors[PokemonField.POKEDEX_NUMBER] != null,
                supportingText = errors[PokemonField.POKEDEX_NUMBER]?.let { { Text(it) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primaryColor,
                    unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f)
                )
            )

            PokemonDropdown(
                label = "Tipo Principal",
                options = pokemonTypes,
                selectedOption = form.primaryType,
                onOptionSelected = { vm.setPrimaryType(it) },
                errorMessage = errors[PokemonField.PRIMARY_TYPE]
            )

            PokemonDropdown(
                label = "Tipo Secundario (Opcional)",
                options = listOf("Ninguno") + pokemonTypes,
                selectedOption = form.secondaryType,
                onOptionSelected = {
                    val selected = if (it == "Ninguno") "" else it
                    vm.setSecondaryType(selected)
                },
                errorMessage = errors[PokemonField.SECONDARY_TYPE]
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = form.level,
                    onValueChange = { newValue ->
                        if (newValue.all { it.isDigit() } && newValue.length <= 3) {
                            vm.setLevel(newValue)
                        }
                    },
                    label = { Text("Nivel") },
                    placeholder = { Text("1-100") },
                    singleLine = true,
                    isError = errors[PokemonField.LEVEL] != null,
                    supportingText = errors[PokemonField.LEVEL]?.let { { Text(it) } },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = primaryColor,
                        unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f)
                    )
                )

                OutlinedTextField(
                    value = hp,
                    onValueChange = { if (it.all { char -> char.isDigit() }) vm.setHp(it) },
                    label = { Text("PS") },
                    placeholder = { Text("10") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = primaryColor,
                        unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f)
                    )
                )
            }

            OutlinedTextField(
                value = attack,
                onValueChange = { if (it.all { char -> char.isDigit() }) vm.setAttack(it) },
                label = { Text("Ataque") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primaryColor,
                    unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f)
                )
            )

            OutlinedTextField(
                value = form.notes,
                onValueChange = { vm.setNotes(it) },
                label = { Text("Notas adicionales") },
                isError = errors[PokemonField.NOTES] != null,
                supportingText = {
                    Text(
                        errors[PokemonField.NOTES]
                            ?: "${form.notes.length}/${PokemonFormValidator.MAX_NOTES_LENGTH}"
                    )
                },
                placeholder = { Text("¿Dónde lo capturaste? ¿Es shiny?") },
                minLines = 3,
                maxLines = 5,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primaryColor,
                    unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = vm::savePokemon,
                enabled = !isSaving && saveState !is SaveState.Success,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.size(12.dp))
                    Text("Guardando…", fontSize = 18.sp)
                } else {
                    Text("Guardar Pokémon", fontSize = 18.sp)
                }
            }
        }
    }
}


@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun AddPokemonScreenPreview() {
    AddPokemonScreen(navController = rememberNavController())
}