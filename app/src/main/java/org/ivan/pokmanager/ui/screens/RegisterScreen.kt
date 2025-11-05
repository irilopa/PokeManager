package org.ivan.pokmanager.ui.screens

import androidx.compose.foundation.Image
import org.ivan.pokmanager.R
import org.ivan.pokmanager.ui.theme.PokeColors
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun Register(
    onRegisterClick: (String, String, String, String) -> Unit = { _, _, _, _ -> },
    onBackClick: () -> Unit = {}
) {
    var nombre by remember { mutableStateOf("") }
    var apellidos by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var fechaNacimiento by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            PokeColors.PokeRed,
            PokeColors.PokeWhite
        )
    )

    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradientBrush)
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Logo
                Card(
                    modifier = Modifier
                        .size(140.dp)
                        .shadow(8.dp, RoundedCornerShape(70.dp)),
                    shape = RoundedCornerShape(70.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = PokeColors.PokeWhite
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.logo_app),
                            contentDescription = "Logo PokeManager",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Nuevo Entrenador",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = PokeColors.PokeYellow,
                    style = MaterialTheme.typography.headlineLarge
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Completa tu perfil de entrenador",
                    fontSize = 14.sp,
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Card contenedor para los campos
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.95f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Campo de nombre
                        OutlinedTextField(
                            value = nombre,
                            onValueChange = {
                                nombre = it
                                showError = false
                            },
                            label = { Text("Nombre") },
                            placeholder = { Text("Tu nombre") },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = "Nombre",
                                    tint = PokeColors.PokeRed
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PokeColors.PokeRed,
                                unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f)
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Campo de apellidos
                        OutlinedTextField(
                            value = apellidos,
                            onValueChange = {
                                apellidos = it
                                showError = false
                            },
                            label = { Text("Apellidos") },
                            placeholder = { Text("Tus apellidos") },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.People,
                                    contentDescription = "Apellidos",
                                    tint = PokeColors.PokeRed
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PokeColors.PokeRed,
                                unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f)
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Campo de email
                        OutlinedTextField(
                            value = email,
                            onValueChange = {
                                email = it
                                showError = false
                            },
                            label = { Text("Email") },
                            placeholder = { Text("entrenador@pokemon.com") },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Email,
                                    contentDescription = "Email",
                                    tint = PokeColors.PokeBlue
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PokeColors.PokeRed,
                                unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f)
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Campo de fecha de nacimiento
                        OutlinedTextField(
                            value = fechaNacimiento,
                            onValueChange = {
                                fechaNacimiento = it
                                showError = false
                            },
                            label = { Text("Fecha de Nacimiento") },
                            placeholder = { Text("DD/MM/AAAA") },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Cake,
                                    contentDescription = "Fecha de Nacimiento",
                                    tint = PokeColors.PokeYellow
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PokeColors.PokeRed,
                                unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f)
                            )
                        )

                        // Mensaje de error
                        if (showError) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = errorMessage,
                                color = PokeColors.PokeRed,
                                fontSize = 12.sp,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Botón de Registro
                        Button(
                            onClick = {
                                when {
                                    nombre.isBlank() -> {
                                        showError = true
                                        errorMessage = "Ingresa tu nombre"
                                    }

                                    apellidos.isBlank() -> {
                                        showError = true
                                        errorMessage = "Ingresa tus apellidos"
                                    }

                                    email.isBlank() -> {
                                        showError = true
                                        errorMessage = "Ingresa tu email"
                                    }

                                    !email.contains("@") || !email.contains(".") -> {
                                        showError = true
                                        errorMessage = "Ingresa un email válido"
                                    }

                                    fechaNacimiento.isBlank() -> {
                                        showError = true
                                        errorMessage = "Ingresa tu fecha de nacimiento"
                                    }

                                    else -> {
                                        onRegisterClick(nombre, apellidos, email, fechaNacimiento)
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PokeColors.PokeRed
                            )
                        ) {
                            Icon(
                                Icons.Default.PersonAdd,
                                contentDescription = "Registrarse"
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "Registrarse",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Botones secundarios
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Botón de Volver
                            OutlinedButton(
                                onClick = onBackClick,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = PokeColors.PokeBlue
                                )
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Volver",
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(Modifier.width(4.dp))
                                Text("Volver", fontSize = 14.sp)
                            }

                            // Botón de Limpiar
                            OutlinedButton(
                                onClick = {
                                    nombre = ""
                                    apellidos = ""
                                    email = ""
                                    fechaNacimiento = ""
                                    showError = false
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color.Gray
                                )
                            ) {
                                Icon(
                                    Icons.Default.Clear,
                                    contentDescription = "Limpiar",
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(Modifier.width(4.dp))
                                Text("Limpiar", fontSize = 14.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Texto de ayuda
                TextButton(onClick = onBackClick) {
                    Text(
                        "¿Ya tienes cuenta? Inicia sesión",
                        color = Color.Red,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterPreview() {
    Register()
}