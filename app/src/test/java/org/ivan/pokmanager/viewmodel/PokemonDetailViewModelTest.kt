package org.ivan.pokmanager.viewmodel

import org.ivan.pokmanager.MainDispatcherRule
import org.ivan.pokmanager.domain.model.Pokemon
import org.ivan.pokmanager.domain.usecase.GetPokemonByIdUseCase
import org.ivan.pokmanager.fakes.FakeAuthRepository
import org.ivan.pokmanager.fakes.FakePokemonRepository
import org.ivan.pokmanager.presentation.state.UiState
import org.ivan.pokmanager.presentation.viewmodel.PokemonDetailViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class PokemonDetailViewModelTest {

    @get:Rule
    val mainRule = MainDispatcherRule()

    private val auth = FakeAuthRepository()
    private val repo = FakePokemonRepository()

    private val pikachuA = Pokemon(id = "doc-a", pokedexNumber = 25, name = "Pikachu", level = 5)
    private val pikachuB = Pokemon(id = "doc-b", pokedexNumber = 25, name = "Pikachu", level = 50)

    private fun createViewModel(id: String) =
        PokemonDetailViewModel(id, GetPokemonByIdUseCase(repo), auth)

    @Test
    fun `dos pokemon con el mismo numero de pokedex abren detalles distintos`() {
        repo.seed("user-1", pikachuA, pikachuB)

        assertEquals(UiState.Success(pikachuA), createViewModel("doc-a").uiState.value)
        assertEquals(UiState.Success(pikachuB), createViewModel("doc-b").uiState.value)
    }

    @Test
    fun `id inexistente devuelve Success null`() {
        assertEquals(UiState.Success(null), createViewModel("nope").uiState.value)
    }

    @Test
    fun `si falla la carga hay Error y reintentar funciona`() {
        repo.seed("user-1", pikachuA)
        repo.failGetById = true
        val vm = createViewModel("doc-a")

        assertTrue(vm.uiState.value is UiState.Error)

        repo.failGetById = false
        vm.retry()

        assertEquals(UiState.Success(pikachuA), vm.uiState.value)
    }
}
