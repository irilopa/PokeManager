package org.ivan.pokmanager.domain.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import org.ivan.pokmanager.domain.model.Pokemon

class PokemonFirestoreRepository(
    private val firestore: FirebaseFirestore
) {

    private fun userPokemonCollection(uid: String) =
        firestore.collection("users")
            .document(uid)
            .collection("pokemon")

    suspend fun getById(uid: String, id: String): Pokemon? {
        return try {
            val snapshot = userPokemonCollection(uid)
                .document(id)
                .get()
                .await()

            snapshot.toObject(Pokemon::class.java)

        } catch (e: Exception) {
            null
        }
    }

    fun list(uid: String): Flow<List<Pokemon>> {
        return queryForList(
            userPokemonCollection(uid),
            Pokemon::class.java
        )
    }

    suspend fun save(uid: String, pokemon: Pokemon): Boolean {
        return try {
            userPokemonCollection(uid)
                .add(pokemon)
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun delete(uid: String, id: String): Boolean {
        return try {
            userPokemonCollection(uid)
                .document(id)
                .delete()
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun <T> queryForList(query: Query, clazz: Class<T>): Flow<List<T>> {
        return callbackFlow {

            val listener = query.addSnapshotListener { snapshots, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val items = snapshots?.documents?.mapNotNull {
                    it.toObject(clazz)
                } ?: emptyList()

                trySend(items)
            }

            awaitClose { listener.remove() }
        }
    }
}

