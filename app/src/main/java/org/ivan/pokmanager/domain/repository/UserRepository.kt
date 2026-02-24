package org.ivan.pokmanager.domain.repository

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import org.ivan.pokmanager.data.model.UserDto

class UserRepository(val firestore: FirebaseFirestore) {
    private val usersCollection = firestore.collection("users")

    suspend fun saveUserData(uid: String, user: UserDto) {
        usersCollection
            .document(uid)
            .set(user)
            .await()
    }

    suspend fun getUserData(uid: String): UserDto? {
        val snapshot = firestore.collection("users")
            .document(uid)
            .get()
            .await()
        return snapshot.toObject(UserDto::class.java)
    }
}