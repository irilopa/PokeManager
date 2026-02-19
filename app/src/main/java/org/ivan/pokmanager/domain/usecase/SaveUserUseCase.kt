package org.ivan.pokmanager.domain.usecase


import org.ivan.pokmanager.domain.model.User
import org.ivan.pokmanager.domain.repository.UserFirestoreRepository

class SaveUserUseCase(private val userFirestoreRepository: UserFirestoreRepository) {
    suspend operator fun invoke(user: User): Boolean {
        return userFirestoreRepository.save(user)
    }
}