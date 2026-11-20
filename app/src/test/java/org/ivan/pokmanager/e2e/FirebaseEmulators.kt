package org.ivan.pokmanager.e2e

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.core.context.stopKoin
import java.net.Socket
import java.util.UUID

/**
 * Configuracion comun de los tests que usan Firebase Emulator Suite con Robolectric.
 * Robolectric reutiliza el proceso entre tests, asi que todo debe ser idempotente.
 */
object FirebaseEmulators {
    const val PROJECT = "pokemanager-6113d"
    const val AUTH_PORT = 9099
    const val FIRESTORE_PORT = 8080

    private var configured = false

    /** Credencial aleatoria para usuarios de prueba que solo existen en el emulador. */
    fun randomCredential(): String = UUID.randomUUID().toString()

    fun areRunning(): Boolean = isUp(AUTH_PORT) && isUp(FIRESTORE_PORT)

    private fun isUp(port: Int) = runCatching { Socket("127.0.0.1", port).close() }.isSuccess

    /** Inicializa Firebase y lo apunta a los emuladores. Debe llamarse antes de lanzar la Activity. */
    fun configure() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        if (FirebaseApp.getApps(context).isEmpty()) FirebaseApp.initializeApp(context)
        if (!configured) {
            FirebaseAuth.getInstance().useEmulator("127.0.0.1", AUTH_PORT)
            FirebaseFirestore.getInstance().useEmulator("127.0.0.1", FIRESTORE_PORT)
            configured = true
        }
    }

    /** Deja el proceso limpio para el siguiente test: sin sesion y sin Koin iniciado. */
    fun reset() {
        runCatching { FirebaseAuth.getInstance().signOut() }
        stopKoin()
    }
}
