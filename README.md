# PokéManager

Aplicación Android para gestionar una colección personal de Pokémon y consultar sus datos en PokéAPI.

![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-7F52FF?logo=kotlin&logoColor=white)
![Android](https://img.shields.io/badge/Android-API%2026%2B-3DDC84?logo=android&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?logo=jetpackcompose&logoColor=white)
![Versión](https://img.shields.io/badge/versi%C3%B3n-1.0-blue)
![Licencia](https://img.shields.io/badge/licencia-MIT-green)

## 📑 Contenido

- [Sobre el proyecto](#-sobre-el-proyecto)
- [Arquitectura y stack tecnológico](#-arquitectura-y-stack-tecnológico)
- [Capturas de pantalla y demo](#-capturas-de-pantalla-y-demo)
- [Requisitos previos](#-requisitos-previos)
- [Instalación y despliegue local](#-instalación-y-despliegue-local)
- [Variables de entorno](#-variables-de-entorno)
- [Estructura del proyecto](#-estructura-del-proyecto)
- [Uso y API](#-uso-y-api)
- [Licencia y contacto](#-licencia-y-contacto)

## 🎯 Sobre el proyecto

PokéManager permite a cada usuario autenticado guardar y consultar su colección de Pokémon desde Android.

- Registro e inicio de sesión con correo y contraseña mediante Firebase Authentication.
- Consulta de Pokémon por nombre en PokéAPI para completar datos del formulario.
- Almacenamiento y listado de Pokémon asociados al usuario en Cloud Firestore.
- Visualización de detalles y eliminación de Pokémon de la colección.

## 🧱 Arquitectura y stack tecnológico

La aplicación utiliza MVVM y organiza el código en capas de presentación, dominio y datos, con casos de uso y repositorios.

| Área | Tecnología |
| --- | --- |
| Aplicación móvil | Kotlin 2.0.21, Jetpack Compose, Material 3 y Navigation Compose |
| Estado y concurrencia | ViewModel, Kotlin Coroutines y Flow |
| Autenticación | Firebase Authentication |
| Base de datos | Cloud Firestore |
| API externa | PokéAPI, Retrofit, OkHttp y Gson |
| Imágenes | Coil |
| Inyección de dependencias | Koin |
| Compilación | Android Gradle Plugin 8.13.2 y Gradle Wrapper 8.13 |
| Infraestructura | Servicios gestionados de Firebase; no hay servidor propio en el repositorio |

## 🖼️ Capturas de pantalla y demo

| Inicio de sesión | Colección | Detalle |
| --- | --- | --- |
| [COMPLETAR: captura de inicio de sesión] | [COMPLETAR: captura de la colección] | [COMPLETAR: captura del detalle] |

**Demo:** [COMPLETAR: enlace a vídeo o GIF]

## ✅ Requisitos previos

- Android Studio compatible con Android Gradle Plugin 8.13.2.
- JDK 17 para ejecutar Gradle; el proyecto compila con compatibilidad de Java 11.
- Android SDK 36 y un emulador o dispositivo con Android 8.0 (API 26) o superior.
- Un proyecto de Firebase con Authentication y Cloud Firestore configurados.
- Conexión a Internet para resolver dependencias y acceder a Firebase y PokéAPI.

El repositorio incluye Gradle Wrapper 8.13; no es necesario instalar Gradle por separado.

## 🚀 Instalación y despliegue local

1. Clona el repositorio y accede al directorio:

   ```bash
   git clone https://github.com/irilopa/PokeManager.git
   cd PokeManager
   ```

2. Abre el proyecto en Android Studio, configura el JDK 17 y el SDK de Android 36 y sincroniza Gradle.

3. En Firebase Console, registra una aplicación Android con el identificador `org.ivan.pokmanager`. Descarga el archivo `google-services.json` de tu proyecto y colócalo en `app/google-services.json`. El repositorio ya contiene un archivo en esa ruta; sustitúyelo si utilizas otro proyecto de Firebase.

4. Activa el proveedor **Correo electrónico/contraseña** en Firebase Authentication y crea una base de datos Cloud Firestore. Configura reglas de seguridad que permitan a cada usuario autenticado acceder únicamente a sus propios datos, incluidos los documentos de `users/{uid}/pokemon`.

5. Selecciona un emulador o dispositivo conectado y ejecuta la aplicación desde Android Studio. También puedes generar el APK de depuración desde la raíz:

   ```bash
   ./gradlew assembleDebug
   ```

## 🔧 Variables de entorno

El código de la aplicación no define variables de entorno propias para Firebase ni para PokéAPI. Firebase se configura mediante `app/google-services.json`; la URL base de PokéAPI está definida en el código. No se requiere un archivo `.env`.

## 📁 Estructura del proyecto

```text
.
├── app/
│   ├── build.gradle.kts              # Configuración del módulo Android
│   ├── google-services.json          # Configuración de Firebase
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml   # Permisos y componentes Android
│       │   ├── java/org/ivan/pokmanager/
│       │   │   ├── data/             # DTO y acceso a PokéAPI
│       │   │   ├── di/               # Módulos de inyección de dependencias
│       │   │   ├── domain/           # Modelos, repositorios y casos de uso
│       │   │   └── presentation/     # Pantallas, navegación y ViewModels
│       │   └── res/                  # Recursos Android
│       ├── test/                     # Pruebas unitarias
│       └── androidTest/              # Pruebas instrumentadas
├── gradle/                           # Catálogo de versiones y wrapper
├── build.gradle.kts                  # Plugins del proyecto
├── gradle.properties                 # Propiedades de Gradle
├── gradlew                           # Gradle Wrapper para Unix
├── gradlew.bat                       # Gradle Wrapper para Windows
└── settings.gradle.kts               # Repositorios y módulos
```

## 📖 Uso y API

1. Registra una cuenta o inicia sesión.
2. En la pantalla de la colección, añade un Pokémon. Puedes consultar su nombre en PokéAPI para completar los datos disponibles.
3. Selecciona un Pokémon para ver su detalle o utiliza el botón de eliminación para retirarlo de la colección.

La aplicación consume el endpoint público de PokéAPI `GET https://pokeapi.co/api/v2/pokemon/{name}`; también admite un identificador numérico. Ejemplo:

```bash
curl https://pokeapi.co/api/v2/pokemon/pikachu
```

Documentación externa: [PokéAPI](https://pokeapi.co/docs/v2).

## ⚖️ Licencia y contacto

La documentación anterior declaraba la licencia MIT, pero el repositorio no incluye un archivo `LICENSE` con su texto. [COMPLETAR: añadir o confirmar el archivo de licencia].

Contacto: [irilopa en GitHub](https://github.com/irilopa) · [Repositorio](https://github.com/irilopa/PokeManager)
