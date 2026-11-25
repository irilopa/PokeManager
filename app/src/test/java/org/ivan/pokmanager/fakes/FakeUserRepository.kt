package org.ivan.pokmanager.fakes

import org.ivan.pokmanager.data.model.UserDto
import org.ivan.pokmanager.domain.repository.UserRepository

/** UserRepository en memoria. */
class FakeUserRepository : UserRepository {
    val profiles = mutableMapOf<String, UserDto>()
    var failDelete = false
    var log: MutableList<String> = mutableListOf()

    override suspend fun saveUserData(uid: String, user: UserDto) {
        profiles[uid] = user
    }

    override suspend fun deleteUserData(uid: String): Result<Unit> {
        log += "deleteProfile"
        if (failDelete) return Result.failure(IllegalStateException("network down"))
        profiles.remove(uid)
        return Result.success(Unit)
    }
}
