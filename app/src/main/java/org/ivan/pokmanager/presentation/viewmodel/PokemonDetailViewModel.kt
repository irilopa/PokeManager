package org.ivan.pokmanager.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.ivan.pokmanager.domain.model.Pokemon
import org.ivan.pokmanager.domain.usecase.GetPokemonUseCase

class PokemonDetailViewModel(
    private val pokemonId: Int,
    private val getPokemonUseCase: GetPokemonUseCase,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val uid = firebaseAuth.currentUser?.uid

    val pokemon: StateFlow<Pokemon?> = if (uid != null) {
        getPokemonUseCase(uid)
            .map { list -> list.firstOrNull { it.pokedexNumber == pokemonId } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    } else {
        MutableStateFlow(null)
    }
}

