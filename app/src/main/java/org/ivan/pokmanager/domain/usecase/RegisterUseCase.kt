package org.ivan.pokmanager.domain.usecase

import com.google.firebase.auth.FirebaseUser
import org.ivan.pokmanager.domain.repository.AuthRepository
import org.ivan.pokmanager.domain.util.Result

/**
 * Use case for user registration
 */
class RegisterUseCase(private val authRepository: AuthRepository) {
    /**
     * Execute registration with email and password
     * @param email User's email
     * @param password User's password
     * @param confirmPassword Password confirmation
     * @return Result indicating success or error with FirebaseUser
     */
    suspend operator fun invoke(
        email: String,
        password: String,
        confirmPassword: String
    ): Result<FirebaseUser> {
        if (email.isBlank()) {
            return Result.Error(Exception("El email no puede estar vacío"))
        }
        if (password.isBlank()) {
            return Result.Error(Exception("La contraseña no puede estar vacía"))
        }
        if (confirmPassword.isBlank()) {
            return Result.Error(Exception("Debe confirmar la contraseña"))
        }
        if (!isValidEmail(email)) {
            return Result.Error(Exception("El email no es válido"))
        }
        if (password.length < 6) {
            return Result.Error(Exception("La contraseña debe tener al menos 6 caracteres"))
        }
        if (password != confirmPassword) {
            return Result.Error(Exception("Las contraseñas no coinciden"))
        }
        return authRepository.register(email, password)
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}
