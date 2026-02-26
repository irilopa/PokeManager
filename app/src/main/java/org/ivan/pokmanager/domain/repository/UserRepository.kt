package org.ivan.pokmanager.domain.repository

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import org.ivan.pokmanager.data.model.UserDto

/**
 * Repositorio para persistir y leer perfiles de usuario en Firestore.
 */
class UserRepository(val firestore: FirebaseFirestore) {
    private val usersCollection = firestore.collection("users")

    /** Guarda el perfil del usuario bajo su uid. */
    suspend fun saveUserData(uid: String, user: UserDto) {
        usersCollection
            .document(uid)
            .set(user)
            .await()
    }

    /** Obtiene el perfil del usuario por uid. */
    suspend fun getUserData(uid: String): UserDto? {
        val snapshot = firestore.collection("users")
            .document(uid)
            .get()
            .await()
        return snapshot.toObject(UserDto::class.java)
    }
}