# Implementación de Firestore y Casos de Uso en PokeManager

Este documento explica en detalle cómo se han implementado **Firebase Firestore** y la **arquitectura de casos de uso** en la aplicación PokeManager.

## 📋 Tabla de Contenidos

1. [Arquitectura General](#arquitectura-general)
2. [Configuración de Firebase](#configuración-de-firebase)
3. [Capa de Dominio (Domain Layer)](#capa-de-dominio)
4. [Capa de Datos (Data Layer)](#capa-de-datos)
5. [Casos de Uso Implementados](#casos-de-uso-implementados)
6. [Integración con Firebase Firestore](#integración-con-firebase-firestore)
7. [Uso en ViewModels](#uso-en-viewmodels)
8. [Ejemplo Completo de Flujo](#ejemplo-completo-de-flujo)
9. [Buenas Prácticas](#buenas-prácticas)

---

## Arquitectura General

La aplicación sigue una **arquitectura limpia (Clean Architecture)** con separación clara de capas:

```
app/src/main/java/org/ivan/pokmanager/
│
├── data/                           # Capa de Datos
│   ├── model/                      # Modelos de datos
│   │   ├── Pokemon.kt
│   │   ├── User.kt
│   │   └── PokemonStats.kt
│   └── repository/                 # Implementaciones de repositorios
│       ├── FirebaseAuthRepository.kt
│       └── FirestorePokemonRepository.kt
│
├── domain/                         # Capa de Dominio
│   ├── repository/                 # Interfaces de repositorios
│   │   ├── AuthRepository.kt
│   │   └── PokemonRepository.kt
│   ├── usecase/                    # Casos de uso
│   │   ├── LoginUseCase.kt
│   │   ├── RegisterUseCase.kt
│   │   ├── LogoutUseCase.kt
│   │   ├── GetPokemonsUseCase.kt
│   │   ├── AddPokemonUseCase.kt
│   │   ├── UpdatePokemonUseCase.kt
│   │   └── DeletePokemonUseCase.kt
│   └── util/
│       └── Result.kt               # Wrapper para manejo de errores
│
└── presentation/                   # Capa de Presentación
    ├── viewmodel/                  # ViewModels
    ├── ui/                         # Composables y UI
    └── navigation/                 # Navegación
```

### Beneficios de esta arquitectura:

- ✅ **Separación de responsabilidades**: Cada capa tiene una función específica
- ✅ **Testeable**: Los casos de uso y repositorios se pueden probar de forma aislada
- ✅ **Mantenible**: Cambios en una capa no afectan directamente a las otras
- ✅ **Escalable**: Fácil agregar nuevas funcionalidades siguiendo el mismo patrón

---

## Configuración de Firebase

### 1. Dependencias en `gradle/libs.versions.toml`

```toml
[versions]
firebaseBom = "34.4.0"
coroutines = "1.9.0"

[libraries]
firebase-bom = { group = "com.google.firebase", name = "firebase-bom", version.ref = "firebaseBom" }
firebase-analytics = { group = "com.google.firebase", name = "firebase-analytics" }
firebase-auth = { group = "com.google.firebase", name = "firebase-auth" }
firebase-firestore = { group = "com.google.firebase", name = "firebase-firestore" }
kotlinx-coroutines-core = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-core", version.ref = "coroutines" }
kotlinx-coroutines-android = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-android", version.ref = "coroutines" }
kotlinx-coroutines-play-services = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-play-services", version.ref = "coroutines" }
```

### 2. Plugin y dependencias en `app/build.gradle.kts`

```kotlin
plugins {
    id("com.google.gms.google-services")  // Plugin de Google Services
    // ... otros plugins
}

dependencies {
    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    
    // Coroutines para operaciones asíncronas
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.play.services)
    
    // ... otras dependencias
}
```

### 3. Archivo `google-services.json`

El archivo `google-services.json` debe estar en el directorio `app/` y contiene las credenciales de tu proyecto Firebase. Se obtiene desde la consola de Firebase.

### 4. Configuración en Firebase Console

1. **Firebase Authentication**:
   - Habilitar Email/Password como método de autenticación
   - Configurar dominios autorizados

2. **Cloud Firestore**:
   - Crear base de datos en modo de prueba o producción
   - Configurar reglas de seguridad (ver sección de Reglas de Seguridad)

---

## Capa de Dominio

### Result Wrapper

La clase `Result` es un contenedor genérico para manejar estados de carga, éxito y error:

```kotlin
sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
    object Loading : Result<Nothing>()
}
```

**Uso**:
```kotlin
when (result) {
    is Result.Success -> {
        // Manejar datos exitosos
        val data = result.data
    }
    is Result.Error -> {
        // Manejar error
        val message = result.exception.message
    }
    Result.Loading -> {
        // Mostrar indicador de carga
    }
}
```

### Interfaces de Repositorio

Las interfaces definen el **contrato** que deben cumplir las implementaciones:

#### AuthRepository

```kotlin
interface AuthRepository {
    suspend fun register(email: String, password: String): Result<FirebaseUser>
    suspend fun login(email: String, password: String): Result<FirebaseUser>
    suspend fun logout(): Result<Unit>
    fun getCurrentUser(): FirebaseUser?
}
```

#### PokemonRepository

```kotlin
interface PokemonRepository {
    fun getPokemons(): Flow<Result<List<Pokemon>>>
    suspend fun addPokemon(pokemon: Pokemon): Result<Unit>
    suspend fun updatePokemon(pokemon: Pokemon): Result<Unit>
    suspend fun deletePokemon(pokedexNumber: Int): Result<Unit>
    suspend fun getPokemon(pokedexNumber: Int): Result<Pokemon>
}
```

**Nota**: Usamos `Flow` para `getPokemons()` porque queremos escuchar cambios en tiempo real desde Firestore.

---

## Capa de Datos

### FirebaseAuthRepository

Implementa `AuthRepository` usando Firebase Authentication:

```kotlin
class FirebaseAuthRepository : AuthRepository {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    override suspend fun register(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user
            if (user != null) {
                Result.Success(user)
            } else {
                Result.Error(Exception("No se pudo crear el usuario"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    // ... otras implementaciones
}
```

**Puntos clave**:
- Usa `await()` de Coroutines para convertir callbacks de Firebase en código secuencial
- Maneja errores con `try-catch` y los envuelve en `Result.Error`
- Retorna `Result.Success` con los datos cuando todo funciona correctamente

### FirestorePokemonRepository

Implementa `PokemonRepository` usando Cloud Firestore:

```kotlin
class FirestorePokemonRepository : PokemonRepository {
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    
    override fun getPokemons(): Flow<Result<List<Pokemon>>> = callbackFlow {
        val userId = auth.currentUser?.uid
        
        if (userId == null) {
            trySend(Result.Error(Exception("Usuario no autenticado")))
            close()
            return@callbackFlow
        }

        trySend(Result.Loading)

        val subscription = firestore.collection("pokemons")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.Error(error))
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val pokemons = snapshot.documents.mapNotNull { doc ->
                        // Convertir documento a Pokemon
                    }
                    trySend(Result.Success(pokemons))
                }
            }

        awaitClose { subscription.remove() }
    }
    
    // ... otras implementaciones
}
```

**Puntos clave**:
- **callbackFlow**: Convierte listeners de Firestore en Flow reactivo
- **whereEqualTo**: Filtra solo los Pokemon del usuario autenticado
- **addSnapshotListener**: Escucha cambios en tiempo real
- **awaitClose**: Limpia el listener cuando el Flow se cancela
- **Seguridad**: Siempre verifica que el usuario esté autenticado antes de hacer operaciones

#### Estructura de documento en Firestore

```json
{
  "userId": "uid-del-usuario",
  "pokedexNumber": 25,
  "name": "Pikachu",
  "types": "Eléctrico",
  "description": "Mantiene su cola en alto para vigilar...",
  "heightM": 0.4,
  "weightKg": 6.0,
  "imageUrl": "https://...",
  "moves": ["Impactrueno", "Onda Trueno"],
  "stats": {
    "hp": 35,
    "attack": 55,
    "defense": 40,
    "specialAttack": 50,
    "specialDefense": 50,
    "speed": 90
  }
}
```

---

## Casos de Uso Implementados

Los casos de uso **encapsulan la lógica de negocio** y coordinan el flujo de datos entre la UI y los repositorios.

### 1. LoginUseCase

```kotlin
class LoginUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String): Result<FirebaseUser> {
        // Validaciones
        if (email.isBlank()) {
            return Result.Error(Exception("El email no puede estar vacío"))
        }
        if (password.isBlank()) {
            return Result.Error(Exception("La contraseña no puede estar vacía"))
        }
        if (!isValidEmail(email)) {
            return Result.Error(Exception("El email no es válido"))
        }
        if (password.length < 6) {
            return Result.Error(Exception("La contraseña debe tener al menos 6 caracteres"))
        }
        
        // Delega al repositorio
        return authRepository.login(email, password)
    }
}
```

**Responsabilidades**:
- ✅ Validar entrada del usuario
- ✅ Aplicar reglas de negocio
- ✅ Delegar operaciones al repositorio
- ✅ Retornar Result con datos o error

### 2. RegisterUseCase

```kotlin
class RegisterUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(
        email: String,
        password: String,
        confirmPassword: String
    ): Result<FirebaseUser> {
        // Validaciones adicionales
        if (password != confirmPassword) {
            return Result.Error(Exception("Las contraseñas no coinciden"))
        }
        // ... más validaciones
        return authRepository.register(email, password)
    }
}
```

### 3. GetPokemonsUseCase

```kotlin
class GetPokemonsUseCase(private val pokemonRepository: PokemonRepository) {
    operator fun invoke(): Flow<Result<List<Pokemon>>> {
        return pokemonRepository.getPokemons()
    }
}
```

**Nota**: En este caso, el caso de uso simplemente delega al repositorio, pero podría agregar lógica adicional como:
- Filtrado de Pokemon
- Ordenamiento
- Caché local
- Transformaciones de datos

### 4. AddPokemonUseCase

```kotlin
class AddPokemonUseCase(private val pokemonRepository: PokemonRepository) {
    suspend operator fun invoke(pokemon: Pokemon): Result<Unit> {
        // Validaciones
        if (pokemon.name.isBlank()) {
            return Result.Error(Exception("El nombre del Pokémon no puede estar vacío"))
        }
        if (pokemon.pokedexNumber <= 0) {
            return Result.Error(Exception("El número de Pokédex debe ser mayor a 0"))
        }
        // ... más validaciones
        
        return pokemonRepository.addPokemon(pokemon)
    }
}
```

### 5. DeletePokemonUseCase

```kotlin
class DeletePokemonUseCase(private val pokemonRepository: PokemonRepository) {
    suspend operator fun invoke(pokedexNumber: Int): Result<Unit> {
        if (pokedexNumber <= 0) {
            return Result.Error(Exception("Número de Pokédex inválido"))
        }
        return pokemonRepository.deletePokemon(pokedexNumber)
    }
}
```

### 6. UpdatePokemonUseCase

```kotlin
class UpdatePokemonUseCase(private val pokemonRepository: PokemonRepository) {
    suspend operator fun invoke(pokemon: Pokemon): Result<Unit> {
        // Validaciones similares a AddPokemonUseCase
        return pokemonRepository.updatePokemon(pokemon)
    }
}
```

### 7. LogoutUseCase

```kotlin
class LogoutUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(): Result<Unit> {
        return authRepository.logout()
    }
}
```

---

## Integración con Firebase Firestore

### Estructura de la Colección

```
/pokemons (colección)
  /{userId}_{pokedexNumber} (documento)
    - userId: "abc123"
    - pokedexNumber: 25
    - name: "Pikachu"
    - types: "Eléctrico"
    - description: "..."
    - heightM: 0.4
    - weightKg: 6.0
    - imageUrl: "https://..."
    - moves: ["Impactrueno", "Onda Trueno"]
    - stats: {
        hp: 35,
        attack: 55,
        ...
      }
```

### Reglas de Seguridad de Firestore

Para proteger los datos, configura estas reglas en Firebase Console:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Regla para la colección de Pokemon
    match /pokemons/{pokemonId} {
      // Solo usuarios autenticados pueden leer/escribir
      allow read, write: if request.auth != null 
        && request.auth.uid == resource.data.userId;
      
      // Al crear, asegurar que userId coincide con el usuario autenticado
      allow create: if request.auth != null 
        && request.auth.uid == request.resource.data.userId;
    }
  }
}
```

**Estas reglas garantizan**:
- ✅ Solo usuarios autenticados pueden acceder a Firestore
- ✅ Los usuarios solo pueden ver/modificar sus propios Pokemon
- ✅ No se puede manipular Pokemon de otros usuarios

---

## Uso en ViewModels

### Ejemplo: PokemonListViewModel (ACTUALIZADO)

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
                    // Pokemon eliminado exitosamente
                    // El listener de Firestore actualizará automáticamente la lista
                }
                is Result.Error -> {
                    // Manejar error
                    _pokemonState.value = Result.Error(result.exception)
                }
                Result.Loading -> { /* No aplica aquí */ }
            }
        }
    }
}
```

### Ejemplo: LoginScreenViewModel (ACTUALIZADO)

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

### Ejemplo: Composable consumiendo el ViewModel

```kotlin
@Composable
fun PokemonListScreen(viewModel: PokemonListViewModel) {
    val pokemonState by viewModel.pokemonState.collectAsState()

    when (val state = pokemonState) {
        is Result.Loading -> {
            // Mostrar loading
            CircularProgressIndicator()
        }
        is Result.Success -> {
            // Mostrar lista de Pokemon
            LazyColumn {
                items(state.data) { pokemon ->
                    PokemonCard(pokemon = pokemon)
                }
            }
        }
        is Result.Error -> {
            // Mostrar error
            Text(text = "Error: ${state.exception.message}")
        }
    }
}
```

---

## Ejemplo Completo de Flujo

### Flujo de Agregar un Pokemon

```
1. Usuario completa formulario y presiona "Guardar"
   ↓
2. AddPokemonScreen llama a viewModel.addPokemon(pokemon)
   ↓
3. AddPokemonScreenViewModel.addPokemon() ejecuta:
   viewModelScope.launch {
       val result = addPokemonUseCase(pokemon)
       // Maneja resultado
   }
   ↓
4. AddPokemonUseCase.invoke(pokemon) valida:
   - Nombre no vacío
   - Número de Pokédex válido
   - Altura y peso positivos
   - Etc.
   Si válido → llama a pokemonRepository.addPokemon(pokemon)
   Si inválido → retorna Result.Error
   ↓
5. FirestorePokemonRepository.addPokemon() 
   - Obtiene userId del usuario autenticado
   - Crea documento en Firestore
   - Retorna Result.Success o Result.Error
   ↓
6. El SnapshotListener en getPokemons() detecta el cambio
   ↓
7. PokemonListScreen se actualiza automáticamente
```

### Flujo de Login

```
1. Usuario ingresa email y password, presiona "Iniciar Sesión"
   ↓
2. LoginScreen llama a viewModel.login(email, password)
   ↓
3. LoginScreenViewModel.login() ejecuta:
   viewModelScope.launch {
       _loginState.value = Result.Loading
       val result = loginUseCase(email, password)
       _loginState.value = result
   }
   ↓
4. LoginUseCase.invoke() valida:
   - Email no vacío y formato válido
   - Password no vacío y longitud mínima
   Si válido → llama a authRepository.login()
   Si inválido → retorna Result.Error
   ↓
5. FirebaseAuthRepository.login()
   - Llama a firebaseAuth.signInWithEmailAndPassword()
   - Espera respuesta con await()
   - Retorna Result.Success(user) o Result.Error(exception)
   ↓
6. LoginScreen observa loginState y reacciona:
   - Loading → Muestra ProgressIndicator
   - Success → Navega a PokemonListScreen
   - Error → Muestra mensaje de error
```

---

## Buenas Prácticas

### 1. Inyección de Dependencias

**Actualmente** las dependencias se crean manualmente. **Recomendación**: Usar Hilt o Koin.

**Ejemplo con constructor manual**:
```kotlin
val authRepository: AuthRepository = FirebaseAuthRepository()
val loginUseCase = LoginUseCase(authRepository)
val viewModel = LoginScreenViewModel(loginUseCase)
```

**Ejemplo con Hilt** (futuro):
```kotlin
@HiltViewModel
class LoginScreenViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() { ... }
```

### 2. Manejo de Errores

- ✅ Siempre envuelve operaciones en `try-catch`
- ✅ Usa la clase `Result` para comunicar estado
- ✅ Proporciona mensajes de error descriptivos en español
- ✅ Valida en el caso de uso antes de llamar al repositorio

### 3. Coroutines y Flow

- ✅ Usa `viewModelScope.launch` para operaciones asíncronas
- ✅ Usa `Flow` para datos que cambian con el tiempo
- ✅ Usa `await()` para convertir callbacks de Firebase
- ✅ Usa `callbackFlow` para convertir listeners en Flow

### 4. Seguridad

- ✅ Siempre verifica autenticación antes de operaciones con Firestore
- ✅ Configura reglas de seguridad en Firestore
- ✅ Nunca expongas credenciales en el código
- ✅ Usa `userId` para separar datos entre usuarios

### 5. Separación de Responsabilidades

- **ViewModel**: Gestiona estado de UI y ciclo de vida
- **UseCase**: Lógica de negocio y validaciones
- **Repository**: Acceso a datos (Firebase, Room, API)
- **UI**: Solo presenta datos y captura eventos

### 6. Testing

```kotlin
// Test de UseCase (fácil de testear)
@Test
fun `login con email vacío debe retornar error`() = runBlocking {
    val mockRepository = mock<AuthRepository>()
    val useCase = LoginUseCase(mockRepository)
    
    val result = useCase("", "password123")
    
    assertTrue(result is Result.Error)
}
```

---

## Recursos Adicionales

- 📘 [Firebase Authentication Docs](https://firebase.google.com/docs/auth)
- 📘 [Cloud Firestore Docs](https://firebase.google.com/docs/firestore)
- 📘 [Kotlin Coroutines Guide](https://kotlinlang.org/docs/coroutines-guide.html)
- 📘 [Android Architecture Components](https://developer.android.com/topic/architecture)

---

## Conclusión

Esta implementación proporciona:

- ✅ **Arquitectura limpia y escalable**
- ✅ **Separación clara de responsabilidades**
- ✅ **Manejo robusto de errores**
- ✅ **Sincronización en tiempo real con Firestore**
- ✅ **Autenticación segura con Firebase Auth**
- ✅ **Código testeable y mantenible**

Para integrar esta implementación en los ViewModels existentes, sigue los ejemplos en la sección "Uso en ViewModels".
