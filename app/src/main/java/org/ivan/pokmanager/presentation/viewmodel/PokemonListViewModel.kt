package org.ivan.pokmanager.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.ivan.pokmanager.domain.model.Pokemon
import org.ivan.pokmanager.domain.usecase.DeletePokemonUseCase
import org.ivan.pokmanager.domain.usecase.GetPokemonUseCase

class PokemonListViewModel(
    private val getPokemonUseCase: GetPokemonUseCase,
    private val deletePokemonUseCase: DeletePokemonUseCase
) : ViewModel() {

    private val _pokemons = getPokemonUseCase().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )
    val pokemons: StateFlow<List<Pokemon>> = _pokemons

    fun removePokemon(pokemon: Pokemon) {
        // En Firestore borramos por id del documento.
        if (pokemon.id.isBlank()) return
        viewModelScope.launch {
            deletePokemonUseCase(pokemon.id)
        }
    }
}