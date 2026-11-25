# Historial de bugs

Registro de los errores encontrados en PokéManager: qué pasaba, por qué, cómo se arregló y qué test evita que vuelva. Los más recientes van al final de cada sección.

**Gravedad:** 🔴 Crítica (seguridad o pérdida de datos) · 🟠 Alta (rompe una función o un requisito legal) · 🟡 Media (comportamiento incorrecto con alternativa) · 🔵 Baja (calidad del código).

## Resumen

| ID | Fecha del arreglo | Gravedad | Área | Bug | Estado |
| --- | --- | --- | --- | --- | --- |
| [BUG-001](#bug-001) | 2026-11-20 | 🔴 Crítica | Seguridad | Reglas de Firestore abiertas en producción | ✅ Arreglado y publicado |
| [BUG-002](#bug-002) | 2026-11-20 | 🔴 Crítica | Datos | El guardado no esperaba a Firestore: Pokémon perdidos o duplicados | ✅ Arreglado |
| [BUG-003](#bug-003) | 2026-11-20 | 🟠 Alta | Seguridad | «Cerrar sesión» no cerraba la sesión | ✅ Arreglado |
| [BUG-004](#bug-004) | 2026-11-20 | 🟠 Alta | Datos | Dos Pokémon con el mismo número abrían el mismo detalle | ✅ Arreglado |
| [BUG-005](#bug-005) | 2026-11-20 | 🟠 Alta | Datos | Número de Pokédex inventado con el reloj | ✅ Arreglado |
| [BUG-006](#bug-006) | 2026-11-20 | 🟡 Media | Datos | El nivel se pedía pero no se guardaba | ✅ Arreglado |
| [BUG-007](#bug-007) | 2026-11-20 | 🟡 Media | UX | «No se encontró el Pokémon» mientras cargaba | ✅ Arreglado |
| [BUG-008](#bug-008) | 2026-11-20 | 🟡 Media | Datos | Borrar un Pokémon fallaba sin avisar | ✅ Arreglado |
| [BUG-009](#bug-009) | 2026-11-20 | 🔵 Baja | Código | Repositorio de usuarios duplicado que leía perfiles como Pokémon | ✅ Arreglado |
| [BUG-010](#bug-010) | 2026-11-20 | 🟠 Alta | Legal | La casilla de privacidad no se podía marcar ni era obligatoria | ✅ Arreglado |
| [BUG-011](#bug-011) | 2026-11-20 | 🟡 Media | UX | Errores de login y registro en inglés técnico | ✅ Arreglado |
| [BUG-012](#bug-012) | 2026-11-20 | 🟡 Media | UX | La sesión no se mantenía entre aperturas | ✅ Arreglado |
| [BUG-013](#bug-013) | 2026-11-25 | 🟠 Alta | Legal | La política de privacidad que se aceptaba no existía | ✅ Arreglado |
| [BUG-014](#bug-014) | 2026-11-25 | 🟡 Media | Privacidad | Se pedía la fecha de nacimiento sin usarla | ✅ Arreglado |
| [BUG-015](#bug-015) | 2026-11-25 | 🟠 Alta | Seguridad | Firestore aceptaba cualquier contenido | ✅ Arreglado · ⏳ pendiente de publicar |
| [BUG-016](#bug-016) | 2026-11-25 | 🟡 Media | Seguridad | Tráfico HTTP sin cifrar permitido en la versión publicada | ✅ Arreglado |
| [BUG-017](#bug-017) | 2026-11-25 | 🟠 Alta | Accesibilidad | Textos invisibles en modo oscuro | ✅ Arreglado |
| [BUG-018](#bug-018) | 2026-11-25 | 🟡 Media | UX | Sin conexión, la app decía «Pokémon no encontrado» | ✅ Arreglado |

## Detalle

### BUG-001

**🔴 Reglas de Firestore abiertas en producción**

- **Síntoma:** las reglas publicadas en el proyecto `pokemanager-6113d` desde el 18-02-2026 eran `allow read, write: if true`.
- **Impacto:** cualquiera que conociera el ID del proyecto podía leer, modificar o borrar los datos de todos los usuarios sin iniciar sesión.
- **Causa:** se publicaron las reglas de prueba de Firebase y nunca se sustituyeron.
- **Arreglo:** [`firestore.rules`](../firestore.rules) solo permite a cada usuario leer y escribir `users/{uid}` y `users/{uid}/pokemon`. Publicado en producción el 20-11-2026 (ruleset `51b4f049`) y verificado con 8 casos en la API de pruebas de Firebase Rules.
- **Commit:** `a3a22a8`
- **Pendiente:** si alguien accedió antes del arreglo, no queda registro en la app. Conviene revisar los datos en la consola de Firebase.

### BUG-002

**🔴 El guardado no esperaba a Firestore: Pokémon perdidos o duplicados**

- **Síntoma:** al pulsar «Guardar Pokémon», la app volvía a la lista al instante. Si el guardado fallaba, el Pokémon se perdía sin aviso. Pulsar dos veces creaba dos Pokémon.
- **Causa:** `savePokemon()` lanzaba el guardado en segundo plano y la pantalla navegaba sin esperar el resultado; el repositorio devolvía `false` en lugar de un error.
- **Arreglo:** `SaveState` (`Saving` / `Success` / `Error`). El botón se desactiva mientras guarda, solo se vuelve a la lista con `Success`, si falla se conservan los datos y se avisa, y las pulsaciones repetidas se ignoran.
- **Commit:** `c141de0` (HU-06)
- **Test:** `AddPokemonScreenViewModelTest` («pulsar dos veces seguidas no crea dos pokemon», «si falla el guardado hay Error y se conservan los datos»).

### BUG-003

**🟠 «Cerrar sesión» no cerraba la sesión**

- **Síntoma:** «Cerrar sesión» llevaba al login, pero la cuenta seguía abierta en Firebase y «Atrás» volvía al equipo.
- **Causa:** el botón solo navegaba; nunca llamaba a `FirebaseAuth.signOut()` ni vaciaba la pila de navegación.
- **Arreglo:** `logout()` llama a `signOut()` y la navegación vacía toda la pila.
- **Commit:** `c141de0` (HU-02)
- **Test:** `PokemonListViewModelTest` («logout cierra la sesion») y `AppFlowEmulatorTest` (comprueba que no queda usuario y que «Atrás» cierra la app).

### BUG-004

**🟠 Dos Pokémon con el mismo número abrían el mismo detalle**

- **Síntoma:** con dos Pikachu en el equipo, pulsar cualquiera mostraba siempre el primero.
- **Causa:** la ruta del detalle usaba el número de Pokédex y el ViewModel buscaba el primer Pokémon con ese número.
- **Arreglo:** la ruta usa el id del documento de Firestore y `GetPokemonByIdUseCase`.
- **Commit:** `c141de0` (HU-07)
- **Test:** `PokemonDetailViewModelTest` («dos pokemon con el mismo numero de pokedex abren detalles distintos») y `AppFlowEmulatorTest`.

### BUG-005

**🟠 Número de Pokédex inventado con el reloj**

- **Síntoma:** si no se buscaba el Pokémon en la PokéAPI, se guardaba con un número absurdo (por ejemplo, 1763647200) y una imagen rota.
- **Causa:** `pokedexNumberInt ?: (System.currentTimeMillis() / 1000).toInt()`.
- **Arreglo:** el número es opcional; si está vacío se guarda `0` y se muestra un icono en lugar de la imagen.
- **Commit:** `c141de0` (HU-05)
- **Test:** `AddPokemonScreenViewModelTest` («sin numero de pokedex no se inventa uno»).
- **Datos antiguos:** los Pokémon guardados antes del arreglo conservan el número inventado.

### BUG-006

**🟡 El nivel se pedía pero no se guardaba**

- **Síntoma:** el formulario tenía un campo «Nivel», pero el valor no aparecía en ningún sitio.
- **Causa:** el modelo `Pokemon` no tenía campo `level` y el ViewModel lo descartaba.
- **Arreglo:** campo `level` en el modelo, validado entre 1 y 100 y mostrado en la lista y el detalle.
- **Commit:** `c141de0` (HU-05)
- **Test:** `AddPokemonScreenViewModelTest`, `PokemonFormValidatorTest`.
- **Datos antiguos:** los Pokémon guardados antes del arreglo no tienen nivel y se muestran sin él.

### BUG-007

**🟡 «No se encontró el Pokémon» mientras cargaba**

- **Síntoma:** al abrir un detalle aparecía «No se encontró el Pokémon» durante la carga. Si Firestore fallaba, la lista se quedaba vacía sin explicación.
- **Causa:** la UI trataba «todavía no hay datos» igual que «no existe» y los errores del flujo no se recogían.
- **Arreglo:** estados `Loading` / `Success` / `Error` en lista y detalle, con indicador de carga y botón «Reintentar».
- **Commit:** `c141de0` (HU-03)
- **Test:** `PokemonListViewModelTest` y `PokemonDetailViewModelTest` (carga, error y reintento).

### BUG-008

**🟡 Borrar un Pokémon fallaba sin avisar**

- **Síntoma:** si el borrado fallaba (sin conexión o sin permiso), el Pokémon seguía en la lista y no se decía nada.
- **Causa:** se ignoraba el resultado de `deletePokemonUseCase`.
- **Arreglo:** el error se muestra con un aviso («No se pudo liberar a …»).
- **Commit:** `c141de0`

### BUG-009

**🔵 Repositorio de usuarios duplicado que leía perfiles como Pokémon**

- **Síntoma:** existían dos repositorios de usuarios. `UserFirestoreRepository.list()` leía la colección `users` como si fueran Pokémon, y había código sin uso (`queryForSingle`, `SaveUserUseCase`, DTO).
- **Impacto:** no se llamaba desde la app, pero cualquiera que lo usara obtendría datos incorrectos.
- **Arreglo:** un único `UserRepository`; código muerto eliminado.
- **Commit:** `197f9d8` (HU-01)

### BUG-010

**🟠 La casilla de privacidad no se podía marcar ni era obligatoria**

- **Síntoma:** la casilla «Al registrarte aceptas nuestra Política de Privacidad» no respondía al pulsarla y el registro funcionaba sin aceptarla.
- **Causa:** `Checkbox(checked = false, onCheckedChange = {})`.
- **Arreglo:** la casilla funciona y es obligatoria para registrarse.
- **Commit:** `01a5c07`
- **Test:** `AppFlowEmulatorTest` (el registro falla sin marcarla).

### BUG-011

**🟡 Errores de login y registro en inglés técnico**

- **Síntoma:** al equivocarse de contraseña aparecía el mensaje original de Firebase («The supplied auth credential is incorrect, malformed or has expired»).
- **Causa:** se mostraba `exception.message` sin traducir; el registro comparaba textos en inglés.
- **Arreglo:** `AuthError` traduce cada tipo de excepción de Firebase Auth a un mensaje en español.
- **Commit:** `01a5c07`
- **Test:** `AppFlowEmulatorTest` («Email o contraseña incorrectos»).

### BUG-012

**🟡 La sesión no se mantenía entre aperturas**

- **Síntoma:** cada vez que se abría la app pedía iniciar sesión, aunque Firebase mantenía al usuario. Tras registrarse también obligaba a iniciar sesión.
- **Causa:** la navegación empezaba siempre en el login.
- **Arreglo:** si hay usuario de Firebase se abre directamente el equipo; tras registrarse se entra sin volver al login.
- **Commit:** `01a5c07`

### BUG-013

**🟠 La política de privacidad que se aceptaba no existía**

- **Síntoma:** el registro obligaba a aceptar una política de privacidad que no se podía leer.
- **Impacto:** el consentimiento no era válido según el RGPD y Google Play exige una política pública.
- **Arreglo:** [política de privacidad](privacy.md) enlazada desde el registro, el menú y el README.
- **Commit:** `97c15e6` (IRI-78)

### BUG-014

**🟡 Se pedía la fecha de nacimiento sin usarla**

- **Síntoma:** el registro exigía la fecha de nacimiento (texto libre, sin validar) y la guardaba en Firestore, pero no se usaba en ningún sitio.
- **Impacto:** recoger datos personales innecesarios va contra el principio de minimización del RGPD.
- **Arreglo:** campo eliminado del registro y del perfil.
- **Commit:** `97c15e6` (IRI-78)

### BUG-015

**🟠 Firestore aceptaba cualquier contenido**

- **Síntoma:** tras arreglar BUG-001, cada usuario solo podía escribir en lo suyo, pero sin ninguna validación: saltándose la app se podía guardar un nivel 9999, campos inventados o textos enormes.
- **Arreglo:** las reglas validan campos permitidos, tipos y los mismos límites que el formulario.
- **Commit:** `be263f6` (IRI-81)
- **Test:** `FirestoreRulesEmulatorTest` (escrituras por la API REST del emulador).
- **Pendiente:** publicar las reglas en producción después de fusionar el PR.

### BUG-016

**🟡 Tráfico HTTP sin cifrar permitido en la versión publicada**

- **Síntoma:** el manifest tenía `usesCleartextTraffic="true"` en todas las versiones.
- **Impacto:** la app solo usa HTTPS, pero cualquier conexión HTTP futura viajaría sin cifrar.
- **Arreglo:** HTTP solo en depuración (necesario para los emuladores de Firebase en local).
- **Commit:** `5ea1559` (IRI-83)

### BUG-017

**🟠 Textos invisibles en modo oscuro**

- **Síntoma:** con el móvil en modo oscuro, el nombre del Pokémon, las estadísticas, las notas y el texto escrito en los campos eran casi invisibles.
- **Causa:** 18 fondos blancos fijos (`Color.White`) combinados con textos que tomaban el color claro del tema oscuro. El tema era además la plantilla morada de Android Studio.
- **Arreglo:** tema con los colores de la marca en claro y oscuro (contraste WCAG AA) y colores del tema en lugar de valores fijos.
- **Commit:** `8d3ae83` (IRI-82)
- **Test:** `DarkModeScreenshotsTest` (capturas de todas las pantallas en modo oscuro).

### BUG-018

**🟡 Sin conexión, la app decía «Pokémon no encontrado»**

- **Síntoma:** al autocompletar desde la PokéAPI, cualquier fallo (sin red, servidor caído, tiempo agotado) mostraba «Pokémon no encontrado. Revisa el nombre.». Además, nombres como «Mr. Mime» nunca se encontraban.
- **Causa:** un único mensaje para cualquier excepción y el nombre enviado sin normalizar.
- **Arreglo:** mensajes distintos para 404, sin conexión y error del servidor; tiempos de espera explícitos; normalización del nombre (`Mr. Mime` → `mr-mime`).
- **Commit:** `9d6eaf9` (IRI-84)
- **Test:** `PokeApiSupportTest`, `AddPokemonScreenViewModelTest`.

## Incidencias de desarrollo

Problemas de las herramientas o de la CI, no de la app.

| Fecha | Incidencia | Arreglo | Commit |
| --- | --- | --- | --- |
| 2026-11-20 | La captura de pantalla de Compose (`captureToImage`) no funciona en Robolectric | Captura dibujando la vista en un `Bitmap` | `a3a22a8` |
| 2026-11-20 | Maven Central redirige algunos artefactos a un mirror bloqueado en el entorno de desarrollo | Script de Gradle con el mirror de Google (fuera del repositorio) | — |
| 2026-11-20 | Los tests E2E compartían proceso en Robolectric (Koin ya iniciado, sesión del test anterior) | `FirebaseEmulators.reset()` tras cada test | `199f7e5` |
| 2026-11-20 | Se versionó un log del compilador de Kotlin | `.kotlin/` en `.gitignore` | `b311a42` |
| 2026-11-20 / 25 | GitGuardian marcó como secretos las contraseñas fijas de los tests | Credenciales de prueba generadas en cada ejecución | `ef35ea3`, `e1249f7` |
| 2026-11-23 | Se coló `__pycache__` de un script de la CI | `__pycache__/` en `.gitignore` | `ed9eca9` |
| 2026-11-25 | En Robolectric, el menú desplegable se colocaba fuera de la pantalla y los clics no llegaban | Las opciones del menú se pulsan por su acción de accesibilidad | `7d4872f` |

Los avisos antiguos de GitGuardian (commits `63665de`, `199f7e5` y `7d4872f`) se pueden cerrar como «test credential» en su panel: no eran secretos reales.

## Pendientes conocidos

- **Publicar las reglas de BUG-015** en producción después de fusionar.
- **Datos antiguos:** Pokémon guardados antes de BUG-005 y BUG-006 con número inventado o sin nivel. Se pueden corregir con la opción «Editar».
- **Versión publicada sin probar en un móvil real:** R8 compila sin errores, pero falta abrir el APK de publicación en un dispositivo.
- **Nombre del paquete** `org.ivan.pokmanager` (falta la «e»): no es un bug de funcionamiento, pero no se puede cambiar después de publicar en Google Play.

## Cómo añadir un bug

1. Añade una fila al resumen con el siguiente ID (`BUG-019`, …) y una sección en «Detalle» con síntoma, causa, arreglo, commit y test.
2. Si es posible, añade un test que falle antes del arreglo y pase después.
