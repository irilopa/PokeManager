package org.ivan.pokmanager.domain.usecase

import com.google.firebase.auth.FirebaseUser
import org.ivan.pokmanager.domain.repository.AuthRepository
import org.ivan.pokmanager.domain.util.Result

/**
 * Use case for user login
 */
class LoginUseCase(private val authRepository: AuthRepository) {
    /**
     * Execute login with email and password
     * @param email User's email
     * @param password User's password
     * @return Result indicating success or error with FirebaseUser
     */
    suspend operator fun invoke(email: String, password: String): Result<FirebaseUser> {
        if (email.isBlank()) {
            return Result.Error(Exception("El email no puede estar vacío"))
        }
        if (password.isBlank()) {
            return Result.Error(Exception("La contraseña no puede estar vacía"))
        }
        if (!isValidEmail(email)) {
            return Result.Error(Exception("El email no es válido"))
        }
        if (password.length < 6) {
            return Result.Error(Exception("La contraseña debe tener al menos 6 caracteres"))
        }
        return authRepository.login(email, password)
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}
