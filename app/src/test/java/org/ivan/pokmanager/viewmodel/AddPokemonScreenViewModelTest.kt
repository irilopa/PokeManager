package org.ivan.pokmanager.viewmodel

import kotlinx.coroutines.CompletableDeferred
import org.ivan.pokmanager.MainDispatcherRule
import org.ivan.pokmanager.data.model.remote.dto.PokemonResponse
import org.ivan.pokmanager.data.model.remote.dto.StatNameDTO
import org.ivan.pokmanager.data.model.remote.dto.StatResponseDTO
import org.ivan.pokmanager.data.model.remote.dto.TypeNameDTO
import org.ivan.pokmanager.data.model.remote.dto.TypeSlotDTO
import org.ivan.pokmanager.domain.usecase.SavePokemonUseCase
import org.ivan.pokmanager.domain.validation.PokemonField
import org.ivan.pokmanager.fakes.FakeAuthRepository
import org.ivan.pokmanager.fakes.FakePokemonApiService
import org.ivan.pokmanager.fakes.FakePokemonRepository
import org.ivan.pokmanager.presentation.state.SaveState
import org.ivan.pokmanager.presentation.viewmodel.AddPokemonScreenViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

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

    private val vm = AddPokemonScreenViewModel(SavePokemonUseCase(repo), api, auth)

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
}
