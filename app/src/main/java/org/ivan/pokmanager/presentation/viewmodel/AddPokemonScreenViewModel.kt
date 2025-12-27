package org.ivan.pokmanager.presentation.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

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

    }
}