# Integration Guide: Connecting ViewModels to Use Cases

This guide shows how to update existing ViewModels to use the newly implemented use cases and Firebase repositories.

## Setup: Dependency Injection

First, create instances of repositories and use cases. In a production app, you should use Hilt or Koin for dependency injection.

### Create a simple DI container (app/src/main/java/org/ivan/pokmanager/di/AppContainer.kt)

```kotlin
package org.ivan.pokmanager.di

import org.ivan.pokmanager.data.repository.FirebaseAuthRepository
import org.ivan.pokmanager.data.repository.FirestorePokemonRepository
import org.ivan.pokmanager.domain.repository.AuthRepository
import org.ivan.pokmanager.domain.repository.PokemonRepository
import org.ivan.pokmanager.domain.usecase.*

/**
 * Simple dependency injection container
 * In production, use Hilt or Koin instead
 */
object AppContainer {
    // Repositories
    val authRepository: AuthRepository by lazy {
        FirebaseAuthRepository()
    }
    
    val pokemonRepository: PokemonRepository by lazy {
        FirestorePokemonRepository()
    }
    
    // Authentication Use Cases
    val loginUseCase: LoginUseCase by lazy {
        LoginUseCase(authRepository)
    }
    
    val registerUseCase: RegisterUseCase by lazy {
        RegisterUseCase(authRepository)
    }
    
    val logoutUseCase: LogoutUseCase by lazy {
        LogoutUseCase(authRepository)
    }
    
    // Pokemon Use Cases
    val getPokemonsUseCase: GetPokemonsUseCase by lazy {
        GetPokemonsUseCase(pokemonRepository)
    }
    
    val addPokemonUseCase: AddPokemonUseCase by lazy {
        AddPokemonUseCase(pokemonRepository)
    }
    
    val updatePokemonUseCase: UpdatePokemonUseCase by lazy {
        UpdatePokemonUseCase(pokemonRepository)
    }
    
    val deletePokemonUseCase: DeletePokemonUseCase by lazy {
        DeletePokemonUseCase(pokemonRepository)
    }
}
```

## 1. Update LoginScreenViewModel

**Before:**
```kotlin
class LoginScreenViewModel : ViewModel() {
    // No actual logic, just navigation
}
```

**After:**
```kotlin
package org.ivan.pokmanager.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.ivan.pokmanager.domain.usecase.LoginUseCase
import org.ivan.pokmanager.domain.util.Result
import com.google.firebase.auth.FirebaseUser

class LoginScreenViewModel(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _loginState = MutableStateFlow<Result<FirebaseUser>?>(null)
    val loginState: StateFlow<Result<FirebaseUser>?> = _loginState.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = Result.Loading
            val result = loginUseCase(email, password)
            _loginState.value = result
        }
    }

    fun clearState() {
        _loginState.value = null
    }
}
```

**Update LoginScreen to use it:**
```kotlin
@Composable
fun LoginScreen(
    navController: NavHostController,
    viewModel: LoginScreenViewModel = viewModel(
        factory = viewModelFactory {
            addInitializer(LoginScreenViewModel::class) {
                LoginScreenViewModel(AppContainer.loginUseCase)
            }
        }
    )
) {
    val loginState by viewModel.loginState.collectAsState()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Observe login state
    LaunchedEffect(loginState) {
        when (val state = loginState) {
            is Result.Success -> {
                // Navigate to Pokemon List
                navController.navigate(Screen.PokemonList.route) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                }
                viewModel.clearState()
            }
            is Result.Error -> {
                errorMessage = state.exception.message
            }
            else -> { }
        }
    }

    // UI with loading indicator
    Column {
        // ... email and password fields ...
        
        if (loginState is Result.Loading) {
            CircularProgressIndicator()
        }
        
        Button(
            onClick = { viewModel.login(email, password) },
            enabled = loginState !is Result.Loading
        ) {
            Text("Iniciar Sesión")
        }
        
        errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}
```

## 2. Update RegisterScreenViewModel

**After:**
```kotlin
package org.ivan.pokmanager.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.ivan.pokmanager.domain.usecase.RegisterUseCase
import org.ivan.pokmanager.domain.util.Result
import com.google.firebase.auth.FirebaseUser

class RegisterScreenViewModel(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _registerState = MutableStateFlow<Result<FirebaseUser>?>(null)
    val registerState: StateFlow<Result<FirebaseUser>?> = _registerState.asStateFlow()

    fun register(email: String, password: String, confirmPassword: String) {
        viewModelScope.launch {
            _registerState.value = Result.Loading
            val result = registerUseCase(email, password, confirmPassword)
            _registerState.value = result
        }
    }

    fun clearState() {
        _registerState.value = null
    }
}
```

## 3. Update PokemonListViewModel

**Before:**
```kotlin
class PokemonListViewModel : ViewModel() {
    val pokemons = PokemonRepository.pokemons
    
    fun deletePokemon(pokedexNumber: Int) {
        PokemonRepository.deletePokemon(pokedexNumber)
    }
}
```

**After:**
```kotlin
package org.ivan.pokmanager.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.ivan.pokmanager.data.model.Pokemon
import org.ivan.pokmanager.domain.usecase.GetPokemonsUseCase
import org.ivan.pokmanager.domain.usecase.DeletePokemonUseCase
import org.ivan.pokmanager.domain.usecase.LogoutUseCase
import org.ivan.pokmanager.domain.util.Result

class PokemonListViewModel(
    private val getPokemonsUseCase: GetPokemonsUseCase,
    private val deletePokemonUseCase: DeletePokemonUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _pokemonState = MutableStateFlow<Result<List<Pokemon>>>(Result.Loading)
    val pokemonState: StateFlow<Result<List<Pokemon>>> = _pokemonState.asStateFlow()

    private val _deleteState = MutableStateFlow<Result<Unit>?>(null)
    val deleteState: StateFlow<Result<Unit>?> = _deleteState.asStateFlow()

    init {
        loadPokemons()
    }

    private fun loadPokemons() {
        viewModelScope.launch {
            getPokemonsUseCase().collect { result ->
                _pokemonState.value = result
            }
        }
    }

    fun deletePokemon(pokedexNumber: Int) {
        viewModelScope.launch {
            _deleteState.value = Result.Loading
            val result = deletePokemonUseCase(pokedexNumber)
            _deleteState.value = result
            // No need to reload - Firestore listener will update automatically
        }
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
        }
    }

    fun clearDeleteState() {
        _deleteState.value = null
    }
}
```

**Update PokemonListScreen:**
```kotlin
@Composable
fun PokemonListScreen(
    navController: NavHostController,
    viewModel: PokemonListViewModel = viewModel(
        factory = viewModelFactory {
            addInitializer(PokemonListViewModel::class) {
                PokemonListViewModel(
                    AppContainer.getPokemonsUseCase,
                    AppContainer.deletePokemonUseCase,
                    AppContainer.logoutUseCase
                )
            }
        }
    )
) {
    val pokemonState by viewModel.pokemonState.collectAsState()
    val deleteState by viewModel.deleteState.collectAsState()

    // Handle delete result
    LaunchedEffect(deleteState) {
        when (deleteState) {
            is Result.Success -> {
                // Show success message
                viewModel.clearDeleteState()
            }
            is Result.Error -> {
                // Show error message
                viewModel.clearDeleteState()
            }
            else -> { }
        }
    }

    when (val state = pokemonState) {
        is Result.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is Result.Success -> {
            LazyColumn {
                items(state.data) { pokemon ->
                    PokemonCard(
                        pokemon = pokemon,
                        onDelete = { viewModel.deletePokemon(pokemon.pokedexNumber) },
                        onClick = { /* Navigate to detail */ }
                    )
                }
            }
        }
        is Result.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Error: ${state.exception.message}",
                        color = MaterialTheme.colorScheme.error
                    )
                    Button(onClick = { /* Retry */ }) {
                        Text("Reintentar")
                    }
                }
            }
        }
    }
}
```

## 4. Update AddPokemonScreenViewModel

**Before:**
```kotlin
class AddPokemonScreenViewModel : ViewModel() {
    fun addPokemon(pokemon: Pokemon) {
        PokemonRepository.addPokemon(pokemon)
    }
}
```

**After:**
```kotlin
package org.ivan.pokmanager.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.ivan.pokmanager.data.model.Pokemon
import org.ivan.pokmanager.domain.usecase.AddPokemonUseCase
import org.ivan.pokmanager.domain.util.Result

class AddPokemonScreenViewModel(
    private val addPokemonUseCase: AddPokemonUseCase
) : ViewModel() {

    private val _addState = MutableStateFlow<Result<Unit>?>(null)
    val addState: StateFlow<Result<Unit>?> = _addState.asStateFlow()

    fun addPokemon(pokemon: Pokemon) {
        viewModelScope.launch {
            _addState.value = Result.Loading
            val result = addPokemonUseCase(pokemon)
            _addState.value = result
        }
    }

    fun clearState() {
        _addState.value = null
    }
}
```

## 5. Check Authentication State on App Start

Update MainActivity or create a splash screen:

```kotlin
@Composable
fun RootNavigation() {
    val authRepository = AppContainer.authRepository
    val startDestination = if (authRepository.getCurrentUser() != null) {
        Screen.PokemonList.route
    } else {
        Screen.Login.route
    }
    
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // ... composable routes ...
    }
}
```

## Testing the Integration

1. **Test Registration**:
   - Open the app
   - Go to Register screen
   - Enter email and password
   - Should create Firebase user and navigate to Pokemon List

2. **Test Login**:
   - Logout
   - Go to Login screen
   - Enter credentials
   - Should authenticate and navigate to Pokemon List

3. **Test Add Pokemon**:
   - Click "Add Pokemon"
   - Fill form
   - Save
   - Should appear in list immediately (Firestore real-time listener)

4. **Test Delete Pokemon**:
   - Click delete on a Pokemon
   - Should disappear from list immediately

5. **Test Persistence**:
   - Add some Pokemon
   - Close app
   - Reopen app
   - Login
   - Pokemon should still be there

## Important Notes

- **Authentication required**: Users must be logged in to see Pokemon (enforced by Firestore rules)
- **Real-time updates**: Changes to Pokemon collection update automatically via Firestore listeners
- **Error handling**: All operations return Result which includes error messages
- **Loading states**: UI can show loading indicators while operations are in progress

## Next Steps

1. Implement proper dependency injection with Hilt
2. Add unit tests for use cases
3. Add integration tests for repositories
4. Implement proper error handling UI (Snackbars, dialogs)
5. Add offline support with Room for caching
