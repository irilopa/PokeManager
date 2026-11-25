package org.ivan.pokmanager.e2e

import android.app.Application
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assume.assumeTrue
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.net.HttpURLConnection
import java.net.Proxy
import java.net.URL
import java.util.Base64
import java.util.UUID

/**
 * Prueba firestore.rules contra el emulador de Firestore, escribiendo con la API REST
 * como lo haria alguien que se salta la app. Se omite si los emuladores no estan arrancados.
 *
 * Usa Robolectric solo para disponer de org.json (parte de Android).
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], application = Application::class)
class FirestoreRulesEmulatorTest {

    companion object {
        @BeforeClass
        @JvmStatic
        fun requireEmulators() {
            assumeTrue("Firebase Emulator Suite no está arrancado", FirebaseEmulators.areRunning())
        }
    }

    private val uid = "rules-" + UUID.randomUUID()
    private val base = "http://127.0.0.1:${FirebaseEmulators.FIRESTORE_PORT}/v1/projects/" +
        "${FirebaseEmulators.PROJECT}/databases/(default)/documents"

    /** Token sin firma que el emulador acepta para simular a un usuario autenticado. */
    private fun tokenFor(userId: String): String {
        val encoder = Base64.getUrlEncoder().withoutPadding()
        val now = System.currentTimeMillis() / 1000
        val header = JSONObject().put("alg", "none").put("typ", "JWT")
        val payload = JSONObject()
            .put("sub", userId).put("user_id", userId)
            .put("iss", "https://securetoken.google.com/${FirebaseEmulators.PROJECT}")
            .put("aud", FirebaseEmulators.PROJECT)
            .put("iat", now).put("exp", now + 3600).put("auth_time", now)
            .put("firebase", JSONObject().put("sign_in_provider", "password"))
        return encoder.encodeToString(header.toString().toByteArray()) + "." +
            encoder.encodeToString(payload.toString().toByteArray()) + "."
    }

    /**
     * Crea un documento con la API REST (POST con documentId) y devuelve el codigo HTTP:
     * 200 permitido, 403 denegado por las reglas.
     */
    private fun create(collection: String, docId: String, fields: JSONObject, asUser: String?): Int {
        val conn = URL("$base/$collection?documentId=$docId").openConnection(Proxy.NO_PROXY) as HttpURLConnection
        conn.requestMethod = "POST"
        conn.setRequestProperty("Content-Type", "application/json")
        if (asUser != null) conn.setRequestProperty("Authorization", "Bearer ${tokenFor(asUser)}")
        conn.doOutput = true
        conn.outputStream.use { it.write(JSONObject().put("fields", fields).toString().toByteArray()) }
        return conn.responseCode
    }

    /** Crea un Pokemon nuevo en la coleccion del usuario de la prueba. */
    private fun writePokemon(fields: JSONObject, asUser: String? = uid): Int =
        create("users/$uid/pokemon", "p" + UUID.randomUUID().toString().take(8), fields, asUser)

    /** Crea el perfil de un usuario nuevo, autenticado como ese usuario. */
    private fun writeProfile(fields: JSONObject): Int {
        val owner = "profile-" + UUID.randomUUID()
        return create("users", owner, fields, owner)
    }

    private fun int(v: Long) = JSONObject().put("integerValue", v.toString())
    private fun str(v: String) = JSONObject().put("stringValue", v)
    private fun dbl(v: Double) = JSONObject().put("doubleValue", v)

    private fun stats(hp: Long = 35, extra: Map<String, JSONObject> = emptyMap()): JSONObject {
        val f = JSONObject()
            .put("hp", int(hp)).put("attack", int(55)).put("defense", int(40))
            .put("specialAttack", int(50)).put("specialDefense", int(50)).put("speed", int(90))
        extra.forEach { (k, v) -> f.put(k, v) }
        return JSONObject().put("mapValue", JSONObject().put("fields", f))
    }

    /** Pokemon valido con los mismos campos que escribe la app. */
    private fun pokemon(): JSONObject = JSONObject()
        .put("pokedexNumber", int(25))
        .put("name", str("Pikachu"))
        .put("types", str("Eléctrico"))
        .put("level", int(25))
        .put("description", str("Capturado en el Bosque Verde"))
        .put("heightM", dbl(0.0))
        .put("weightKg", dbl(0.0))
        .put("stats", stats())
        .put("imageUrl", str("https://raw.githubusercontent.com/PokeAPI/sprites/master/x/25.png"))
        .put("moves", JSONObject().put("arrayValue", JSONObject().put("values", JSONArray())))

    private val ALLOWED = 200
    private val DENIED = 403

    @Test
    fun `el duenio puede guardar un Pokemon valido`() {
        assertEquals(ALLOWED, writePokemon(pokemon()))
    }

    @Test
    fun `otro usuario o alguien sin sesion no pueden escribir`() {
        assertEquals(DENIED, writePokemon(pokemon(), asUser = "otro-usuario"))
        assertEquals(DENIED, writePokemon(pokemon(), asUser = null))
    }

    @Test
    fun `se rechazan campos desconocidos`() {
        assertEquals(DENIED, writePokemon(pokemon().put("isAdmin", JSONObject().put("booleanValue", true))))
        assertEquals(DENIED, writePokemon(pokemon().put("stats", stats(extra = mapOf("luck" to int(1))))))
    }

    @Test
    fun `se rechazan valores fuera de los limites del formulario`() {
        assertEquals(DENIED, writePokemon(pokemon().put("level", int(9999))))
        assertEquals(DENIED, writePokemon(pokemon().put("level", int(0))))
        assertEquals(DENIED, writePokemon(pokemon().put("name", str(""))))
        assertEquals(DENIED, writePokemon(pokemon().put("name", str("x".repeat(31)))))
        assertEquals(DENIED, writePokemon(pokemon().put("description", str("x".repeat(201)))))
        assertEquals(DENIED, writePokemon(pokemon().put("stats", stats(hp = 300))))
        assertEquals(DENIED, writePokemon(pokemon().put("pokedexNumber", int(-1))))
    }

    @Test
    fun `se rechazan tipos incorrectos y campos obligatorios ausentes`() {
        assertEquals(DENIED, writePokemon(pokemon().put("level", str("25"))))
        val withoutName = pokemon().also { it.remove("name") }
        assertEquals(DENIED, writePokemon(withoutName))
    }

    @Test
    fun `el perfil solo admite nombre y email`() {
        val profile = JSONObject().put("name", str("Ash")).put("email", str("ash@test.com"))
        assertEquals(ALLOWED, writeProfile(profile))
        assertEquals(DENIED, writeProfile(JSONObject(profile.toString()).put("role", str("admin"))))
        assertEquals(DENIED, writeProfile(JSONObject().put("name", str("Ash"))))
    }
}
