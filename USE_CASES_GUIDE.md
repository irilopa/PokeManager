# Use Cases Architecture - PokeManager

## Overview

This document explains the **Use Case pattern** implementation in PokeManager following **Clean Architecture** principles.

## What are Use Cases?

Use Cases (also called Interactors) are classes that encapsulate a single piece of business logic. They coordinate the flow of data between the UI layer and the data layer.

### Benefits

- ✅ **Single Responsibility**: Each use case does one thing
- ✅ **Reusable**: Can be called from multiple ViewModels
- ✅ **Testable**: Easy to unit test without UI or database
- ✅ **Maintainable**: Business logic changes are isolated
- ✅ **Readable**: Clear intent with descriptive names

## Architecture Layers

```
┌─────────────────────────────────────────┐
│         Presentation Layer              │
│  (UI, ViewModels, Composables)          │
└─────────────┬───────────────────────────┘
              │
              ↓
┌─────────────────────────────────────────┐
│          Domain Layer                   │
│  (Use Cases, Repository Interfaces)     │
└─────────────┬───────────────────────────┘
              │
              ↓
┌─────────────────────────────────────────┐
│           Data Layer                    │
│  (Repository Implementations, Firebase) │
└─────────────────────────────────────────┘
```

## Use Cases in PokeManager

### Authentication Use Cases

#### LoginUseCase
**Purpose**: Authenticate user with email and password

**Input**: email, password  
**Output**: `Result<FirebaseUser>`

**Responsibilities**:
- Validate email format
- Validate password length (min 6 characters)
- Check for empty fields
- Delegate to AuthRepository

```kotlin
class LoginUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String): Result<FirebaseUser> {
        // Validation logic
        // Delegation to repository
    }
}
```

#### RegisterUseCase
**Purpose**: Register new user

**Input**: email, password, confirmPassword  
**Output**: `Result<FirebaseUser>`

**Responsibilities**:
- Validate email format
- Validate password length
- Check passwords match
- Delegate to AuthRepository

#### LogoutUseCase
**Purpose**: Sign out current user

**Output**: `Result<Unit>`

### Pokemon Management Use Cases

#### GetPokemonsUseCase
**Purpose**: Retrieve all Pokemon for current user

**Output**: `Flow<Result<List<Pokemon>>>`

**Note**: Returns a Flow for real-time updates from Firestore

#### AddPokemonUseCase
**Purpose**: Add new Pokemon to collection

**Input**: Pokemon  
**Output**: `Result<Unit>`

**Responsibilities**:
- Validate Pokemon name is not empty
- Validate pokedexNumber > 0
- Validate height and weight > 0
- Validate types is not empty
- Delegate to PokemonRepository

#### UpdatePokemonUseCase
**Purpose**: Update existing Pokemon

**Input**: Pokemon  
**Output**: `Result<Unit>`

#### DeletePokemonUseCase
**Purpose**: Remove Pokemon from collection

**Input**: pokedexNumber  
**Output**: `Result<Unit>`

## How to Use in ViewModels

### Example: Using LoginUseCase

```kotlin
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
}
```

### Example: Using GetPokemonsUseCase

```kotlin
class PokemonListViewModel(
    private val getPokemonsUseCase: GetPokemonsUseCase,
    private val deletePokemonUseCase: DeletePokemonUseCase
) : ViewModel() {

    private val _pokemonState = MutableStateFlow<Result<List<Pokemon>>>(Result.Loading)
    val pokemonState: StateFlow<Result<List<Pokemon>>> = _pokemonState.asStateFlow()

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
            when (val result = deletePokemonUseCase(pokedexNumber)) {
                is Result.Success -> {
                    // Success - Firestore listener will update list automatically
                }
                is Result.Error -> {
                    _pokemonState.value = Result.Error(result.exception)
                }
                Result.Loading -> { }
            }
        }
    }
}
```

## Result Wrapper

The `Result` sealed class handles loading, success, and error states:

```kotlin
sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
    object Loading : Result<Nothing>()
}
```

### Usage in UI

```kotlin
@Composable
fun MyScreen(viewModel: MyViewModel) {
    val state by viewModel.state.collectAsState()

    when (state) {
        is Result.Loading -> CircularProgressIndicator()
        is Result.Success -> {
            val data = (state as Result.Success).data
            // Display data
        }
        is Result.Error -> {
            val message = (state as Result.Error).exception.message
            Text("Error: $message")
        }
    }
}
```

## Testing Use Cases

Use cases are easy to test because they have minimal dependencies:

```kotlin
@Test
fun `login with empty email returns error`() = runBlocking {
    // Given
    val mockRepository = mock<AuthRepository>()
    val useCase = LoginUseCase(mockRepository)
    
    // When
    val result = useCase("", "password123")
    
    // Then
    assertTrue(result is Result.Error)
    assertEquals("El email no puede estar vacío", (result as Result.Error).exception.message)
}

@Test
fun `add pokemon with invalid pokedex number returns error`() = runBlocking {
    // Given
    val mockRepository = mock<PokemonRepository>()
    val useCase = AddPokemonUseCase(mockRepository)
    val invalidPokemon = Pokemon(
        pokedexNumber = -1,  // Invalid!
        name = "Test",
        // ... other fields
    )
    
    // When
    val result = useCase(invalidPokemon)
    
    // Then
    assertTrue(result is Result.Error)
}
```

## Best Practices

1. **One use case = One action**: Keep use cases focused on a single responsibility
2. **Validate in use cases**: Business logic validation belongs here, not in ViewModels
3. **Use suspend functions**: For one-shot operations (login, add, delete)
4. **Use Flow**: For continuous data streams (real-time updates)
5. **Return Result**: Always wrap return values in Result for consistent error handling
6. **Name clearly**: Use verb phrases (GetPokemons, AddPokemon, DeletePokemon)

## Migration Guide

To migrate existing ViewModels to use these use cases:

### Before (Direct repository access)
```kotlin
class MyViewModel {
    private val repository = PokemonRepository
    
    fun loadData() {
        val data = repository.pokemons.value
        // ...
    }
}
```

### After (Using use cases)
```kotlin
class MyViewModel(
    private val getPokemonsUseCase: GetPokemonsUseCase
) : ViewModel() {
    
    fun loadData() {
        viewModelScope.launch {
            getPokemonsUseCase().collect { result ->
                // Handle result
            }
        }
    }
}
```

## See Also

- [FIRESTORE_IMPLEMENTATION.md](FIRESTORE_IMPLEMENTATION.md) - Full Firebase implementation guide (Spanish)
- [Repository Pattern](https://developer.android.com/topic/architecture/data-layer)
- [Clean Architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
