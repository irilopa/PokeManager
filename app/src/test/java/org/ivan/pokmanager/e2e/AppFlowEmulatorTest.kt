package org.ivan.pokmanager.e2e

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
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.google.firebase.auth.FirebaseAuth
import org.ivan.pokmanager.MainActivity
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.After
import org.junit.Assume.assumeTrue
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.io.File

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

    private fun addPokemon(name: String, level: String, notes: String = "") {
        type("Nombre del Pokémon", name)
        compose.onNodeWithText("Tipo Principal").performScrollTo().performClick()
        compose.onNodeWithText("Eléctrico").performClick()
        type("Nivel", level)
        if (notes.isNotEmpty()) type("Notas adicionales", notes)
        compose.onNodeWithText("Guardar Pokémon").performScrollTo().performClick()
    }

    private fun hasNode(text: String, substring: Boolean = false) =
        compose.onAllNodes(hasText(text, substring = substring)).fetchSemanticsNodes().isNotEmpty()

    private fun logout() {
        compose.onNodeWithContentDescription("Más opciones").performClick()
        compose.onNodeWithText("Cerrar sesión").performClick()
        compose.waitForText("¡Bienvenido de nuevo!")
    }

    @Test
    fun registro_login_equipo_detalle_busqueda_y_logout() {
        val email = "ash${System.currentTimeMillis()}@test.com"
        val credential = FirebaseEmulators.randomCredential()
        val wrongCredential = FirebaseEmulators.randomCredential()

        // Registro: la casilla de privacidad es obligatoria
        compose.waitForText("Iniciar Sesión")
        compose.onNodeWithText("Regístrate").performClick()
        compose.waitForText("Registrarse")
        type("Nombre", "Ash")
        type("Email", email)
        type("Fecha de Nacimiento", "01/04/1997")
        type("Contraseña", credential)
        compose.onNodeWithText("Registrarse").performScrollTo().performClick()
        compose.waitForText("Debes aceptar la Política de Privacidad")
        compose.onNodeWithText("Al registrarte aceptas nuestra Política de Privacidad").performScrollTo().performClick()
        compose.onNodeWithText("Registrarse").performScrollTo().performClick()

        // Tras registrarse se entra directamente al equipo vacio (HU-04)
        compose.waitForText("Añadir mi primer Pokémon")
        shot("01_lista_vacia")

        // Login con contraseña incorrecta: mensaje en español
        logout()
        type("Email", email)
        type("Contraseña", wrongCredential)
        compose.onNodeWithText("Iniciar Sesión").performClick()
        compose.waitForText("Email o contraseña incorrectos")
        shot("02_login_error")

        // Login correcto
        compose.onNodeWithText("Contraseña").performTextClearance()
        type("Contraseña", credential)
        compose.onNodeWithText("Iniciar Sesión").performClick()
        compose.waitForText("Añadir mi primer Pokémon")

        // HU-05: errores de validacion
        compose.onNodeWithText("Añadir mi primer Pokémon").performClick()
        compose.waitForText("Guardar Pokémon")
        compose.onNodeWithText("Guardar Pokémon").performScrollTo().performClick()
        compose.waitForText("El nombre es obligatorio")
        compose.onNodeWithText("Nombre del Pokémon").performScrollTo()
        shot("03_formulario_errores")

        // HU-06: guardado confirmado -> vuelve a la lista
        addPokemon("Pikachu", "5", notes = "Mi primer Pokémon")
        compose.waitForText("Nv. 5 · Eléctrico")

        compose.onNodeWithContentDescription("Añadir Pokémon").performClick()
        compose.waitForText("Guardar Pokémon")
        addPokemon("Pikachu", "50")
        compose.waitForText("Nv. 50 · Eléctrico")
        shot("04_lista_con_buscador")

        // IRI-19: busqueda sin resultados y filtro por tipo
        compose.onNodeWithText("Buscar en tu equipo").performTextInput("zzz")
        compose.waitForText("No se encontraron Pokémon con esos filtros.")
        shot("05_busqueda_sin_resultados")
        compose.onNodeWithText("Limpiar filtros").performClick()
        compose.waitForText("Nv. 50 · Eléctrico")
        compose.onNodeWithText("Agua").performClick()
        compose.waitForText("No se encontraron Pokémon con esos filtros.")
        compose.onNodeWithContentDescription("Limpiar filtros").performClick()
        compose.waitForText("Nv. 5 · Eléctrico")

        // HU-07 + IRI-18 + IRI-22: cada Pikachu abre su propio detalle con tipos, stats y debilidades
        compose.onNodeWithText("Nv. 50 · Eléctrico").performClick()
        compose.waitForText("Nivel 50")
        assertTrue(hasNode("Tierra ×2"))
        assertTrue(hasNode("Volador ×½"))
        compose.waitForIdle()
        shot("06_detalle_pikachu_nv50")
        assertTrue(!hasNode("Nivel 5"))
        compose.onNodeWithContentDescription("Volver").performClick()

        compose.waitForText("Nv. 5 · Eléctrico")
        compose.onNodeWithText("Nv. 5 · Eléctrico").performClick()
        compose.waitForText("Nivel 5")
        assertTrue(hasNode("Mi primer Pokémon"))
        assertTrue(!hasNode("Nivel 50"))
        compose.onNodeWithText("Debilidades y resistencias").performScrollTo()
        shot("07_detalle_pikachu_nv5_debilidades")
        compose.onNodeWithContentDescription("Volver").performClick()

        // HU-02: logout real y la pila queda vacia
        compose.waitForText("Mi Equipo Pokémon")
        logout()
        shot("08_login_tras_logout")
        assertNull(FirebaseAuth.getInstance().currentUser)

        compose.activityRule.scenario.onActivity { it.onBackPressedDispatcher.onBackPressed() }
        compose.waitForIdle()
        compose.activityRule.scenario.onActivity { assertTrue("Atrás debe cerrar la app", it.isFinishing) }
    }
}
