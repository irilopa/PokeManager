package org.ivan.pokmanager.domain.usecase

import org.ivan.pokmanager.domain.repository.AuthRepository
import org.ivan.pokmanager.domain.util.Result

/**
 * Use case for user logout
 */
class LogoutUseCase(private val authRepository: AuthRepository) {
    /**
     * Execute logout
     * @return Result indicating success or error
     */
    suspend operator fun invoke(): Result<Unit> {
        return authRepository.logout()
    }
}
