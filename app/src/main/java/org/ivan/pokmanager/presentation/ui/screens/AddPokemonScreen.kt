package org.ivan.pokmanager.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPokemonScreen(onBackClick: () -> Unit = {}) {

    // --- 1. ESTADOS DEL FORMULARIO ---
    var name by remember { mutableStateOf("") }
    var isNameError by remember { mutableStateOf(false) } // Para validar si está vacío

    var primaryType by remember { mutableStateOf("") }
    var secondaryType by remember { mutableStateOf("") } // Opcional

    // Valores por defecto como String para el TextField, luego se convierten
    var level by remember { mutableStateOf("1") }
    var hp by remember { mutableStateOf("") }
    var attack by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    // Lista de tipos
    val pokemonTypes = listOf(
        "Planta", "Fuego", "Agua", "Eléctrico", "Hielo", "Lucha", "Veneno", "Tierra",
        "Roca", "Volador", "Psíquico", "Bicho", "Dragón", "Siniestro", "Fantasma",
        "Acero", "Hada", "Normal"
    )

    // Color principal (Usa PokeColors.PokeRed si lo tienes importado)
    val primaryColor = Color(0xFFEF5350) // Un rojo estilo Pokémon

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
                .background(Color.White)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()) // Habilita scroll vertical
                .padding(24.dp), // Margen general
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp) // Espacio entre elementos
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
                color = Color.DarkGray
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // --- 3. CAMPOS DEL FORMULARIO ---

            // 1️⃣ Nombre (Obligatorio)
            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    isNameError = false
                },
                label = { Text("Nombre del Pokémon") },
                placeholder = { Text("Ej. Pikachu") },
                leadingIcon = { Icon(Icons.Default.Pets, contentDescription = null) },
                isError = isNameError,
                supportingText = {
                    if (isNameError) Text("El nombre es obligatorio", color = MaterialTheme.colorScheme.error)
                },
                trailingIcon = {
                    if (isNameError) Icon(Icons.Default.Warning, "Error", tint = MaterialTheme.colorScheme.error)
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // 2️⃣ Tipo Principal (Obligatorio)
            PokemonDropdown(
                label = "Tipo Principal",
                options = pokemonTypes,
                selectedOption = primaryType,
                onOptionSelected = { primaryType = it }
            )

            // 3️⃣ Tipo Secundario (Opcional)
            PokemonDropdown(
                label = "Tipo Secundario (Opcional)",
                options = listOf("Ninguno") + pokemonTypes, // Añadimos opción de vacío
                selectedOption = secondaryType,
                onOptionSelected = { secondaryType = if (it == "Ninguno") "" else it }
            )

            // 4️⃣ Nivel (Numérico 1-100)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = level,
                    onValueChange = { newValue ->
                        // Solo permite números y longitud máx 3
                        if (newValue.all { it.isDigit() } && newValue.length <= 3) {
                            // Lógica extra para limitar a 100 si quieres, o dejar validar al final
                            level = newValue
                        }
                    },
                    label = { Text("Nivel") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f) // Ocupa la mitad
                )

                // 5️⃣ PS (Puntos de Salud)
                OutlinedTextField(
                    value = hp,
                    onValueChange = { if (it.all { char -> char.isDigit() }) hp = it },
                    label = { Text("PS") },
                    placeholder = { Text("10") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
            }

            // 6️⃣ Ataque
            OutlinedTextField(
                value = attack,
                onValueChange = { if (it.all { char -> char.isDigit() }) attack = it },
                label = { Text("Ataque") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth()
            )

            // 7️⃣ Notas (Opcional, Multilinea)
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notas adicionales") },
                placeholder = { Text("¿Dónde lo capturaste? ¿Es shiny?") },
                minLines = 3,
                maxLines = 5,
                modifier = Modifier.fillMaxWidth()
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
                        onBackClick() // Volver tras guardar
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
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
    onOptionSelected: (String) -> Unit
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
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
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