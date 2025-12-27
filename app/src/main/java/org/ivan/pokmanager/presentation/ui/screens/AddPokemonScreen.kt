package org.ivan.pokmanager.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.ivan.pokmanager.presentation.ui.theme.PokeColors
import org.ivan.pokmanager.presentation.viewmodel.AddPokemonScreenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPokemonScreen(
    onBackClick: () -> Unit = {},
    viewModel: AddPokemonScreenViewModel = viewModel()
) {
    val name by viewModel.name.collectAsState()
    val primaryType by viewModel.primaryType.collectAsState()
    val secondaryType by viewModel.secondaryType.collectAsState()
    val level by viewModel.level.collectAsState()
    val hp by viewModel.hp.collectAsState()
    val attack by viewModel.attack.collectAsState()
    val notes by viewModel.notes.collectAsState()

    var isNameError by remember { mutableStateOf(false) }
    val pokemonTypes = viewModel.pokemonTypes

    val primaryColor = PokeColors.PokeRed

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registrar Pokémon") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
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

            // --- 2. ENCABEZADO VISUAL ---
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

            // --- 3. CAMPOS DEL FORMULARIO ---

            // 1️⃣ Nombre (Obligatorio)
            OutlinedTextField(
                value = name,
                onValueChange = {
                    viewModel.setName(it)
                    isNameError = false
                },
                label = { Text("Nombre del Pokémon") },
                placeholder = { Text("Ej. Pikachu") },
                leadingIcon = {
                    Icon(
                        Icons.Default.Pets,
                        contentDescription = null,
                        tint = primaryColor
                    )
                },
                isError = isNameError,
                supportingText = {
                    if (isNameError) Text(
                        "El nombre es obligatorio",
                        color = MaterialTheme.colorScheme.error
                    )
                },
                trailingIcon = {
                    if (isNameError) Icon(
                        Icons.Default.Warning,
                        "Error",
                        tint = MaterialTheme.colorScheme.error
                    )
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primaryColor,
                    unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f)
                )
            )

            // 2️⃣ Tipo Principal (Obligatorio)
            PokemonDropdown(
                label = "Tipo Principal",
                options = pokemonTypes,
                selectedOption = primaryType,
                onOptionSelected = { viewModel.setPrimaryTypeSelected(it) }
            )

            // 3️⃣ Tipo Secundario (Opcional)
            PokemonDropdown(
                label = "Tipo Secundario (Opcional)",
                options = listOf("Ninguno") + pokemonTypes, // Añadimos opción de vacío
                selectedOption = secondaryType.ifBlank { "" },
                onOptionSelected = {
                    val selected = if (it == "Ninguno") "" else it
                    viewModel.setSecondaryTypeSelected(selected)
                }
            )

            // 4️⃣ Nivel (Numérico 1-100)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = level,
                    onValueChange = { newValue ->
                        // Solo permite números y longitud máx 3
                        if (newValue.all { it.isDigit() } && newValue.length <= 3) {
                            viewModel.setLevel(newValue)
                        }
                    },
                    label = { Text("Nivel") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = primaryColor,
                        unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f)
                    )
                )

                // 5️⃣ PS (Puntos de Salud)
                OutlinedTextField(
                    value = hp,
                    onValueChange = { if (it.all { char -> char.isDigit() }) viewModel.setHp(it) },
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

            // 6️⃣ Ataque
            OutlinedTextField(
                value = attack,
                onValueChange = { if (it.all { char -> char.isDigit() }) viewModel.setAttack(it) },
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

            // 7️⃣ Notas (Opcional, Multilinea)
            OutlinedTextField(
                value = notes,
                onValueChange = { viewModel.setNotes(it) },
                label = { Text("Notas adicionales") },
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

            // --- 4. BOTÓN PRINCIPAL ---
            Button(
                onClick = {
                    // --- VALIDACIÓN ---
                    if (name.isBlank()) {
                        isNameError = true
                    } else if (primaryType.isBlank()) {
                        // Aquí podrías mostrar un Toast o Snackbar pidiendo el tipo
                    } else {
                        // TODO: Crear objeto Pokémon y guardar en BDD
                        /*
                           val newPokemon = Pokemon(
                               name = name,
                               type1 = primaryType,
                               type2 = secondaryType,
                               level = level.toIntOrNull() ?: 1,
                               hp = hp.toIntOrNull() ?: 10,
                               attack = attack.toIntOrNull() ?: 5,
                               notes = notes
                           )
                           viewModel.save(newPokemon)
                        */
                        onBackClick()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
            ) {
                Text("Guardar Pokémon", fontSize = 18.sp)
            }
        }
    }
}

/**
 * Componente reutilizable para los menús desplegables
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonDropdown(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    primaryColor: Color = PokeColors.PokeRed
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                focusedBorderColor = primaryColor,
                unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f)
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddPokemonScreenPreview() {
    AddPokemonScreen()
}