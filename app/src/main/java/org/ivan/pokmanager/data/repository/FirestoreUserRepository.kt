package org.ivan.pokmanager.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import org.ivan.pokmanager.data.model.UserDto
import org.ivan.pokmanager.domain.repository.UserRepository

/**
 * Implementacion de [UserRepository] sobre Firestore (`users/{uid}`).
 */
class FirestoreUserRepository(private val firestore: FirebaseFirestore) : UserRepository {
    private val usersCollection = firestore.collection("users")

    override suspend fun saveUserData(uid: String, user: UserDto) {
        usersCollection.document(uid).set(user).await()
    }

    override suspend fun deleteUserData(uid: String): Result<Unit> = runCatching {
        usersCollection.document(uid).delete().await()
        Unit
    }
}
