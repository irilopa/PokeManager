package org.ivan.pokmanager.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.ivan.pokmanager.domain.model.Pokemon
import org.ivan.pokmanager.domain.repository.AuthRepository
import org.ivan.pokmanager.domain.usecase.GetPokemonByIdUseCase
import org.ivan.pokmanager.presentation.state.UiState

/**
 * Carga un Pokemon concreto del equipo por el id de su documento en Firestore.
 * `Success(null)` significa que el documento no existe.
 */
class PokemonDetailViewModel(
    private val pokemonId: String,
    private val getPokemonByIdUseCase: GetPokemonByIdUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<Pokemon?>>(UiState.Loading)
    val uiState: StateFlow<UiState<Pokemon?>> = _uiState.asStateFlow()

    init {
        load()
    }

    /** Vuelve a intentar la carga. */
    fun retry() = load()

    private fun load() {
        val uid = authRepository.currentUserId()
        if (uid == null) {
            _uiState.value = UiState.Error(PokemonListViewModel.NO_SESSION_MESSAGE)
            return
        }
        _uiState.value = UiState.Loading
        viewModelScope.launch {
            _uiState.value = getPokemonByIdUseCase(uid, pokemonId).fold(
                onSuccess = { UiState.Success(it) },
                onFailure = { UiState.Error(LOAD_ERROR_MESSAGE) }
            )
        }
    }

    companion object {
        const val LOAD_ERROR_MESSAGE = "No se pudo cargar el Pokémon. Comprueba tu conexión."
    }
}
