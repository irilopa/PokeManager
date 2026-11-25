package org.ivan.pokmanager.viewmodel

import org.ivan.pokmanager.MainDispatcherRule
import org.ivan.pokmanager.domain.model.Pokemon
import org.ivan.pokmanager.domain.usecase.DeleteAccountUseCase
import org.ivan.pokmanager.fakes.FakeAuthRepository
import org.ivan.pokmanager.fakes.randomCredential
import org.ivan.pokmanager.fakes.FakePokemonRepository
import org.ivan.pokmanager.fakes.FakeUserRepository
import org.ivan.pokmanager.presentation.state.DeleteAccountState
import org.ivan.pokmanager.presentation.viewmodel.DeleteAccountViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class DeleteAccountViewModelTest {

    @get:Rule
    val mainRule = MainDispatcherRule()

    private val auth = FakeAuthRepository()
    private val repo = FakePokemonRepository().also { it.seed("user-1", Pokemon(id = "a", name = "Pikachu")) }
    private val vm = DeleteAccountViewModel(DeleteAccountUseCase(auth, repo, FakeUserRepository()))

    @Test
    fun `con la contrasena correcta termina en Deleted`() {
        vm.setPassword(auth.password)
        vm.deleteAccount()

        assertEquals(DeleteAccountState.Deleted, vm.state.value)
        assertTrue(auth.accountDeleted)
        assertTrue(repo.pokemonsOf("user-1").isEmpty())
    }

    @Test
    fun `con contrasena incorrecta muestra el error y no borra`() {
        vm.setPassword(randomCredential())
        vm.deleteAccount()

        assertEquals(DeleteAccountState.Error("La contraseña no es correcta."), vm.state.value)
        assertEquals(1, repo.pokemonsOf("user-1").size)

        vm.setPassword(auth.password)
        assertEquals("escribir de nuevo limpia el error", DeleteAccountState.Idle, vm.state.value)
    }

    @Test
    fun `sin contrasena no llama al repositorio`() {
        vm.deleteAccount()

        assertTrue(vm.state.value is DeleteAccountState.Error)
        assertTrue(auth.log.isEmpty())
    }

    @Test
    fun `tras borrar ignora nuevas pulsaciones`() {
        vm.setPassword(auth.password)
        vm.deleteAccount()
        auth.log.clear()

        vm.deleteAccount()

        assertTrue(auth.log.isEmpty())
    }
}
