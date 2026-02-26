# PokéManager

🧩 **Aplicacion Android — PokéManager**

---

## 🎯 Objetivo

Aplicacion Android moderna que permite a los usuarios registrarse, iniciar sesion y gestionar su equipo personal de Pokemon.
Cada usuario puede **anadir o eliminar Pokemon**, ver detalles y consultar datos generales desde la PokeAPI.

---

## ⚙️ Caracteristicas principales

### 🔐 Autenticacion de usuarios

* Registro mediante **correo electronico y contrasena**.
* Inicio de sesion con **Firebase Authentication**.

### 🧩 Gestion de Pokemon

* Anadir Pokemon con autocompletado desde **PokeAPI**.
* Guardado de Pokemon por usuario en **Firestore**.
* Lista del equipo con navegacion a detalle.
* Eliminacion de Pokemon.

### 🎨 Interfaz y diseno

* Estilo inspirado en la **Pokedex clasica**.
* Logo visible en todas las pantallas principales.
* Paleta de colores:

  * Rojo: `#E3350D`
  * Amarillo: `#FFCC00`
  * Grises suaves para contraste y fondos.
* Accesibilidad: contraste suficiente, tamaños de texto adaptables y modo oscuro opcional.

---

## 🧰 Tecnologias

* **Lenguaje:** Kotlin
* **Arquitectura:** MVVM (ViewModel + Flow)
* **Persistencia:** Firebase Firestore
* **Backend / Cloud:** Firebase Auth
* **UI:** Jetpack Compose + Navigation
* **Imagenes:** Coil
* **Concurrencia:** Coroutines

---

## 🧱 Estructura de datos (ejemplo)

 Ejemplo de entidad Pokémon (para Room o Firestore):

```json
{
  "id": "uuid-o-int",
  "ownerUserId": "user-uid",
  "name": "Pikachu",
  "types": "Electrico",
  "level": 25,
  "description": "Ratón Pokémon. Le encantan las baterías.",
  "imageUrl": "path/o-url",
  "moves": ["Impactrueno", "Placaje"],
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
 * Navegacion por la lista y detalle.

---

## 🚀 Guia rapida de desarrollo / instalacion

 1. Clonar el repositorio.
 2. Abrir el proyecto en **Android Studio** (versión reciente).
 3. Configurar las dependencias:

   * Añadir `google-services.json` en `app/` y configurar el proyecto en Firebase Console.
 4. Ejecutar en emulador o dispositivo físico.
 5. (Opcional) Cargar datos de ejemplo desde un JSON inicial.

### 🔧 Configuracion Firebase

 * Activar **Authentication (Email/Password)**.
 * Configurar **Firestore** con reglas seguras (solo acceso por usuario autenticado).

---

## 🧭 Diseno y UX

### Pantallas mínimas

* **Login / Registro**
* **Lista principal de Pokemon**
* **Detalle de Pokemon**
* **Formulario de creacion**

### Componentes reutilizables

* Tarjeta de Pokémon.
* Barra superior con logo.
* Buscador y filtros dinamicos (pendiente).

🎨 **Estilo general:**
Botones principales en rojo (`#E3350D`) y elementos destacados en amarillo (`#FFCC00`).

---

## 🗓️ Roadmap del proyecto

| Fase       | Descripción                                                         | Objetivo            |
| ---------- | ------------------------------------------------------------------- | ------------------- |
| **MVP**    | Autenticacion basica y CRUD en Firestore                            | Base funcional      |
| **Fase 2** | Integracion con PokeAPI y autocompletado                            | Datos enriquecidos  |
| **Fase 3** | Filtros avanzados y estadisticas                                    | Version avanzada    |
| **Fase 4** | Compartir colecciones, soporte multidioma, UI mejorada              | Version final       |

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

> 💬 Proyecto academico Android realizado en Kotlin con enfoque profesional, centrado en arquitectura limpia, diseno intuitivo y escalabilidad mediante servicios en la nube.
