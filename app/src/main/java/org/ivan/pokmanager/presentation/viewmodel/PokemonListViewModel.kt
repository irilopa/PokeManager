package org.ivan.pokmanager.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.ivan.pokmanager.domain.model.Pokemon
import org.ivan.pokmanager.domain.usecase.DeletePokemonUseCase
import org.ivan.pokmanager.domain.usecase.GetPokemonUseCase

class PokemonListViewModel(
    private val getPokemonUseCase: GetPokemonUseCase,
    private val deletePokemonUseCase: DeletePokemonUseCase
) : ViewModel() {

    private val _pokemons = MutableStateFlow<List<Pokemon>>(emptyList())
    val pokemons: StateFlow<List<Pokemon>> = _pokemons

    init {
        // Nos suscribimos a Firestore y mantenemos la lista en memoria como StateFlow para la UI
        viewModelScope.launch {
            getPokemonUseCase()
                .onEach { _pokemons.value = it }
                .catch { _pokemons.value = emptyList() }
                .collect { /* handled in onEach */ }
        }
    }

    fun removePokemon(pokemon: Pokemon) {
        // En Firestore borramos por id del documento.
        if (pokemon.id.isBlank()) return
        viewModelScope.launch {
            deletePokemonUseCase(pokemon.id)
        }
    }
}