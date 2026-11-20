package org.ivan.pokmanager.viewmodel

import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.ivan.pokmanager.MainDispatcherRule
import org.ivan.pokmanager.domain.model.Pokemon
import org.ivan.pokmanager.domain.usecase.DeletePokemonUseCase
import org.ivan.pokmanager.domain.usecase.GetPokemonUseCase
import org.ivan.pokmanager.fakes.FakeAuthRepository
import org.ivan.pokmanager.fakes.FakePokemonRepository
import org.ivan.pokmanager.presentation.state.UiState
import org.ivan.pokmanager.presentation.viewmodel.PokemonListViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class PokemonListViewModelTest {

    @get:Rule
    val mainRule = MainDispatcherRule()

    private val auth = FakeAuthRepository()
    private val repo = FakePokemonRepository()

    private fun createViewModel() = PokemonListViewModel(
        GetPokemonUseCase(repo),
        DeletePokemonUseCase(repo),
        auth
    )

    @Test
    fun `mientras carga el estado es Loading`() = runTest {
        repo.holdLoading()
        val vm = createViewModel()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.collect {} }

        assertEquals(UiState.Loading, vm.uiState.value)
    }

    @Test
    fun `con datos el estado es Success con la lista`() = runTest {
        val pikachu = Pokemon(id = "a", name = "Pikachu")
        repo.seed("user-1", pikachu)
        val vm = createViewModel()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.collect {} }

        assertEquals(UiState.Success(listOf(pikachu)), vm.uiState.value)
    }

    @Test
    fun `sin datos el estado es Success vacio y no Loading`() = runTest {
        val vm = createViewModel()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.collect {} }

        assertEquals(UiState.Success(emptyList<Pokemon>()), vm.uiState.value)
    }

    @Test
    fun `si falla la carga hay Error y reintentar recupera la lista`() = runTest {
        repo.failList = true
        val vm = createViewModel()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.collect {} }

        assertTrue(vm.uiState.value is UiState.Error)

        repo.failList = false
        repo.seed("user-1", Pokemon(id = "a", name = "Pikachu"))
        vm.retry()

        assertEquals(UiState.Success(listOf(Pokemon(id = "a", name = "Pikachu"))), vm.uiState.value)
    }

    @Test
    fun `sin sesion el estado es Error`() = runTest {
        auth.uid = null
        val vm = createViewModel()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.collect {} }

        assertEquals(UiState.Error(PokemonListViewModel.NO_SESSION_MESSAGE), vm.uiState.value)
    }

    @Test
    fun `logout cierra la sesion`() {
        val vm = createViewModel()

        vm.logout()

        assertEquals(1, auth.logoutCalls)
        assertNull(auth.currentUserId())
    }

    @Test
    fun `eliminar quita el pokemon de la lista`() = runTest {
        repo.seed("user-1", Pokemon(id = "a", name = "Pikachu"), Pokemon(id = "b", name = "Eevee"))
        val vm = createViewModel()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.collect {} }

        vm.removePokemon(Pokemon(id = "a", name = "Pikachu"))

        assertEquals(UiState.Success(listOf(Pokemon(id = "b", name = "Eevee"))), vm.uiState.value)
    }
}
