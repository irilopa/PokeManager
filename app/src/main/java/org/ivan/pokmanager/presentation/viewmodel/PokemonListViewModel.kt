package org.ivan.pokmanager.presentation.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.StateFlow

import org.ivan.pokmanager.data.model.Pokemon
import org.ivan.pokmanager.data.model.repository.PokemonRepository

class PokemonListViewModel : ViewModel() {
    // Leemos directamente del Repositorio (Single Source of Truth)
    val pokemons: StateFlow<List<Pokemon>> = PokemonRepository.pokemons

    fun removePokemon(pokemon: Pokemon) {
        PokemonRepository.deletePokemon(pokemon.pokedexNumber)
    }
}