package org.ivan.pokmanager.domain.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

/**
 * Acceso a operaciones de autenticacion con Firebase Auth.
 */
class AuthRepository(
    private val firebaseAuth: FirebaseAuth
) {

    /** Crea una cuenta con email y contrasena. */
    suspend fun register(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = firebaseAuth
                .createUserWithEmailAndPassword(email, password)
                .await()

            Result.success(result.user!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /** Inicia sesion con email y contrasena. */
    suspend fun login(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = firebaseAuth
                .signInWithEmailAndPassword(email, password)
                .await()

            Result.success(result.user!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /** Cierra la sesion actual. */
    fun logout() {
        firebaseAuth.signOut()
    }

    /** Retorna el usuario autenticado actual, si existe. */
    fun currentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }
}