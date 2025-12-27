package org.ivan.pokmanager.presentation.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.ivan.pokmanager.data.model.Pokemon
import org.ivan.pokmanager.data.model.PokemonStats
import org.ivan.pokmanager.data.model.repository.PokemonRepository

class AddPokemonScreenViewModel : ViewModel() {


    val pokemonTypes = listOf(
        "Planta", "Fuego", "Agua", "Eléctrico", "Hielo", "Lucha", "Veneno", "Tierra",
        "Roca", "Volador", "Psíquico", "Bicho", "Dragón", "Siniestro", "Fantasma",
        "Acero", "Hada", "Normal"
    )

    private val _name = MutableStateFlow("")

    // Tipos separados para cada menú desplegable
    private val _primaryType = MutableStateFlow("")
    private val _secondaryType = MutableStateFlow("")

    private val _level = MutableStateFlow("")
    private val _hp = MutableStateFlow("")
    private val _attack = MutableStateFlow("")

    private val _notes = MutableStateFlow("")


    val name = _name.asStateFlow()
    val primaryType = _primaryType.asStateFlow()
    val secondaryType = _secondaryType.asStateFlow()
    val level = _level.asStateFlow()
    val hp = _hp.asStateFlow()
    val attack = _attack.asStateFlow()
    val notes = _notes.asStateFlow()


    fun setName(newName: String) {
        _name.value = newName
    }

    fun setPrimaryTypeSelected(type: String) {
        _primaryType.value = type
    }

    fun setSecondaryTypeSelected(type: String) {
        _secondaryType.value = type
    }


    fun setLevel(newLevel: String) {
        if (newLevel.all { it.isDigit() }) {
            _level.value = newLevel
        }
    }

    fun setHp(newHp: String) {
        if (newHp.all { it.isDigit() }) {
            _hp.value = newHp
        }
    }

    fun setAttack(newAttack: String) {
        if (newAttack.all { it.isDigit() }) {
            _attack.value = newAttack
        }
    }

    fun setNotes(newNote: String) {
        _notes.value = newNote
    }


    fun savePokemon() {
        val finalName = _name.value
        // Convertimos Strings a Int de forma segura
        val finalLevel = _level.value.toIntOrNull() ?: 1
        val finalHp = _hp.value.toIntOrNull() ?: 10
        val finalAttack = _attack.value.toIntOrNull() ?: 5

        // Construimos el String de tipos "Fuego / Volador"
        val typeString = if (_secondaryType.value.isNotBlank()) {
            "${_primaryType.value} / ${_secondaryType.value}"
        } else {
            _primaryType.value
        }
        // 2. Creamos el objeto COMPLETO
        val newPokemon = Pokemon(
            pokedexNumber = (System.currentTimeMillis() / 1000).toInt(), // ID "único" basado en tiempo
            name = finalName,
            types = typeString,
            description = "Pokémon registrado manualmente por el entrenador.", // Default
            heightM = 0.0, // Default
            weightKg = 0.0, // Default
            stats = PokemonStats(
                hp = finalHp,
                attack = finalAttack,
                defense = 0, // Default
                specialAttack = 0, // Default
                specialDefense = 0, // Default
                speed = 0 // Default
            ),
            imageUrl = "",
            moves = emptyList()
        )

        // 3. ¡Guardamos en la "BBDD"!
        PokemonRepository.addPokemon(newPokemon)
    }
}