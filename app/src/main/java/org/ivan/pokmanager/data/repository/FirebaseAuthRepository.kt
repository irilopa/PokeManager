package org.ivan.pokmanager.data.repository

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await
import org.ivan.pokmanager.domain.repository.AuthError
import org.ivan.pokmanager.domain.repository.AuthRepository

/**
 * Implementacion de [AuthRepository] con Firebase Auth.
 */
class FirebaseAuthRepository(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    override suspend fun register(email: String, password: String): Result<String> = authCall {
        firebaseAuth.createUserWithEmailAndPassword(email, password).await().user
    }

    override suspend fun login(email: String, password: String): Result<String> = authCall {
        firebaseAuth.signInWithEmailAndPassword(email, password).await().user
    }

    private inline fun authCall(block: () -> FirebaseUser?): Result<String> = try {
        val user = block() ?: throw AuthError(GENERIC_ERROR)
        Result.success(user.uid)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.failure(e as? AuthError ?: AuthError(e.toUserMessage(), e))
    }

    override fun logout() {
        firebaseAuth.signOut()
    }

    override fun currentUserId(): String? = firebaseAuth.currentUser?.uid

    companion object {
        const val GENERIC_ERROR = "No se pudo completar la operación. Inténtalo de nuevo."

        /** Traduce los errores de Firebase Auth a mensajes en español. */
        fun Exception.toUserMessage(): String = when (this) {
            is FirebaseAuthWeakPasswordException -> "La contraseña es demasiado débil"
            is FirebaseAuthUserCollisionException -> "Este email ya está registrado"
            is FirebaseAuthInvalidUserException -> "Email o contraseña incorrectos"
            is FirebaseAuthInvalidCredentialsException ->
                if (errorCode == "ERROR_INVALID_EMAIL") "El email no tiene un formato válido"
                else "Email o contraseña incorrectos"
            is FirebaseTooManyRequestsException -> "Demasiados intentos. Espera un momento y vuelve a probar."
            is FirebaseNetworkException -> "Sin conexión. Comprueba tu red e inténtalo de nuevo."
            else -> GENERIC_ERROR
        }
    }
}
