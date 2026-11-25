package org.ivan.pokmanager.usecase

import kotlinx.coroutines.test.runTest
import org.ivan.pokmanager.data.model.UserDto
import org.ivan.pokmanager.domain.model.Pokemon
import org.ivan.pokmanager.domain.usecase.DeleteAccountUseCase
import org.ivan.pokmanager.fakes.FakeAuthRepository
import org.ivan.pokmanager.fakes.FakePokemonRepository
import org.ivan.pokmanager.fakes.FakeUserRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DeleteAccountUseCaseTest {

    private val log = mutableListOf<String>()
    private val auth = FakeAuthRepository().also { it.log = log }
    private val pokemons = FakePokemonRepository().also { it.log = log }
    private val users = FakeUserRepository().also { it.log = log }
    private val deleteAccount = DeleteAccountUseCase(auth, pokemons, users)

    @Before
    fun seed() {
        pokemons.seed("user-1", Pokemon(id = "a", name = "Pikachu"), Pokemon(id = "b", name = "Eevee"))
        users.profiles["user-1"] = UserDto(name = "Ash", email = "ash@test.com")
    }

    @Test
    fun `borra los Pokemon, el perfil y la cuenta en ese orden`() = runTest {
        val result = deleteAccount("secreto")

        assertTrue(result.isSuccess)
        assertEquals(listOf("reauthenticate", "deleteAllPokemon", "deleteProfile", "deleteAccount"), log)
        assertTrue(pokemons.pokemonsOf("user-1").isEmpty())
        assertTrue("user-1" !in users.profiles)
        assertTrue(auth.accountDeleted)
    }

    @Test
    fun `con contrasena incorrecta no borra nada`() = runTest {
        val result = deleteAccount("otra")

        assertEquals("La contraseña no es correcta.", result.exceptionOrNull()?.message)
        assertEquals(listOf("reauthenticate"), log)
        assertEquals(2, pokemons.pokemonsOf("user-1").size)
        assertTrue("user-1" in users.profiles)
        assertFalse(auth.accountDeleted)
    }

    @Test
    fun `si falla el borrado de datos la cuenta sigue activa`() = runTest {
        pokemons.failDeleteAll = true

        val result = deleteAccount("secreto")

        assertEquals(DeleteAccountUseCase.DATA_ERROR_MESSAGE, result.exceptionOrNull()?.message)
        assertFalse(auth.accountDeleted)
        assertTrue("deleteAccount" !in log)
    }

    @Test
    fun `si falla el borrado de la cuenta avisa de que los datos ya se borraron`() = runTest {
        auth.failDeleteAccount = true

        val result = deleteAccount("secreto")

        assertEquals(DeleteAccountUseCase.ACCOUNT_ERROR_MESSAGE, result.exceptionOrNull()?.message)
        assertTrue(pokemons.pokemonsOf("user-1").isEmpty())
    }

    @Test
    fun `sin sesion devuelve error sin tocar nada`() = runTest {
        auth.uid = null

        val result = deleteAccount("secreto")

        assertTrue(result.isFailure)
        assertTrue(log.isEmpty())
    }
}
