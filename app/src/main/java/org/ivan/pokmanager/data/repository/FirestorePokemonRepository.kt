package org.ivan.pokmanager.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import org.ivan.pokmanager.data.model.Pokemon
import org.ivan.pokmanager.data.model.PokemonStats
import org.ivan.pokmanager.domain.repository.PokemonRepository
import org.ivan.pokmanager.domain.util.Result

/**
 * Firestore implementation of PokemonRepository
 */
class FirestorePokemonRepository : PokemonRepository {
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    
    private companion object {
        const val COLLECTION_POKEMONS = "pokemons"
        const val FIELD_USER_ID = "userId"
        const val FIELD_POKEDEX_NUMBER = "pokedexNumber"
        const val FIELD_NAME = "name"
        const val FIELD_TYPES = "types"
        const val FIELD_DESCRIPTION = "description"
        const val FIELD_HEIGHT_M = "heightM"
        const val FIELD_WEIGHT_KG = "weightKg"
        const val FIELD_IMAGE_URL = "imageUrl"
        const val FIELD_MOVES = "moves"
        const val FIELD_STATS = "stats"
    }

    override fun getPokemons(): Flow<Result<List<Pokemon>>> = callbackFlow {
        val userId = auth.currentUser?.uid
        
        if (userId == null) {
            trySend(Result.Error(Exception("Usuario no autenticado")))
            close()
            return@callbackFlow
        }

        trySend(Result.Loading)

        val subscription = firestore.collection(COLLECTION_POKEMONS)
            .whereEqualTo(FIELD_USER_ID, userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.Error(error))
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    try {
                        val pokemons = snapshot.documents.mapNotNull { doc ->
                            try {
                                val statsMap = doc.get(FIELD_STATS) as? Map<*, *>
                                val stats = if (statsMap != null) {
                                    PokemonStats(
                                        hp = (statsMap["hp"] as? Long)?.toInt() ?: 0,
                                        attack = (statsMap["attack"] as? Long)?.toInt() ?: 0,
                                        defense = (statsMap["defense"] as? Long)?.toInt() ?: 0,
                                        specialAttack = (statsMap["specialAttack"] as? Long)?.toInt() ?: 0,
                                        specialDefense = (statsMap["specialDefense"] as? Long)?.toInt() ?: 0,
                                        speed = (statsMap["speed"] as? Long)?.toInt() ?: 0
                                    )
                                } else {
                                    PokemonStats(0, 0, 0, 0, 0, 0)
                                }

                                Pokemon(
                                    pokedexNumber = (doc.get(FIELD_POKEDEX_NUMBER) as? Long)?.toInt() ?: 0,
                                    name = doc.getString(FIELD_NAME) ?: "",
                                    types = doc.getString(FIELD_TYPES) ?: "",
                                    description = doc.getString(FIELD_DESCRIPTION) ?: "",
                                    heightM = doc.getDouble(FIELD_HEIGHT_M) ?: 0.0,
                                    weightKg = doc.getDouble(FIELD_WEIGHT_KG) ?: 0.0,
                                    stats = stats,
                                    imageUrl = doc.getString(FIELD_IMAGE_URL) ?: "",
                                    moves = (doc.get(FIELD_MOVES) as? List<*>)?.mapNotNull { it as? String } ?: emptyList()
                                )
                            } catch (e: Exception) {
                                null
                            }
                        }
                        trySend(Result.Success(pokemons))
                    } catch (e: Exception) {
                        trySend(Result.Error(e))
                    }
                }
            }

        awaitClose { subscription.remove() }
    }

    override suspend fun addPokemon(pokemon: Pokemon): Result<Unit> {
        return try {
            val userId = auth.currentUser?.uid
                ?: return Result.Error(Exception("Usuario no autenticado"))

            val pokemonData = hashMapOf(
                FIELD_USER_ID to userId,
                FIELD_POKEDEX_NUMBER to pokemon.pokedexNumber,
                FIELD_NAME to pokemon.name,
                FIELD_TYPES to pokemon.types,
                FIELD_DESCRIPTION to pokemon.description,
                FIELD_HEIGHT_M to pokemon.heightM,
                FIELD_WEIGHT_KG to pokemon.weightKg,
                FIELD_IMAGE_URL to pokemon.imageUrl,
                FIELD_MOVES to pokemon.moves,
                FIELD_STATS to hashMapOf(
                    "hp" to pokemon.stats.hp,
                    "attack" to pokemon.stats.attack,
                    "defense" to pokemon.stats.defense,
                    "specialAttack" to pokemon.stats.specialAttack,
                    "specialDefense" to pokemon.stats.specialDefense,
                    "speed" to pokemon.stats.speed
                )
            )

            firestore.collection(COLLECTION_POKEMONS)
                .document("${userId}_${pokemon.pokedexNumber}")
                .set(pokemonData)
                .await()

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun updatePokemon(pokemon: Pokemon): Result<Unit> {
        return try {
            val userId = auth.currentUser?.uid
                ?: return Result.Error(Exception("Usuario no autenticado"))

            val pokemonData = hashMapOf(
                FIELD_USER_ID to userId,
                FIELD_POKEDEX_NUMBER to pokemon.pokedexNumber,
                FIELD_NAME to pokemon.name,
                FIELD_TYPES to pokemon.types,
                FIELD_DESCRIPTION to pokemon.description,
                FIELD_HEIGHT_M to pokemon.heightM,
                FIELD_WEIGHT_KG to pokemon.weightKg,
                FIELD_IMAGE_URL to pokemon.imageUrl,
                FIELD_MOVES to pokemon.moves,
                FIELD_STATS to hashMapOf(
                    "hp" to pokemon.stats.hp,
                    "attack" to pokemon.stats.attack,
                    "defense" to pokemon.stats.defense,
                    "specialAttack" to pokemon.stats.specialAttack,
                    "specialDefense" to pokemon.stats.specialDefense,
                    "speed" to pokemon.stats.speed
                )
            )

            firestore.collection(COLLECTION_POKEMONS)
                .document("${userId}_${pokemon.pokedexNumber}")
                .set(pokemonData)
                .await()

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun deletePokemon(pokedexNumber: Int): Result<Unit> {
        return try {
            val userId = auth.currentUser?.uid
                ?: return Result.Error(Exception("Usuario no autenticado"))

            firestore.collection(COLLECTION_POKEMONS)
                .document("${userId}_${pokedexNumber}")
                .delete()
                .await()

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getPokemon(pokedexNumber: Int): Result<Pokemon> {
        return try {
            val userId = auth.currentUser?.uid
                ?: return Result.Error(Exception("Usuario no autenticado"))

            val doc = firestore.collection(COLLECTION_POKEMONS)
                .document("${userId}_${pokedexNumber}")
                .get()
                .await()

            if (doc.exists()) {
                val statsMap = doc.get(FIELD_STATS) as? Map<*, *>
                val stats = if (statsMap != null) {
                    PokemonStats(
                        hp = (statsMap["hp"] as? Long)?.toInt() ?: 0,
                        attack = (statsMap["attack"] as? Long)?.toInt() ?: 0,
                        defense = (statsMap["defense"] as? Long)?.toInt() ?: 0,
                        specialAttack = (statsMap["specialAttack"] as? Long)?.toInt() ?: 0,
                        specialDefense = (statsMap["specialDefense"] as? Long)?.toInt() ?: 0,
                        speed = (statsMap["speed"] as? Long)?.toInt() ?: 0
                    )
                } else {
                    PokemonStats(0, 0, 0, 0, 0, 0)
                }

                val pokemon = Pokemon(
                    pokedexNumber = (doc.get(FIELD_POKEDEX_NUMBER) as? Long)?.toInt() ?: 0,
                    name = doc.getString(FIELD_NAME) ?: "",
                    types = doc.getString(FIELD_TYPES) ?: "",
                    description = doc.getString(FIELD_DESCRIPTION) ?: "",
                    heightM = doc.getDouble(FIELD_HEIGHT_M) ?: 0.0,
                    weightKg = doc.getDouble(FIELD_WEIGHT_KG) ?: 0.0,
                    stats = stats,
                    imageUrl = doc.getString(FIELD_IMAGE_URL) ?: "",
                    moves = (doc.get(FIELD_MOVES) as? List<*>)?.mapNotNull { it as? String } ?: emptyList()
                )
                Result.Success(pokemon)
            } else {
                Result.Error(Exception("Pokémon no encontrado"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
