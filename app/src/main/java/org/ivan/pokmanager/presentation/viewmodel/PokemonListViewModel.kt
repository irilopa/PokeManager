package org.ivan.pokmanager.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.ivan.pokmanager.domain.filter.PokemonFilter
import org.ivan.pokmanager.domain.model.Pokemon
import org.ivan.pokmanager.domain.repository.AuthRepository
import org.ivan.pokmanager.domain.type.PokemonType
import org.ivan.pokmanager.domain.usecase.DeletePokemonUseCase
import org.ivan.pokmanager.domain.usecase.GetPokemonUseCase
import org.ivan.pokmanager.presentation.state.UiState

/**
 * Provee el equipo Pokemon del usuario, permite eliminar y cerrar sesion.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class PokemonListViewModel(
    private val getPokemonUseCase: GetPokemonUseCase,
    private val deletePokemonUseCase: DeletePokemonUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val retryTrigger = MutableStateFlow(0)

    /** Estado de la lista: cargando, con datos (posiblemente vacia) o error. */
    val uiState: StateFlow<UiState<List<Pokemon>>> = retryTrigger
        .flatMapLatest {
            val uid = authRepository.currentUserId()
            if (uid == null) {
                flowOf(UiState.Error(NO_SESSION_MESSAGE))
            } else {
                getPokemonUseCase(uid)
                    .map<List<Pokemon>, UiState<List<Pokemon>>> { UiState.Success(it) }
                    .onStart { emit(UiState.Loading) }
                    .catch { emit(UiState.Error(LOAD_ERROR_MESSAGE)) }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UiState.Loading)

    private val _query = MutableStateFlow("")
    private val _typeFilter = MutableStateFlow<PokemonType?>(null)

    /** Texto del buscador, tal cual lo escribe el usuario. */
    val query: StateFlow<String> = _query.asStateFlow()
    /** Tipo seleccionado en los chips, o null si no hay filtro. */
    val typeFilter: StateFlow<PokemonType?> = _typeFilter.asStateFlow()

    /** Pokemon del equipo que cumplen la busqueda (con espera de 300 ms) y el tipo. */
    @OptIn(FlowPreview::class)
    val visiblePokemons: StateFlow<List<Pokemon>> = combine(
        uiState,
        _query.debounce { if (it.isEmpty()) 0L else SEARCH_DEBOUNCE_MS },
        _typeFilter
    ) { state, query, type ->
        val team = (state as? UiState.Success)?.data.orEmpty()
        PokemonFilter.apply(team, query, type)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setQuery(value: String) {
        _query.value = value
    }

    /** Selecciona un tipo; si ya estaba seleccionado, lo quita. */
    fun toggleTypeFilter(type: PokemonType) {
        _typeFilter.value = if (_typeFilter.value == type) null else type
    }

    fun clearFilters() {
        _query.value = ""
        _typeFilter.value = null
    }

    private val _deleteError = MutableStateFlow<String?>(null)
    /** Mensaje si falla la eliminacion de un Pokemon. */
    val deleteError: StateFlow<String?> = _deleteError

    /** Vuelve a suscribirse a la lista tras un error. */
    fun retry() {
        retryTrigger.value++
    }

    /** Elimina un Pokemon del equipo. */
    fun removePokemon(pokemon: Pokemon) {
        val uid = authRepository.currentUserId() ?: return
        if (pokemon.id.isBlank()) return
        viewModelScope.launch {
            deletePokemonUseCase(uid, pokemon.id)
                .onFailure { _deleteError.value = "No se pudo liberar a ${pokemon.name}" }
        }
    }

    fun clearDeleteError() {
        _deleteError.value = null
    }

    /** Cierra la sesion de Firebase. */
    fun logout() {
        authRepository.logout()
    }

    companion object {
        const val NO_SESSION_MESSAGE = "Tu sesión ha caducado. Vuelve a iniciar sesión."
        const val SEARCH_DEBOUNCE_MS = 300L
        const val LOAD_ERROR_MESSAGE = "No se pudo cargar tu equipo. Comprueba tu conexión."
    }
}
