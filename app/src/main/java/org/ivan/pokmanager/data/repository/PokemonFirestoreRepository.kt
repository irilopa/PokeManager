package org.ivan.pokmanager.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import org.ivan.pokmanager.domain.model.Pokemon
import org.ivan.pokmanager.domain.repository.PokemonRepository

/**
 * Implementacion de [PokemonRepository] sobre Firestore (`users/{uid}/pokemon`).
 */
class PokemonFirestoreRepository(
    private val firestore: FirebaseFirestore
) : PokemonRepository {

    private fun userPokemonCollection(uid: String) =
        firestore.collection("users")
            .document(uid)
            .collection("pokemon")

    override fun list(uid: String): Flow<List<Pokemon>> = callbackFlow {
        val listener = userPokemonCollection(uid).addSnapshotListener { snapshots, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val items = snapshots?.documents?.mapNotNull { it.toObject(Pokemon::class.java) }
                ?: emptyList()
            trySend(items)
        }
        awaitClose { listener.remove() }
    }

    override suspend fun getById(uid: String, id: String): Result<Pokemon?> = runCatching {
        userPokemonCollection(uid)
            .document(id)
            .get()
            .await()
            .toObject(Pokemon::class.java)
    }

    override suspend fun save(uid: String, pokemon: Pokemon): Result<Unit> = runCatching {
        userPokemonCollection(uid).add(pokemon).await()
        Unit
    }

    override suspend fun delete(uid: String, id: String): Result<Unit> = runCatching {
        userPokemonCollection(uid).document(id).delete().await()
        Unit
    }

    override suspend fun deleteAll(uid: String): Result<Unit> = runCatching {
        // Firestore no borra subcolecciones en cascada: se borran los documentos por lotes.
        val documents = userPokemonCollection(uid).get().await().documents
        documents.chunked(MAX_BATCH_SIZE).forEach { chunk ->
            val batch = firestore.batch()
            chunk.forEach { batch.delete(it.reference) }
            batch.commit().await()
        }
    }

    private companion object {
        const val MAX_BATCH_SIZE = 500
    }
}
