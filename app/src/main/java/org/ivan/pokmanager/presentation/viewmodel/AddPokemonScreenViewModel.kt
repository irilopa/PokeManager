package org.ivan.pokmanager.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.ivan.pokmanager.domain.model.Pokemon
import org.ivan.pokmanager.domain.model.PokemonStats
import org.ivan.pokmanager.domain.usecase.SavePokemonUseCase

class AddPokemonScreenViewModel(
    private val savePokemonUseCase: SavePokemonUseCase
) : ViewModel() {

    private val _name = MutableStateFlow("")
    private val _pokedexNumber = MutableStateFlow("")
    private val _primaryType = MutableStateFlow("")
    private val _secondaryType = MutableStateFlow("")
    private val _level = MutableStateFlow("")
    private val _hp = MutableStateFlow("")
    private val _attack = MutableStateFlow("")
    private val _notes = MutableStateFlow("")

    val name = _name.asStateFlow()
    val pokedexNumber = _pokedexNumber.asStateFlow()
    val primaryType = _primaryType.asStateFlow()
    val secondaryType = _secondaryType.asStateFlow()
    val level = _level.asStateFlow()
    val hp = _hp.asStateFlow()
    val attack = _attack.asStateFlow()
    val notes = _notes.asStateFlow()

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

    fun savePokemon() {
        val pokedexNumberInt = _pokedexNumber.value.toIntOrNull()
        val resolvedPokedexNumber = pokedexNumberInt ?: (System.currentTimeMillis() / 1000).toInt()

        val pokemon = Pokemon(
            pokedexNumber = resolvedPokedexNumber,
            name = _name.value,
            types = buildTypes(),
            description = _notes.value,
            heightM = 0.0,
            weightKg = 0.0,
            stats = PokemonStats(
                hp = _hp.value.toIntOrNull() ?: 10,
                attack = _attack.value.toIntOrNull() ?: 5,
                defense = 0,
                specialAttack = 0,
                specialDefense = 0,
                speed = 0
            ),
            imageUrl = buildOfficialArtworkUrl(pokedexNumberInt),
            moves = emptyList()
        )

        viewModelScope.launch {
            savePokemonUseCase(pokemon)
        }
    }

    private fun buildOfficialArtworkUrl(pokedexNumber: Int?): String {
        // Si no hay número válido, no forzamos URL.
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