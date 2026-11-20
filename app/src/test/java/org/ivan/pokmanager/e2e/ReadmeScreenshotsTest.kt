package org.ivan.pokmanager.e2e

import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Looper
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.hasScrollToNodeAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.rules.ActivityScenarioRule
import org.ivan.pokmanager.MainActivity
import org.json.JSONArray
import org.json.JSONObject
import org.junit.After
import org.junit.Assume.assumeTrue
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.GraphicsMode
import java.io.File
import java.net.HttpURLConnection
import java.net.Proxy
import java.net.URL
import java.time.Duration
import java.util.UUID

/**
 * Genera las capturas del README con una coleccion de ejemplo sembrada en Firebase Emulator Suite.
 * Se omite si los emuladores no estan arrancados. Ejecutar con:
 *
 *   firebase emulators:exec --only auth,firestore --project pokemanager-6113d \
 *     "./gradlew testDebugUnitTest --tests '*ReadmeScreenshotsTest*'"
 *
 * Las capturas quedan en app/build/readme-screenshots/.
 */
@OptIn(ExperimentalTestApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], qualifiers = "w411dp-h891dp-xxhdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class ReadmeScreenshotsTest {

    companion object {
        private const val PROJECT = "pokemanager-6113d"
        /** Usuario de prueba que solo existe en el emulador: se genera en cada ejecucion. */
        private val testPassword = "e2e-" + UUID.randomUUID()


        @BeforeClass
        @JvmStatic
        fun requireEmulators() {
            assumeTrue("Firebase Emulator Suite no está arrancado", FirebaseEmulators.areRunning())
        }
    }

    @get:Rule
    val compose: AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity> =
        createAndroidComposeRule<MainActivity>().also { FirebaseEmulators.configure() }

    @After
    fun tearDown() = FirebaseEmulators.reset()

    private val shotsDir = File(System.getProperty("user.dir"), "build/readme-screenshots").apply { mkdirs() }

    private data class Sample(
        val number: Int, val name: String, val types: String, val level: Int, val notes: String,
        val hp: Int, val atk: Int, val def: Int, val spa: Int, val spd: Int, val spe: Int
    )

    private val samples = listOf(
        Sample(6, "Charizard", "Fuego / Volador", 52, "Mi compañero desde Pueblo Paleta. Le encanta el sol.", 78, 84, 78, 109, 85, 100),
        Sample(25, "Pikachu", "Eléctrico", 25, "Capturado en el Bosque Verde.", 35, 55, 40, 50, 50, 90),
        Sample(1, "Bulbasaur", "Planta / Veneno", 12, "", 45, 49, 49, 65, 65, 45),
        Sample(130, "Gyarados", "Agua / Volador", 40, "Evolucionó en la Ruta 4.", 95, 125, 79, 60, 100, 81),
        Sample(94, "Gengar", "Fantasma / Veneno", 45, "", 60, 65, 60, 130, 75, 110)
    )

    /** Peticion HTTP directa al emulador (sin el proxy del sistema). */
    private fun http(method: String, url: String, body: JSONObject, auth: String? = null): JSONObject {
        val conn = URL(url).openConnection(Proxy.NO_PROXY) as HttpURLConnection
        conn.requestMethod = method
        conn.doOutput = true
        conn.setRequestProperty("Content-Type", "application/json")
        if (auth != null) conn.setRequestProperty("Authorization", "Bearer $auth")
        conn.outputStream.use { it.write(body.toString().toByteArray()) }
        val text = (if (conn.responseCode < 400) conn.inputStream else conn.errorStream).bufferedReader().readText()
        check(conn.responseCode < 400) { "HTTP ${conn.responseCode}: $text" }
        return JSONObject(text)
    }

    /** Crea el usuario en el emulador de Auth y siembra su coleccion en Firestore. */
    private fun seedUser(email: String) {
        val uid = http(
            "POST",
            "http://127.0.0.1:9099/identitytoolkit.googleapis.com/v1/accounts:signUp?key=fake",
            JSONObject().put("email", email).put("password", testPassword).put("returnSecureToken", true)
        ).getString("localId")

        fun int(v: Int) = JSONObject().put("integerValue", v.toString())
        fun str(v: String) = JSONObject().put("stringValue", v)
        val base = "http://127.0.0.1:8080/v1/projects/$PROJECT/databases/(default)/documents/users/$uid/pokemon"
        samples.forEach { p ->
            val stats = JSONObject().put("fields", JSONObject()
                .put("hp", int(p.hp)).put("attack", int(p.atk)).put("defense", int(p.def))
                .put("specialAttack", int(p.spa)).put("specialDefense", int(p.spd)).put("speed", int(p.spe)))
            val fields = JSONObject()
                .put("pokedexNumber", int(p.number))
                .put("name", str(p.name))
                .put("types", str(p.types))
                .put("level", int(p.level))
                .put("description", str(p.notes))
                .put("heightM", JSONObject().put("doubleValue", 0.0))
                .put("weightKg", JSONObject().put("doubleValue", 0.0))
                .put("stats", JSONObject().put("mapValue", stats))
                .put("imageUrl", str("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/${p.number}.png"))
                .put("moves", JSONObject().put("arrayValue", JSONObject().put("values", JSONArray())))
            http("POST", base, JSONObject().put("fields", fields), auth = "owner")
        }
    }

    private fun waitForText(text: String, timeoutMs: Long = 15_000) {
        compose.waitUntil(timeoutMs) { compose.onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty() }
    }

    /** Deja tiempo a Coil para descargar las imagenes y a las animaciones para terminar. */
    private fun settle(ms: Long = 4_000) {
        val end = System.currentTimeMillis() + ms
        while (System.currentTimeMillis() < end) {
            Thread.sleep(200)
            shadowOf(Looper.getMainLooper()).idleFor(Duration.ofMillis(200))
            compose.mainClock.advanceTimeBy(200)
            compose.waitForIdle()
        }
    }

    private fun shot(name: String) {
        compose.waitForIdle()
        compose.activityRule.scenario.onActivity { activity ->
            val view = activity.window.decorView
            val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
            view.draw(Canvas(bitmap))
            File(shotsDir, "$name.png").outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
        }
    }

    @Test
    fun capturas_del_readme() {
        val email = "ash@pokemanager.dev"
        seedUser(email)

        waitForText("Iniciar Sesión")
        compose.onNodeWithText("Email").performTextInput(email)
        shot("01_login")
        compose.onNodeWithText("Contraseña").performTextInput(testPassword)
        compose.onNodeWithText("Iniciar Sesión").performClick()

        waitForText("Nv. 25 · Eléctrico")
        settle()
        shot("02_coleccion")

        compose.onNode(hasScrollToNodeAction() and SemanticsMatcher.keyIsDefined(SemanticsProperties.HorizontalScrollAxisRange))
            .performScrollToNode(hasText("Volador"))
        compose.onNodeWithText("Volador").performClick()
        waitForText("Nv. 40 · Agua / Volador")
        settle(1_000)
        shot("03_filtro_tipo")
        compose.onNodeWithText("Volador").performClick()

        compose.onNodeWithText("Nv. 52 · Fuego / Volador").performClick()
        waitForText("Estadísticas base")
        settle()
        shot("04_detalle")
        compose.onNodeWithText("Inmune a").performScrollTo()
        settle(1_000)
        shot("05_debilidades")
        compose.activityRule.scenario.onActivity { it.onBackPressedDispatcher.onBackPressed() }

        waitForText("Mi Equipo Pokémon")
        compose.onNodeWithText("Nv. 25 · Eléctrico").performClick()
        waitForText("Capturado en el Bosque Verde.")
        compose.activityRule.scenario.onActivity { it.onBackPressedDispatcher.onBackPressed() }
        waitForText("Mi Equipo Pokémon")

        compose.onNodeWithText("Buscar en tu equipo").performClick()
        compose.onNodeWithText("Buscar en tu equipo").performTextInput("ga")
        settle(1_000)
        check(compose.onAllNodesWithText("Nv. 25 · Eléctrico").fetchSemanticsNodes().isEmpty()) { "la búsqueda no se aplicó" }
        shot("06_busqueda")
    }
}
