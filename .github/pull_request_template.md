## Qué cambia

<!-- Resume el cambio en una o dos frases y explica por qué hace falta. -->

**Tarea relacionada:** <!-- IRI-XX en Linear o «Closes #123» para cerrar un issue al fusionar -->

## Tipo de cambio

- [ ] 🐞 Corrección de un bug
- [ ] ✨ Funcionalidad nueva
- [ ] ♻️ Refactorización (sin cambio de comportamiento)
- [ ] 📝 Documentación
- [ ] 🔧 CI, build o configuración

## Cómo se ha probado

<!-- Qué tests has añadido o cambiado y qué has comprobado a mano. -->

```bash
./gradlew testDebugUnitTest
```

<!-- Si toca Firebase (Auth, Firestore o las reglas), ejecuta también los E2E con el emulador: -->

```bash
firebase emulators:exec --only auth,firestore --project pokemanager-6113d "./gradlew testDebugUnitTest"
```

## Capturas

<!-- Si cambia la interfaz: antes y después, en modo claro y oscuro. Borra esta sección si no aplica. -->

| Antes | Después |
| :---: | :---: |
|  |  |

## Lista de comprobación

- [ ] Los tests pasan en local y he añadido tests para lo nuevo o lo corregido.
- [ ] Si cambia el modelo de datos, he actualizado `firestore.rules` y `FirestoreRulesEmulatorTest`.
- [ ] Si cambia la interfaz, se ve bien en modo claro y oscuro.
- [ ] Si es un bug, lo he añadido al [historial de bugs](https://github.com/irilopa/PokeManager/blob/master/docs/bugs.md) (`docs/bugs.md`).
- [ ] Si cambia el README, he editado solo `README.md`: la versión en inglés la genera el workflow «README translate».
- [ ] No hay contraseñas, claves ni datos personales en el código, los tests ni las capturas.
