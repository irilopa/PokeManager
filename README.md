# PokéManager

🧩 **Propuesta de proyecto Android — PokéManager**

---

## 🎯 Objetivo general

Desarrollar una aplicación Android moderna que permita a los usuarios registrarse, iniciar sesión y gestionar su colección personal de Pokémon.
Cada usuario podrá **añadir, editar o eliminar Pokémon**, ver estadísticas y consultar información general (tipo, nivel, habilidades, etc.).

---

## ⚙️ Características principales

### 🔐 Autenticación de usuarios

* Registro mediante **correo electrónico y contraseña**.
* Inicio de sesión seguro con validación de credenciales.
* Persistencia de sesión mediante **EncryptedSharedPreferences**, **Room** o **Firebase Authentication** (según la fase del desarrollo).

### 🧩 Gestión de Pokémon

* Añadir Pokémon con los siguientes campos:

  * Nombre
  * Tipo (único o múltiple)
  * Nivel
  * Descripción
  * Imagen (subida o seleccionada de galería)
  * Habilidades (lista opcional)
* Editar o eliminar Pokémon existentes.
* Ver la lista de Pokémon guardados por el usuario.
* Filtros y orden por **tipo**, **nivel** o **nombre**.
* Estadísticas generales:

  * Total de Pokémon.
  * Distribución por tipo.
  * Nivel medio.

### 🎨 Interfaz y diseño

* Estilo **Flat Design**, inspirado en la **Pokédex clásica**.
* Logo visible en todas las pantallas principales.
* Paleta de colores:

  * Rojo: `#E3350D`
  * Amarillo: `#FFCC00`
  * Grises suaves para contraste y fondos.
* Accesibilidad: contraste suficiente, tamaños de texto adaptables y modo oscuro opcional.

---

## 🧰 Tecnologías recomendadas

* **Lenguaje:** Kotlin
* **Arquitectura:** MVVM (ViewModel + LiveData / Flow)
* **Persistencia:** Room o Firebase Firestore
* **Backend / Cloud:** Firebase o AWS Amplify
* **UI:** Jetpack Compose o XML con Jetpack Navigation
* **Cargas de imagen:** Coil o Glide
* **Concurrencia:** Coroutines

---

## 🧱 Estructura de datos (ejemplo)

Ejemplo de entidad Pokémon (para Room o Firestore):

```json
{
  "id": "uuid-o-int",
  "ownerUserId": "user-uid",
  "name": "Pikachu",
  "types": ["Eléctrico"],
  "level": 25,
  "description": "Ratón Pokémon. Le encantan las baterías.",
  "imageUrl": "path/o-url",
  "abilities": ["Impactrueno", "Static"],
  "createdAt": "2025-10-26T17:00:00Z",
  "updatedAt": "2025-10-26T17:05:00Z"
}
```

---

## 💡 Casos de uso principales

* Registro de usuario.
* Inicio y cierre de sesión.
* Creación de Pokémon asociado a un usuario.
* Edición y eliminación de Pokémon.
* Navegación por la lista, búsqueda y filtrado.
* Visualización de detalles y estadísticas globales.

---

## 🚀 Guía rápida de desarrollo / instalación

1. Clonar el repositorio.
2. Abrir el proyecto en **Android Studio** (versión reciente).
3. Configurar las dependencias:

   * Si usas Firebase: añadir `google-services.json` en `app/` y configurar el proyecto en Firebase Console.
   * Si usas Room: no se requiere configuración extra.
4. Ejecutar en emulador o dispositivo físico.
5. (Opcional) Cargar datos de ejemplo desde un JSON inicial.

### 🔧 Configuración Firebase (opcional)

* Activar **Authentication (Email/Password)**.
* Configurar **Firestore / Realtime Database** con reglas seguras (solo acceso por usuario autenticado).
* Configurar **Firebase Storage** si se gestionan imágenes.

---

## 🧭 Diseño y UX

### Pantallas mínimas

* **Splash / Logo inicial**
* **Login / Registro**
* **Lista principal de Pokémon**
* **Detalle de Pokémon**
* **Formulario de creación / edición**
* **Perfil o ajustes (opcional)**

### Componentes reutilizables

* Tarjeta de Pokémon.
* Barra superior con logo.
* Buscador y filtros dinámicos.

🎨 **Estilo general:**
Botones principales en rojo (`#E3350D`) y elementos destacados en amarillo (`#FFCC00`).

---

## 🗓️ Roadmap del proyecto

| Fase       | Descripción                                                         | Objetivo            |
| ---------- | ------------------------------------------------------------------- | ------------------- |
| **MVP**    | Autenticación básica y CRUD local                                   | Base funcional      |
| **Fase 2** | Sincronización en la nube (Firebase / Amplify) y subida de imágenes | Persistencia remota |
| **Fase 3** | Filtros avanzados, estadísticas, integración con PokéAPI            | Versión avanzada    |
| **Fase 4** | Compartir colecciones, soporte multidioma, UI mejorada              | Versión final       |

---

## 🤝 Contribución

* Reportar bugs o mejoras creando un *issue*.
* Crear ramas con el formato `feature/nombre-feature`.
* Realizar *pull requests* con descripción detallada.
* Seguir las **buenas prácticas de Kotlin y Android Jetpack**.

---

## 📚 Recursos y referencias

* 🔗 [PokéAPI](https://pokeapi.co) — Datos oficiales de Pokémon.
* 🔗 [Firebase Docs](https://firebase.google.com/docs)
* 🔗 [Android Developers](https://developer.android.com)
* 🔗 [AWS Amplify Docs](https://docs.amplify.aws)

---

## ⚖️ Licencia

Licencia **MIT**
---

> 💬 Proyecto académico Android realizado en Kotlin con enfoque profesional, centrado en arquitectura limpia, diseño intuitivo y escalabilidad mediante servicios en la nube.
