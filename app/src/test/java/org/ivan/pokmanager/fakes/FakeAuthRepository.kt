package org.ivan.pokmanager.fakes

import org.ivan.pokmanager.domain.repository.AuthRepository

/** AuthRepository en memoria. */
class FakeAuthRepository(var uid: String? = "user-1") : AuthRepository {
    var logoutCalls = 0
        private set

    override suspend fun register(email: String, password: String): Result<String> =
        Result.success("new-user").also { uid = "new-user" }

    override suspend fun login(email: String, password: String): Result<String> =
        Result.success("user-1").also { uid = "user-1" }

    override fun logout() {
        logoutCalls++
        uid = null
    }

    override fun currentUserId(): String? = uid
}
