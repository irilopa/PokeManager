package org.ivan.pokmanager.presentation.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.ivan.pokmanager.R
import org.ivan.pokmanager.presentation.ui.theme.PokeColors
import org.ivan.pokmanager.presentation.viewmodel.LoginScreenViewModel

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit = {}, // Acción al loguearse con éxito
    onRegisterClick: () -> Unit = {}, // Acción para ir a registrarse
    viewModel: LoginScreenViewModel = viewModel()
) {
    // 1. Recolección de estado del ViewModel
    val username by viewModel.username.collectAsState()
    val password by viewModel.password.collectAsState()

    // 2. Estado local de la UI
    var passwordVisible by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val primaryColor = PokeColors.PokeRed

    Scaffold(
        // Opcional: Puedes poner un TopAppBar aquí si quieres,
        // pero las pantallas de Login suelen ser limpias.
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White) // Fondo limpio
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()) // Scroll si la pantalla es pequeña
                .padding(horizontal = 32.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center // Centra el contenido verticalmente
        ) {

            // LOGO
            Image(
                painter = painterResource(id = R.drawable.logo_app),
                contentDescription = "Logo PokeManager",
                modifier = Modifier
                    .size(120.dp)
                    .padding(bottom = 16.dp)
            )

            // TÍTULO
            Text(
                text = "¡Bienvenido de nuevo!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = primaryColor
            )

            Text(
                text = "Gestiona tu equipo Pokémon",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(32.dp))

            // --- FORMULARIO ---

            // 1. Campo: USUARIO / ENTRENADOR
            OutlinedTextField(
                value = username,
                onValueChange = {
                    viewModel.setUsername(it)
                    showError = false
                },
                label = { Text("Entrenador") },
                leadingIcon = {
                    Icon(Icons.Default.Person, contentDescription = null, tint = primaryColor)
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primaryColor,
                    unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Campo: CONTRASEÑA
            OutlinedTextField(
                value = password,
                onValueChange = {
                    viewModel.setPassword(it)
                    showError = false
                },
                label = { Text("Contraseña") },
                leadingIcon = {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = primaryColor)
                },
                // Lógica para mostrar/ocultar contraseña
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                    val description = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"

                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, contentDescription = description, tint = Color.Gray)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primaryColor,
                    unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f)
                )
            )

            // MENSAJE DE ERROR
            if (showError) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage,
                    color = primaryColor,
                    fontSize = 14.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // BOTÓN DE INICIAR SESIÓN
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
                        else -> {
                            // Llamamos a la lógica del ViewModel
                            viewModel.login()
                            onLoginSuccess() // Callback de navegación
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
            ) {
                Text("Iniciar Sesión", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // BOTÓN IR A REGISTRO
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text("¿No tienes cuenta?", fontSize = 14.sp, color = Color.Gray)
                TextButton(onClick = onRegisterClick) {
                    Text(
                        "Regístrate",
                        color = primaryColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen()
}