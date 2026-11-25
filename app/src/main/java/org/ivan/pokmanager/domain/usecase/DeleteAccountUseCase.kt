package org.ivan.pokmanager.domain.usecase

import org.ivan.pokmanager.domain.repository.AuthError
import org.ivan.pokmanager.domain.repository.AuthRepository
import org.ivan.pokmanager.domain.repository.PokemonRepository
import org.ivan.pokmanager.domain.repository.UserRepository

/**
 * Borra la cuenta del usuario y todos sus datos.
 *
 * Orden: 1) comprobar la contrasena (si falla, no se borra nada); 2) borrar los Pokemon;
 * 3) borrar el perfil; 4) borrar la cuenta de Auth. Los datos se borran antes que la cuenta
 * porque, sin cuenta, las reglas de Firestore ya no permitirian borrarlos.
 */
class DeleteAccountUseCase(
    private val authRepository: AuthRepository,
    private val pokemonRepository: PokemonRepository,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(password: String): Result<Unit> {
        val uid = authRepository.currentUserId()
            ?: return Result.failure(AuthError(NO_SESSION_MESSAGE))

        authRepository.reauthenticate(password).onFailure { return Result.failure(it) }

        pokemonRepository.deleteAll(uid).onFailure {
            return Result.failure(AuthError(DATA_ERROR_MESSAGE, it))
        }
        userRepository.deleteUserData(uid).onFailure {
            return Result.failure(AuthError(DATA_ERROR_MESSAGE, it))
        }
        return authRepository.deleteAccount().fold(
            onSuccess = { Result.success(Unit) },
            onFailure = { Result.failure(AuthError(ACCOUNT_ERROR_MESSAGE, it)) }
        )
    }

    companion object {
        const val NO_SESSION_MESSAGE = "Tu sesión ha caducado. Vuelve a iniciar sesión."
        const val DATA_ERROR_MESSAGE =
            "No se pudieron borrar todos tus datos. Tu cuenta sigue activa; inténtalo de nuevo."
        const val ACCOUNT_ERROR_MESSAGE =
            "Tus datos se han borrado, pero la cuenta no. Inténtalo de nuevo para terminar."
    }
}
