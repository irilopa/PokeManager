package org.ivan.pokmanager.e2e

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.core.app.ApplicationProvider
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.ivan.pokmanager.MainActivity
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeTrue
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.io.File
import java.net.Socket

/**
 * Recorre la app completa (Koin + SDK real de Firebase) contra Firebase Emulator Suite.
 * Se omite si los emuladores no estan arrancados. Ejecutar con:
 *
 *   firebase emulators:exec --only auth,firestore --project pokemanager-6113d \
 *     "./gradlew testDebugUnitTest --tests '*AppFlowEmulatorTest*'"
 */
@OptIn(ExperimentalTestApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], qualifiers = "w411dp-h891dp-xxhdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class AppFlowEmulatorTest {

    companion object {
        private fun isUp(port: Int) = runCatching { Socket("127.0.0.1", port).close() }.isSuccess

        @BeforeClass
        @JvmStatic
        fun requireEmulators() {
            assumeTrue("Firebase Emulator Suite no está arrancado", isUp(9099) && isUp(8080))
        }
    }

    @get:Rule
    val compose: AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity> =
        createAndroidComposeRule<MainActivity>().also {
            // Robolectric no ejecuta FirebaseInitProvider: se inicializa a mano con google-services.json.
            val context = ApplicationProvider.getApplicationContext<Context>()
            if (FirebaseApp.getApps(context).isEmpty()) FirebaseApp.initializeApp(context)
            // Debe hacerse antes de que Koin cree los singletons de Firebase.
            FirebaseAuth.getInstance().useEmulator("127.0.0.1", 9099)
            FirebaseFirestore.getInstance().useEmulator("127.0.0.1", 8080)
        }

    private val shotsDir = File(System.getProperty("user.dir"), "build/e2e-screenshots").apply { mkdirs() }

    private fun shot(name: String) {
        compose.waitForIdle()
        compose.activityRule.scenario.onActivity { activity ->
            val view = activity.window.decorView
            val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
            view.draw(Canvas(bitmap))
            File(shotsDir, "$name.png").outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
        }
    }

    private fun SemanticsNodeInteractionsProvider.waitForText(text: String, timeoutMs: Long = 15_000) {
        compose.waitUntil(timeoutMs) { onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty() }
    }

    private fun type(label: String, value: String) {
        compose.onNodeWithText(label).performScrollTo().performTextInput(value)
    }

    private fun addPokemon(name: String, level: String) {
        type("Nombre del Pokémon", name)
        compose.onNodeWithText("Tipo Principal").performScrollTo().performClick()
        compose.onNodeWithText("Eléctrico").performClick()
        type("Nivel", level)
        compose.onNodeWithText("Guardar Pokémon").performScrollTo().performClick()
    }

    @Test
    fun registro_login_equipo_detalle_y_logout() {
        val email = "ash${System.currentTimeMillis()}@test.com"

        // Registro
        compose.waitForText("Iniciar Sesión")
        compose.onNodeWithText("Regístrate").performClick()
        compose.waitForText("Registrarse")
        type("Nombre", "Ash")
        type("Email", email)
        type("Fecha de Nacimiento", "01/04/1997")
        type("Contraseña", "pikachu123")
        compose.onNodeWithText("Registrarse").performScrollTo().performClick()

        // Login
        compose.waitForText("¡Bienvenido de nuevo!")
        type("Email", email)
        type("Contraseña", "pikachu123")
        compose.onNodeWithText("Iniciar Sesión").performClick()

        // HU-04: equipo vacio
        compose.waitForText("Añadir mi primer Pokémon")
        shot("01_lista_vacia")

        // HU-05: errores de validacion
        compose.onNodeWithText("Añadir mi primer Pokémon").performClick()
        compose.waitForText("Guardar Pokémon")
        compose.onNodeWithText("Guardar Pokémon").performScrollTo().performClick()
        compose.waitForText("El nombre es obligatorio")
        compose.onNodeWithText("Nombre del Pokémon").performScrollTo()
        shot("02_formulario_errores")

        // HU-06: guardado confirmado -> vuelve a la lista
        addPokemon("Pikachu", "5")
        compose.waitForText("Nv. 5 · Eléctrico")
        shot("03_lista_un_pokemon")

        // Segundo Pikachu con distinto nivel
        compose.onNodeWithContentDescription("Añadir Pokémon").performClick()
        compose.waitForText("Guardar Pokémon")
        addPokemon("Pikachu", "50")
        compose.waitForText("Nv. 50 · Eléctrico")
        shot("04_lista_dos_pikachu")

        // HU-07: cada Pikachu abre su propio detalle
        compose.onNodeWithText("Nv. 50 · Eléctrico").performClick()
        compose.waitForText("Nv. 50 · Eléctrico", 15_000)
        compose.waitUntil(15_000) { compose.onAllNodesWithText("Detalle Pokémon").fetchSemanticsNodes().isNotEmpty() }
        shot("05_detalle_pikachu_nv50")
        assertTrue(compose.onAllNodesWithText("Nv. 5 · Eléctrico").fetchSemanticsNodes().isEmpty())
        compose.onNodeWithContentDescription("Volver").performClick()

        compose.waitForText("Nv. 5 · Eléctrico")
        compose.onNodeWithText("Nv. 5 · Eléctrico").performClick()
        compose.waitUntil(15_000) {
            compose.onAllNodesWithText("Detalle Pokémon").fetchSemanticsNodes().isNotEmpty() &&
                compose.onAllNodes(hasText("Nv. 5", substring = true)).fetchSemanticsNodes().isNotEmpty()
        }
        assertTrue(compose.onAllNodesWithText("Nv. 50 · Eléctrico").fetchSemanticsNodes().isEmpty())
        shot("06_detalle_pikachu_nv5")
        compose.onNodeWithContentDescription("Volver").performClick()

        // HU-02: logout real y la pila queda vacia
        compose.waitForText("Mi Equipo Pokémon")
        compose.onNodeWithContentDescription("Más opciones").performClick()
        compose.onNodeWithText("Cerrar sesión").performClick()
        compose.waitForText("¡Bienvenido de nuevo!")
        shot("07_login_tras_logout")
        assertNull(FirebaseAuth.getInstance().currentUser)

        compose.activityRule.scenario.onActivity { it.onBackPressedDispatcher.onBackPressed() }
        compose.waitForIdle()
        compose.activityRule.scenario.onActivity { assertTrue("Atrás debe cerrar la app", it.isFinishing) }
    }
}
