package org.ivan.pokmanager.domain.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import org.ivan.pokmanager.domain.model.Pokemon

class PokemonFirestoreRepository(val firestore: FirebaseFirestore) {
    private val pokemonCollection = firestore.collection("pokemon")
    suspend fun getById(id: String): Pokemon? {
        return try {
            val documentSnapshot = pokemonCollection.document(id).get().await()
            documentSnapshot.toObject(Pokemon::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
        fun list(): Flow<List<Pokemon>> {
            // Esta implementación crea un Flow que actualiza la lista de usuarios
            // cada vez que hay un cambio en la base de datos
            return queryForList(
                pokemonCollection,
                Pokemon::class.java
            )
        }

        // Agregar un nuevo usuario
        suspend fun save(pokemon: Pokemon): Boolean {
            return try {
                pokemonCollection.add(pokemon).await()
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

        // Eliminar un usuario por ID
        suspend fun delete(id: String): Boolean {
            return try {
                pokemonCollection.document(id).delete().await()
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

    }

    // Este método es siempre igual para cualquier repository
    private fun <T> queryForList(query: Query, clazz: Class<T>): Flow<List<T>> {
        return callbackFlow {

            val listener = query
                .addSnapshotListener { snapshots, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }

                    val items = snapshots?.documents?.mapNotNull { doc ->
                        doc.toObject(clazz)

                    } ?: emptyList()

                    trySend(items)
                }

            awaitClose() { listener.remove() }
        }
    }

    // Este método es siempre igual para cualquier repository
    private fun <T> queryForSingle(query: Query, clazz: Class<T>): Flow<T?> {
        return callbackFlow {
            val listener = query
                .addSnapshotListener { snapshots, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }

                    val item = snapshots?.documents?.firstOrNull()?.toObject(clazz)

                    trySend(item)
                }
            awaitClose() { listener.remove() }
        }
    }

}