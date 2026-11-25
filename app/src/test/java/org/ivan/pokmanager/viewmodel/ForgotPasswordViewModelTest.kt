package org.ivan.pokmanager.viewmodel

import org.ivan.pokmanager.MainDispatcherRule
import org.ivan.pokmanager.fakes.FakeAuthRepository
import org.ivan.pokmanager.presentation.state.PasswordResetState
import org.ivan.pokmanager.presentation.viewmodel.ForgotPasswordViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ForgotPasswordViewModelTest {

    @get:Rule
    val mainRule = MainDispatcherRule()

    private val auth = FakeAuthRepository()
    private val vm = ForgotPasswordViewModel(auth, isValidEmail = { "@" in it && "." in it })

    @Test
    fun `envia el correo al email escrito, sin espacios`() {
        vm.setEmail("  ash@test.com ")
        vm.sendReset()

        assertEquals(PasswordResetState.Sent, vm.state.value)
        assertEquals(listOf("ash@test.com"), auth.resetEmails)
    }

    @Test
    fun `email con formato invalido no llama a Firebase`() {
        vm.setEmail("ash")
        vm.sendReset()

        assertEquals(PasswordResetState.Error("Introduce un email válido."), vm.state.value)
        assertTrue(auth.resetEmails.isEmpty())
    }

    @Test
    fun `si falla la red muestra el error y escribir lo limpia`() {
        auth.failPasswordReset = true
        vm.setEmail("ash@test.com")
        vm.sendReset()

        assertTrue(vm.state.value is PasswordResetState.Error)

        vm.setEmail("ash@test.es")
        assertEquals(PasswordResetState.Idle, vm.state.value)
    }
}
