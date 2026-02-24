package org.ivan.pokmanager.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
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
    private val deletePokemonUseCase: DeletePokemonUseCase,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val uid = firebaseAuth.currentUser?.uid

    val pokemons: StateFlow<List<Pokemon>> =
        if (uid != null) {
            getPokemonUseCase(uid).stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )
        } else {
            MutableStateFlow(emptyList())
        }

    fun removePokemon(pokemon: Pokemon) {
        if (pokemon.id.isBlank() || uid == null) return

        viewModelScope.launch {
            deletePokemonUseCase(uid, pokemon.id)
        }
    }
}