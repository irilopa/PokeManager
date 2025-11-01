package org.ivan.pokmanager.ui.screens

import androidx.compose.foundation.Image
import org.ivan.pokmanager.R
import org.ivan.pokmanager.ui.theme.PokeColors
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun Login(
    onLoginClick: (String, String) -> Unit = { _, _ -> },
    onRegisterClick: () -> Unit = {}
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // Gradiente de fondo estilo Pokémon
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
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Logo o imagen de Pokémon
                Card(
                    modifier = Modifier
                        .size(180.dp)
                        .shadow(8.dp, RoundedCornerShape(90.dp)),
                    shape = RoundedCornerShape(90.dp),
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

                // Título
                Text(
                    text = "PokeManager",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = PokeColors.PokeYellow,
                    style = MaterialTheme.typography.headlineLarge
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Gestiona tu equipo Pokémon",
                    fontSize = 14.sp,
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(32.dp))

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
                        // Campo de usuario
                        OutlinedTextField(
                            value = username,
                            onValueChange = {
                                username = it
                                showError = false
                            },
                            label = { Text("Nombre de Entrenador") },
                            placeholder = { Text("Introduzca un nombre") },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = "Usuario",
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

                        // Campo de contraseña
                        OutlinedTextField(
                            value = password,
                            onValueChange = {
                                password = it
                                showError = false
                            },
                            label = { Text("Contraseña") },
                            placeholder = { Text("••••••••") },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Lock,
                                    contentDescription = "Contraseña",
                                    tint = PokeColors.PokeRed
                                )
                            },
                            visualTransformation = if (passwordVisible)
                                VisualTransformation.None
                            else
                                PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = {
                                    passwordVisible = !passwordVisible
                                }) {
                                    Icon(
                                        imageVector = if (passwordVisible)
                                            Icons.Default.Visibility
                                        else
                                            Icons.Default.VisibilityOff,
                                        contentDescription = if (passwordVisible)
                                            "Ocultar contraseña"
                                        else
                                            "Mostrar contraseña",
                                        tint = PokeColors.PokeBlue
                                    )
                                }
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

                        // Botón de Iniciar Sesión
                        Button(
                            onClick = {
                                when {
                                    username.isBlank() -> {
                                        showError = true
                                        errorMessage = "Ingresa tu nombre de entrenador"
                                    }

                                    password.isBlank() -> {
                                        showError = true
                                        errorMessage = "Ingresa tu contraseña"
                                    }

                                    password.length < 8 -> {
                                        showError = true
                                        errorMessage =
                                            "La contraseña debe tener al menos 8 caracteres"
                                    }

                                    else -> {
                                        onLoginClick(username, password)
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
                                Icons.AutoMirrored.Filled.Login,
                                contentDescription = "Iniciar Sesión"
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "Iniciar Sesión",
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
                            // Botón de Registro
                            OutlinedButton(
                                onClick = onRegisterClick,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = PokeColors.PokeBlue
                                )
                            ) {
                                Icon(
                                    Icons.Default.PersonAdd,
                                    contentDescription = "Registro",
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(Modifier.width(4.dp))
                                Text("Registro", fontSize = 14.sp)
                            }

                            // Botón de Limpiar
                            OutlinedButton(
                                onClick = {
                                    username = ""
                                    password = ""
                                    passwordVisible = false
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
                TextButton(onClick = { /* Acción de recuperar contraseña */ }) {
                    Text(
                        "¿Olvidaste tu contraseña?",
                        color = Color.Red,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    Login()
}
