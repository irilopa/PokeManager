package org.ivan.pokmanager.fakes

import org.ivan.pokmanager.domain.repository.AuthError
import org.ivan.pokmanager.domain.repository.AuthRepository

/** AuthRepository en memoria. */
class FakeAuthRepository(
    var uid: String? = "user-1",
    var password: String = "secreto"
) : AuthRepository {
    var logoutCalls = 0
        private set
    var accountDeleted = false
        private set
    var failDeleteAccount = false
    /** Orden de las operaciones de borrado, compartido con los demas repositorios falsos. */
    var log: MutableList<String> = mutableListOf()

    override suspend fun register(email: String, password: String): Result<String> =
        Result.success("new-user").also { uid = "new-user" }

    override suspend fun login(email: String, password: String): Result<String> =
        Result.success("user-1").also { uid = "user-1" }

    override fun logout() {
        logoutCalls++
        uid = null
    }

    override fun currentUserId(): String? = uid

    override suspend fun reauthenticate(password: String): Result<Unit> {
        log += "reauthenticate"
        return if (password == this.password) Result.success(Unit)
        else Result.failure(AuthError("La contraseña no es correcta."))
    }

    val resetEmails = mutableListOf<String>()
    var failPasswordReset = false

    override suspend fun sendPasswordReset(email: String): Result<Unit> {
        if (failPasswordReset) return Result.failure(AuthError("Sin conexión. Comprueba tu red e inténtalo de nuevo."))
        resetEmails += email
        return Result.success(Unit)
    }

    override suspend fun deleteAccount(): Result<Unit> {
        log += "deleteAccount"
        if (failDeleteAccount) return Result.failure(IllegalStateException("auth down"))
        accountDeleted = true
        uid = null
        return Result.success(Unit)
    }
}
