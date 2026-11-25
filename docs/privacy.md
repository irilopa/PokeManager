# Política de privacidad de PokéManager

**Última actualización:** 25 de noviembre de 2026

PokéManager es una aplicación Android personal, educativa y sin ánimo de lucro para gestionar una colección de Pokémon. Esta política explica qué datos trata la app, para qué y cómo puedes borrarlos.

## Responsable

**Ivan Lopez Rilopa**, autor de la aplicación. Para cualquier consulta sobre tus datos, abre un [issue en el repositorio](https://github.com/irilopa/PokeManager/issues) o contacta a través de [su perfil de GitHub](https://github.com/irilopa).

## Qué datos se recogen

| Dato | Para qué se usa | Obligatorio |
| --- | --- | --- |
| Email y contraseña | Crear tu cuenta e iniciar sesión. La contraseña la gestiona Firebase Authentication y nunca se guarda en texto legible. | Sí |
| Nombre | Mostrarlo en tu perfil. | Sí |
| Tu colección de Pokémon (nombre, número, tipos, nivel, notas y estadísticas) | Guardar y mostrar tu colección en cualquier dispositivo en el que inicies sesión. | No: solo lo que tú añadas |
| Informes de fallos (dispositivo, versión de Android y de la app, error) | Detectar y corregir errores de la app. | Se envían automáticamente si la app falla |

La app **no** recoge tu ubicación, tus contactos ni tus fotos, **no** muestra publicidad y **no** vende ni comparte tus datos con terceros con fines comerciales.

## Dónde se guardan

Los datos se guardan en servicios de **Google Firebase**, que actúa como encargado del tratamiento:

- **Firebase Authentication**: tu cuenta (email y contraseña cifrada).
- **Cloud Firestore**: tu perfil y tu colección, en `users/{tu-id}`. Las reglas de seguridad solo permiten que tú leas y escribas tus propios datos.

Puedes consultar la [política de privacidad de Google](https://policies.google.com/privacy) y los [compromisos de privacidad de Firebase](https://firebase.google.com/support/privacy).

Si la app falla, **Firebase Crashlytics** envía un informe técnico del error para poder corregirlo: el modelo del dispositivo, la versión de Android y de la app, y dónde falló el código. Estos informes no incluyen tu email, tu nombre ni tu colección, y Google los conserva 90 días.

Al buscar un Pokémon por nombre, la app consulta la [PokéAPI](https://pokeapi.co/), un servicio público. Esa consulta solo incluye el nombre del Pokémon, no tus datos personales.

## Cuánto tiempo se conservan

Mientras tengas la cuenta. Si la borras, se eliminan tu perfil, tu colección y tu cuenta de forma inmediata y definitiva.

## Cómo borrar tu cuenta y tus datos

- **Desde la app:** menú **⋮ → Eliminar cuenta**. Se borran tu colección, tu perfil y tu cuenta. Por seguridad, la app puede pedirte la contraseña antes de borrar.
- **Sin la app:** abre un [issue en el repositorio](https://github.com/irilopa/PokeManager/issues) indicando que quieres borrar tu cuenta (sin escribir tu contraseña) y se borrará manualmente en un plazo máximo de 30 días.

## Tus derechos

Según el Reglamento General de Protección de Datos (RGPD) puedes acceder a tus datos, rectificarlos, borrarlos, oponerte a su tratamiento y pedir su portabilidad. Puedes ver y corregir tu colección desde la propia app, y borrar todo como se indica arriba. Para el resto de derechos, usa los datos de contacto del responsable. También puedes reclamar ante la [Agencia Española de Protección de Datos](https://www.aepd.es/).

## Menores

La app no está dirigida a menores de 14 años. Si eres menor de esa edad, necesitas el consentimiento de tu madre, padre o tutor para crear una cuenta.

## Cambios en esta política

Si la política cambia, se actualizará la fecha de arriba y el historial de cambios queda público en el repositorio.
