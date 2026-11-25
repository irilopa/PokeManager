package org.ivan.pokmanager.viewmodel

import kotlinx.coroutines.CompletableDeferred
import org.ivan.pokmanager.MainDispatcherRule
import org.ivan.pokmanager.data.model.remote.PokeApiErrors
import org.ivan.pokmanager.data.model.remote.dto.PokemonResponse
import org.ivan.pokmanager.data.model.remote.dto.StatNameDTO
import org.ivan.pokmanager.data.model.remote.dto.StatResponseDTO
import org.ivan.pokmanager.data.model.remote.dto.TypeNameDTO
import org.ivan.pokmanager.data.model.remote.dto.TypeSlotDTO
import org.ivan.pokmanager.domain.model.Pokemon
import org.ivan.pokmanager.domain.model.PokemonStats
import org.ivan.pokmanager.domain.usecase.GetPokemonByIdUseCase
import org.ivan.pokmanager.domain.usecase.SavePokemonUseCase
import org.ivan.pokmanager.domain.usecase.UpdatePokemonUseCase
import org.ivan.pokmanager.domain.validation.PokemonForm
import org.ivan.pokmanager.domain.validation.PokemonField
import org.ivan.pokmanager.fakes.FakeAuthRepository
import org.ivan.pokmanager.fakes.FakePokemonApiService
import org.ivan.pokmanager.fakes.FakePokemonRepository
import org.ivan.pokmanager.presentation.state.SaveState
import org.ivan.pokmanager.presentation.state.UiState
import org.ivan.pokmanager.presentation.viewmodel.AddPokemonScreenViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.net.UnknownHostException

class AddPokemonScreenViewModelTest {

    @get:Rule
    val mainRule = MainDispatcherRule()

    private val auth = FakeAuthRepository()
    private val repo = FakePokemonRepository()
    private val api = FakePokemonApiService(
        mapOf(
            "pikachu" to PokemonResponse(
                id = 25,
                name = "pikachu",
                stats = listOf(
                    StatResponseDTO(35, StatNameDTO("hp")),
                    StatResponseDTO(55, StatNameDTO("attack")),
                    StatResponseDTO(90, StatNameDTO("speed"))
                ),
                types = listOf(TypeSlotDTO(1, TypeNameDTO("electric")))
            )
        )
    )

    private fun createViewModel(pokemonId: String = "") = AddPokemonScreenViewModel(
        SavePokemonUseCase(repo), api, auth, GetPokemonByIdUseCase(repo), UpdatePokemonUseCase(repo), pokemonId
    )

    private val vm = createViewModel()

    private fun fillValidForm() {
        vm.setName("Pikachu")
        vm.setPrimaryType("Eléctrico")
        vm.setLevel("25")
        vm.setNotes("Capturado en el Bosque Verde")
    }

    @Test
    fun `formulario invalido muestra errores y no guarda`() {
        vm.setLevel("150")
        vm.setPrimaryType("Agua")
        vm.setSecondaryType("Agua")

        vm.savePokemon()

        assertEquals(
            setOf(PokemonField.NAME, PokemonField.LEVEL, PokemonField.SECONDARY_TYPE),
            vm.errors.value.keys
        )
        assertEquals(SaveState.Idle, vm.saveState.value)
        assertEquals(0, repo.saveCalls)
    }

    @Test
    fun `corregir un campo borra su error`() {
        vm.savePokemon()
        assertTrue(PokemonField.NAME in vm.errors.value)

        vm.setName("Pikachu")

        assertTrue(PokemonField.NAME !in vm.errors.value)
    }

    @Test
    fun `guardado correcto termina en Success y crea el pokemon`() {
        fillValidForm()

        vm.savePokemon()

        assertEquals(SaveState.Success, vm.saveState.value)
        val saved = repo.pokemonsOf("user-1").single()
        assertEquals("Pikachu", saved.name)
        assertEquals(25, saved.level)
        assertEquals("Capturado en el Bosque Verde", saved.description)
    }

    @Test
    fun `sin numero de pokedex no se inventa uno`() {
        fillValidForm()

        vm.savePokemon()

        val saved = repo.pokemonsOf("user-1").single()
        assertEquals(0, saved.pokedexNumber)
        assertEquals("", saved.imageUrl)
    }

    @Test
    fun `mientras guarda el estado es Saving`() {
        repo.saveGate = CompletableDeferred()
        fillValidForm()

        vm.savePokemon()
        assertEquals(SaveState.Saving, vm.saveState.value)

        repo.saveGate!!.complete(Unit)
        assertEquals(SaveState.Success, vm.saveState.value)
    }

    @Test
    fun `si falla el guardado hay Error y se conservan los datos`() {
        repo.failSave = true
        fillValidForm()

        vm.savePokemon()

        assertEquals(SaveState.Error(AddPokemonScreenViewModel.SAVE_ERROR_MESSAGE), vm.saveState.value)
        assertEquals("Pikachu", vm.form.value.name)
        assertEquals("25", vm.form.value.level)
        assertEquals("Capturado en el Bosque Verde", vm.form.value.notes)
        assertTrue(repo.pokemonsOf("user-1").isEmpty())
    }

    @Test
    fun `tras un error se puede reintentar`() {
        repo.failSave = true
        fillValidForm()
        vm.savePokemon()

        repo.failSave = false
        vm.savePokemon()

        assertEquals(SaveState.Success, vm.saveState.value)
        assertEquals(1, repo.pokemonsOf("user-1").size)
    }

    @Test
    fun `pulsar dos veces seguidas no crea dos pokemon`() {
        repo.saveGate = CompletableDeferred()
        fillValidForm()

        vm.savePokemon()
        vm.savePokemon()
        repo.saveGate!!.complete(Unit)
        vm.savePokemon()

        assertEquals(1, repo.saveCalls)
        assertEquals(1, repo.pokemonsOf("user-1").size)
    }

    @Test
    fun `autocompletar desde la PokeAPI rellena numero, tipos y stats`() {
        vm.setName("Pikachu")

        vm.fetchPokemonFromApi()

        assertEquals("25", vm.form.value.pokedexNumber)
        assertEquals("Eléctrico", vm.form.value.primaryType)
        assertEquals("35", vm.hp.value)
        assertEquals("55", vm.attack.value)

        vm.setLevel("10")
        vm.savePokemon()
        val saved = repo.pokemonsOf("user-1").single()
        assertEquals(25, saved.pokedexNumber)
        assertEquals(90, saved.stats.speed)
    }

    private val charizard = Pokemon(
        id = "doc-7", pokedexNumber = 6, name = "Charizard", types = "Fuego / Volador", level = 36,
        description = "Mi compañero", imageUrl = "https://img/6.png", moves = listOf("Lanzallamas"),
        stats = PokemonStats(hp = 78, attack = 84, defense = 78, specialAttack = 109, specialDefense = 85, speed = 100)
    )

    @Test
    fun `en modo edicion precarga el formulario con los datos actuales`() {
        repo.seed("user-1", charizard)

        val edit = createViewModel("doc-7")

        assertTrue(edit.isEditing)
        assertEquals(UiState.Success(Unit), edit.loadState.value)
        assertEquals(PokemonForm("Charizard", "6", "Fuego", "Volador", "36", "Mi compañero"), edit.form.value)
        assertEquals("78", edit.hp.value)
        assertEquals("84", edit.attack.value)
    }

    @Test
    fun `guardar en modo edicion actualiza el mismo documento y conserva lo que no se edita`() {
        repo.seed("user-1", charizard)
        val edit = createViewModel("doc-7")

        edit.setLevel("40")
        edit.setNotes("Ya en nivel 40")
        edit.savePokemon()

        assertEquals(SaveState.Success, edit.saveState.value)
        assertEquals(0, repo.saveCalls)
        val saved = repo.pokemonsOf("user-1").single()
        assertEquals("doc-7", saved.id)
        assertEquals(40, saved.level)
        assertEquals("Ya en nivel 40", saved.description)
        assertEquals(109, saved.stats.specialAttack)
        assertEquals(listOf("Lanzallamas"), saved.moves)
    }

    @Test
    fun `en modo edicion las validaciones siguen aplicando`() {
        repo.seed("user-1", charizard)
        val edit = createViewModel("doc-7")

        edit.setLevel("0")
        edit.savePokemon()

        assertTrue(PokemonField.LEVEL in edit.errors.value)
        assertEquals(0, repo.updateCalls)
    }

    @Test
    fun `si el Pokemon a editar ya no existe muestra error`() {
        val edit = createViewModel("no-existe")

        assertTrue(edit.loadState.value is UiState.Error)
    }

    @Test
    fun `si falla la actualizacion conserva los cambios y muestra error`() {
        repo.seed("user-1", charizard)
        repo.failUpdate = true
        val edit = createViewModel("doc-7")

        edit.setLevel("41")
        edit.savePokemon()

        assertEquals(SaveState.Error(AddPokemonScreenViewModel.SAVE_ERROR_MESSAGE), edit.saveState.value)
        assertEquals("41", edit.form.value.level)
        assertEquals(36, repo.pokemonsOf("user-1").single().level)
    }

    @Test
    fun `buscar un nombre que no existe dice que no se encontro`() {
        vm.setName("Pikachuu")
        vm.fetchPokemonFromApi()

        assertEquals(PokeApiErrors.NOT_FOUND, vm.apiError.value)
        assertEquals(false, vm.isLoadingFromApi.value)
    }

    @Test
    fun `sin conexion no dice que el nombre este mal`() {
        api.failure = UnknownHostException("pokeapi.co")
        vm.setName("Pikachu")
        vm.fetchPokemonFromApi()

        assertEquals(PokeApiErrors.NO_CONNECTION, vm.apiError.value)
    }

    @Test
    fun `si la PokeAPI falla avisa de que no responde`() {
        api.failure = FakePokemonApiService.httpError(500)
        vm.setName("Pikachu")
        vm.fetchPokemonFromApi()

        assertEquals(PokeApiErrors.SERVER, vm.apiError.value)
    }

    @Test
    fun `busca con el nombre normalizado`() {
        vm.setName("  PIKACHU ")
        vm.fetchPokemonFromApi()

        assertEquals(listOf("pikachu"), api.queries)
        assertEquals("25", vm.form.value.pokedexNumber)
    }
}
