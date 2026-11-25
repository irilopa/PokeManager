package org.ivan.pokmanager.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.ivan.pokmanager.data.model.remote.PokeApiErrors
import org.ivan.pokmanager.data.model.remote.dto.toPokemonStats
import org.ivan.pokmanager.data.model.remote.toPokeApiName
import org.ivan.pokmanager.data.model.remote.dto.toSpanish
import org.ivan.pokmanager.data.model.repository.PokemonApiService
import org.ivan.pokmanager.domain.model.Pokemon
import org.ivan.pokmanager.domain.model.PokemonStats
import org.ivan.pokmanager.domain.repository.AuthRepository
import org.ivan.pokmanager.domain.type.PokemonType
import org.ivan.pokmanager.domain.usecase.GetPokemonByIdUseCase
import org.ivan.pokmanager.domain.usecase.SavePokemonUseCase
import org.ivan.pokmanager.domain.usecase.UpdatePokemonUseCase
import org.ivan.pokmanager.domain.validation.PokemonField
import org.ivan.pokmanager.domain.validation.PokemonForm
import org.ivan.pokmanager.domain.validation.PokemonFormValidator
import org.ivan.pokmanager.presentation.state.SaveState
import org.ivan.pokmanager.presentation.state.UiState

/**
 * Administra el formulario de alta de Pokemon, su validacion, el guardado y la consulta a la PokeAPI.
 */
class AddPokemonScreenViewModel(
    private val savePokemonUseCase: SavePokemonUseCase,
    private val pokemonApiService: PokemonApiService,
    private val authRepository: AuthRepository,
    private val getPokemonByIdUseCase: GetPokemonByIdUseCase,
    private val updatePokemonUseCase: UpdatePokemonUseCase,
    /** Id del documento a editar; vacio para crear un Pokemon nuevo. */
    private val pokemonId: String = ""
) : ViewModel() {

    /** True si el formulario edita un Pokemon existente. */
    val isEditing: Boolean = pokemonId.isNotBlank()

    /** Pokemon original en modo edicion; se conservan los campos que el formulario no muestra. */
    private var original: Pokemon? = null

    private val _loadState = MutableStateFlow<UiState<Unit>>(
        if (isEditing) UiState.Loading else UiState.Success(Unit)
    )
    /** Carga del Pokemon a editar (siempre Success al crear uno nuevo). */
    val loadState: StateFlow<UiState<Unit>> = _loadState.asStateFlow()

    private val _form = MutableStateFlow(PokemonForm())
    private val _hp = MutableStateFlow("")
    private val _attack = MutableStateFlow("")
    private val _errors = MutableStateFlow<Map<PokemonField, String>>(emptyMap())
    private val _saveState = MutableStateFlow<SaveState>(SaveState.Idle)

    private val _isLoadingFromApi = MutableStateFlow(false)
    private val _apiError = MutableStateFlow<String?>(null)
    private val _apiSuccess = MutableStateFlow(false)

    /** Stats completas obtenidas de la PokeAPI para el nombre actual. */
    private var fetchedStats: PokemonStats? = null

    val form: StateFlow<PokemonForm> = _form.asStateFlow()
    val hp: StateFlow<String> = _hp.asStateFlow()
    val attack: StateFlow<String> = _attack.asStateFlow()
    /** Errores de validacion por campo. */
    val errors: StateFlow<Map<PokemonField, String>> = _errors.asStateFlow()
    /** Estado del guardado. */
    val saveState: StateFlow<SaveState> = _saveState.asStateFlow()
    val isLoadingFromApi: StateFlow<Boolean> = _isLoadingFromApi.asStateFlow()
    val apiError: StateFlow<String?> = _apiError.asStateFlow()
    val apiSuccess: StateFlow<Boolean> = _apiSuccess.asStateFlow()

    /** Lista de tipos disponibles para la UI. */
    val pokemonTypes: List<String> = PokemonType.entries.map { it.spanishName }

    init {
        if (isEditing) loadExisting()
    }

    /** Vuelve a intentar cargar el Pokemon a editar. */
    fun retryLoad() {
        if (isEditing) loadExisting()
    }

    private fun loadExisting() {
        val uid = authRepository.currentUserId()
        if (uid == null) {
            _loadState.value = UiState.Error(PokemonListViewModel.NO_SESSION_MESSAGE)
            return
        }
        _loadState.value = UiState.Loading
        viewModelScope.launch {
            _loadState.value = getPokemonByIdUseCase(uid, pokemonId).fold(
                onSuccess = { pokemon ->
                    if (pokemon == null) {
                        UiState.Error("Este Pokémon ya no está en tu equipo.")
                    } else {
                        fillForm(pokemon)
                        UiState.Success(Unit)
                    }
                },
                onFailure = { UiState.Error(PokemonDetailViewModel.LOAD_ERROR_MESSAGE) }
            )
        }
    }

    private fun fillForm(pokemon: Pokemon) {
        original = pokemon
        fetchedStats = pokemon.stats
        val types = pokemon.types.split("/").map { it.trim() }.filter { it.isNotEmpty() }
        _form.value = PokemonForm(
            name = pokemon.name,
            pokedexNumber = pokemon.pokedexNumber.takeIf { it > 0 }?.toString().orEmpty(),
            primaryType = types.getOrElse(0) { "" },
            secondaryType = types.getOrElse(1) { "" },
            level = pokemon.level.takeIf { it > 0 }?.toString().orEmpty(),
            notes = pokemon.description
        )
        _hp.value = pokemon.stats.hp.toString()
        _attack.value = pokemon.stats.attack.toString()
    }

    fun setName(value: String) {
        if (value != _form.value.name && !isEditing) fetchedStats = null
        updateForm(PokemonField.NAME) { it.copy(name = value) }
    }

    fun setPokedexNumber(value: String) {
        if (value.all { it.isDigit() }) {
            updateForm(PokemonField.POKEDEX_NUMBER) { it.copy(pokedexNumber = value) }
        }
    }

    fun setPrimaryType(value: String) {
        updateForm(PokemonField.PRIMARY_TYPE, PokemonField.SECONDARY_TYPE) { it.copy(primaryType = value) }
    }

    fun setSecondaryType(value: String) {
        updateForm(PokemonField.SECONDARY_TYPE) { it.copy(secondaryType = value) }
    }

    fun setLevel(value: String) {
        if (value.all { it.isDigit() }) {
            updateForm(PokemonField.LEVEL) { it.copy(level = value) }
        }
    }

    fun setHp(value: String) {
        if (value.all { it.isDigit() }) _hp.value = value
    }

    fun setAttack(value: String) {
        if (value.all { it.isDigit() }) _attack.value = value
    }

    fun setNotes(value: String) {
        updateForm(PokemonField.NOTES) { it.copy(notes = value) }
    }

    /** Actualiza el formulario y borra el error de los campos tocados. */
    private fun updateForm(vararg fields: PokemonField, transform: (PokemonForm) -> PokemonForm) {
        _form.value = transform(_form.value)
        if (fields.any { it in _errors.value }) _errors.value = _errors.value - fields.toSet()
        if (_saveState.value is SaveState.Error) _saveState.value = SaveState.Idle
    }

    /** Busca el Pokemon por nombre en la PokeAPI y autocompleta los campos principales. */
    fun fetchPokemonFromApi() {
        val query = toPokeApiName(_form.value.name)
        if (query.isBlank()) return

        _isLoadingFromApi.value = true
        _apiError.value = null
        _apiSuccess.value = false

        viewModelScope.launch {
            runCatching { pokemonApiService.getPokemonByName(query) }
                .onSuccess { response ->
                    val stats = response.toPokemonStats()
                    fetchedStats = stats
                    _hp.value = stats.hp.toString()
                    _attack.value = stats.attack.toString()

                    val typeNames = response.types
                        .sortedBy { it.slot }
                        .map { it.type.toSpanish() }
                    _form.value = _form.value.copy(
                        pokedexNumber = response.id.toString(),
                        primaryType = typeNames.getOrElse(0) { "" },
                        secondaryType = typeNames.getOrElse(1) { "" }
                    )
                    _errors.value = _errors.value - setOf(
                        PokemonField.POKEDEX_NUMBER, PokemonField.PRIMARY_TYPE, PokemonField.SECONDARY_TYPE
                    )
                    _apiSuccess.value = true
                }
                .onFailure { error ->
                    _apiError.value = PokeApiErrors.messageFor(error)
                }

            _isLoadingFromApi.value = false
        }
    }

    fun clearApiError() {
        _apiError.value = null
    }

    fun clearApiSuccess() {
        _apiSuccess.value = false
    }

    /**
     * Valida el formulario y guarda el Pokemon. Ignora pulsaciones mientras ya se esta guardando
     * o tras un guardado correcto, para no crear duplicados.
     */
    fun savePokemon() {
        val current = _saveState.value
        if (current is SaveState.Saving || current is SaveState.Success) return

        val form = _form.value
        val validationErrors = PokemonFormValidator.validate(form)
        _errors.value = validationErrors
        if (validationErrors.isNotEmpty()) return

        val uid = authRepository.currentUserId()
        if (uid == null) {
            _saveState.value = SaveState.Error(PokemonListViewModel.NO_SESSION_MESSAGE)
            return
        }

        _saveState.value = SaveState.Saving

        val pokedexNumber = form.pokedexNumber.trim().toIntOrNull() ?: 0
        val hp = _hp.value.toIntOrNull() ?: DEFAULT_HP
        val attack = _attack.value.toIntOrNull() ?: DEFAULT_ATTACK
        val stats = fetchedStats?.copy(hp = hp, attack = attack)
            ?: PokemonStats(hp = hp, attack = attack)

        val pokemon = (original ?: Pokemon()).copy(
            pokedexNumber = pokedexNumber,
            name = form.name.trim(),
            types = buildTypes(form),
            level = form.level.trim().toInt(),
            description = form.notes.trim(),
            stats = stats,
            imageUrl = buildOfficialArtworkUrl(pokedexNumber)
        )

        viewModelScope.launch {
            val result = if (isEditing) updatePokemonUseCase(uid, pokemon) else savePokemonUseCase(uid, pokemon)
            _saveState.value = result.fold(
                onSuccess = { SaveState.Success },
                onFailure = { SaveState.Error(SAVE_ERROR_MESSAGE) }
            )
        }
    }

    private fun buildOfficialArtworkUrl(pokedexNumber: Int): String {
        if (pokedexNumber <= 0) return ""
        return "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$pokedexNumber.png"
    }

    private fun buildTypes(form: PokemonForm): String =
        if (form.secondaryType.isNotBlank()) "${form.primaryType} / ${form.secondaryType}"
        else form.primaryType

    companion object {
        const val DEFAULT_HP = 10
        const val DEFAULT_ATTACK = 5
        const val SAVE_ERROR_MESSAGE = "No se pudo guardar el Pokémon. Inténtalo de nuevo."
    }
}
