package org.ivan.pokmanager.fakes

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import org.ivan.pokmanager.domain.model.Pokemon
import org.ivan.pokmanager.domain.repository.PokemonRepository

/**
 * PokemonRepository en memoria con interruptores para simular fallos y esperas.
 */
class FakePokemonRepository : PokemonRepository {

    /** null = todavia no ha llegado la primera respuesta (la lista sigue cargando). */
    private val data = MutableStateFlow<Map<String, List<Pokemon>>?>(emptyMap())
    private var nextId = 1

    var failList = false
    var failGetById = false
    var failSave = false
    /** Si no es null, save() espera a que se complete antes de responder. */
    var saveGate: CompletableDeferred<Unit>? = null
    var saveCalls = 0
        private set
    var failDeleteAll = false
    var log: MutableList<String> = mutableListOf()

    fun pokemonsOf(uid: String): List<Pokemon> = data.value?.get(uid).orEmpty()

    /** Simula que Firestore aun no ha respondido. */
    fun holdLoading() {
        data.value = null
    }

    fun seed(uid: String, vararg pokemon: Pokemon) {
        val current = data.value.orEmpty()
        data.value = current + (uid to current[uid].orEmpty() + pokemon)
    }

    override fun list(uid: String): Flow<List<Pokemon>> =
        if (failList) flow { throw IllegalStateException("network down") }
        else data.filterNotNull().map { it[uid].orEmpty() }

    override suspend fun getById(uid: String, id: String): Result<Pokemon?> =
        if (failGetById) Result.failure(IllegalStateException("network down"))
        else Result.success(pokemonsOf(uid).firstOrNull { it.id == id })

    override suspend fun save(uid: String, pokemon: Pokemon): Result<Unit> {
        saveCalls++
        saveGate?.await()
        if (failSave) return Result.failure(IllegalStateException("permission denied"))
        seed(uid, pokemon.copy(id = "doc-${nextId++}"))
        return Result.success(Unit)
    }

    override suspend fun delete(uid: String, id: String): Result<Unit> {
        val current = data.value.orEmpty()
        data.value = current + (uid to current[uid].orEmpty().filterNot { it.id == id })
        return Result.success(Unit)
    }

    override suspend fun deleteAll(uid: String): Result<Unit> {
        log += "deleteAllPokemon"
        if (failDeleteAll) return Result.failure(IllegalStateException("network down"))
        val current = data.value.orEmpty()
        data.value = current - uid
        return Result.success(Unit)
    }
}
