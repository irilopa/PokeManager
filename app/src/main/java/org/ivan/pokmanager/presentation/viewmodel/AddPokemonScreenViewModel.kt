package org.ivan.pokmanager.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.ivan.pokmanager.data.model.remote.dto.toSpanish
import org.ivan.pokmanager.data.model.remote.dto.toPokemonStats
import org.ivan.pokmanager.data.model.repository.PokemonApiService
import org.ivan.pokmanager.domain.model.Pokemon
import org.ivan.pokmanager.domain.model.PokemonStats
import org.ivan.pokmanager.domain.usecase.SavePokemonUseCase

class AddPokemonScreenViewModel(
    private val savePokemonUseCase: SavePokemonUseCase,
    private val pokemonApiService: PokemonApiService,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _name = MutableStateFlow("")
    private val _pokedexNumber = MutableStateFlow("")
    private val _primaryType = MutableStateFlow("")
    private val _secondaryType = MutableStateFlow("")
    private val _level = MutableStateFlow("")
    private val _hp = MutableStateFlow("")
    private val _attack = MutableStateFlow("")
    private val _notes = MutableStateFlow("")

    // Estado de búsqueda en la API
    private val _isLoadingFromApi = MutableStateFlow(false)
    private val _apiError = MutableStateFlow<String?>(null)
    private val _apiSuccess = MutableStateFlow(false)

    val name = _name.asStateFlow()
    val pokedexNumber = _pokedexNumber.asStateFlow()
    val primaryType = _primaryType.asStateFlow()
    val secondaryType = _secondaryType.asStateFlow()
    val level = _level.asStateFlow()
    val hp = _hp.asStateFlow()
    val attack = _attack.asStateFlow()
    val notes = _notes.asStateFlow()
    val isLoadingFromApi = _isLoadingFromApi.asStateFlow()
    val apiError = _apiError.asStateFlow()
    val apiSuccess = _apiSuccess.asStateFlow()

    // Lista de tipos que la UI utiliza. Mínima y estática; cambiar por fuente real si existe.
    val pokemonTypes: List<String> = listOf(
        "Normal", "Fuego", "Agua", "Planta", "Eléctrico", "Hielo", "Lucha",
        "Veneno", "Tierra", "Volador", "Psíquico", "Bicho", "Roca", "Fantasma",
        "Dragón", "Siniestro", "Acero", "Hada"
    )

    fun setName(value: String) {
        _name.value = value
    }

    fun setPokedexNumber(value: String) {
        if (value.all { it.isDigit() }) _pokedexNumber.value = value
    }

    fun setPrimaryType(value: String) {
        _primaryType.value = value
    }

    fun setSecondaryType(value: String) {
        _secondaryType.value = value
    }

    // Métodos que la UI estaba invocando (nombres distintos). Delegan a los setters existentes.
    fun setPrimaryTypeSelected(value: String) {
        setPrimaryType(value)
    }

    fun setSecondaryTypeSelected(value: String) {
        setSecondaryType(value)
    }

    fun setLevel(value: String) {
        if (value.all { it.isDigit() }) _level.value = value
    }

    fun setHp(value: String) {
        if (value.all { it.isDigit() }) _hp.value = value
    }

    fun setAttack(value: String) {
        if (value.all { it.isDigit() }) _attack.value = value
    }

    fun setNotes(value: String) {
        _notes.value = value
    }

    /** Busca el Pokémon por nombre en la PokeAPI y autocompleta todos los campos */
    fun fetchPokemonFromApi() {
        val query = _name.value.trim().lowercase()
        if (query.isBlank()) return

        _isLoadingFromApi.value = true
        _apiError.value = null
        _apiSuccess.value = false

        viewModelScope.launch {
            runCatching { pokemonApiService.getPokemonByName(query) }
                .onSuccess { response ->
                    // Número de Pokédex
                    _pokedexNumber.value = response.id.toString()

                    if (_name.value.isBlank()) _name.value = response.name

                    // Stats
                    val stats = response.toPokemonStats()
                    _hp.value = stats.hp.toString()
                    _attack.value = stats.attack.toString()

                    val typeNames = response.types
                        .sortedBy { it.slot }
                        .map { it.type.toSpanish() }
                    _primaryType.value = typeNames.getOrElse(0) { "" }
                    _secondaryType.value = typeNames.getOrElse(1) { "" }

                    _apiSuccess.value = true
                }
                .onFailure {
                    _apiError.value = "Pokémon no encontrado. Revisa el nombre."
                }

            _isLoadingFromApi.value = false
        }
    }

    fun clearApiError() {
        _apiError.value = null
    }

    fun clearApiSuccess() {
        _apiSuccess.value = false
    }

    fun savePokemon() {
        val uid = firebaseAuth.currentUser?.uid ?: return
        val pokedexNumberInt = _pokedexNumber.value.toIntOrNull()
        val resolvedPokedexNumber = pokedexNumberInt ?: (System.currentTimeMillis() / 1000).toInt()

        viewModelScope.launch {
            // Intentar cargar stats reales desde la PokeAPI si hay nombre o número de Pokédex
            val apiStats: PokemonStats? = runCatching {
                val nameOrId = if (_name.value.isNotBlank()) _name.value.lowercase().trim()
                else pokedexNumberInt?.toString()
                if (nameOrId != null) {
                    pokemonApiService.getPokemonByName(nameOrId).toPokemonStats()
                } else null
            }.getOrNull()

            val stats = apiStats ?: PokemonStats(
                hp = _hp.value.toIntOrNull() ?: 10,
                attack = _attack.value.toIntOrNull() ?: 5,
                defense = 0,
                specialAttack = 0,
                specialDefense = 0,
                speed = 0
            )

            val pokemon = Pokemon(
                pokedexNumber = resolvedPokedexNumber,
                name = _name.value,
                types = buildTypes(),
                description = _notes.value,
                heightM = 0.0,
                weightKg = 0.0,
                stats = stats,
                imageUrl = buildOfficialArtworkUrl(pokedexNumberInt),
                moves = emptyList()
            )

            savePokemonUseCase(uid, pokemon)
        }
    }

    private fun buildOfficialArtworkUrl(pokedexNumber: Int?): String {
        if (pokedexNumber == null || pokedexNumber <= 0) return ""
        return "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$pokedexNumber.png"
    }

    private fun buildTypes(): String {
        return if (_secondaryType.value.isNotBlank()) {
            "${_primaryType.value} / ${_secondaryType.value}"
        } else {
            _primaryType.value
        }
    }
}