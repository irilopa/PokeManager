package org.ivan.pokmanager.data.repository

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import org.ivan.pokmanager.domain.repository.AuthRepository

/**
 * Implementacion de [AuthRepository] con Firebase Auth.
 */
class FirebaseAuthRepository(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    override suspend fun register(email: String, password: String): Result<String> = runCatching {
        val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        requireNotNull(result.user) { "Firebase no devolvió el usuario" }.uid
    }

    override suspend fun login(email: String, password: String): Result<String> = runCatching {
        val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
        requireNotNull(result.user) { "Firebase no devolvió el usuario" }.uid
    }

    override fun logout() {
        firebaseAuth.signOut()
    }

    override fun currentUserId(): String? = firebaseAuth.currentUser?.uid
}
