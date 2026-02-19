# 📋 Casos de Uso - Gestión de Usuarios

Basados en el modelo `User`, el repositorio `UserFirestoreRepository` y el caso de uso existente `SaveUserUseCase`.

---

## ✅ Ya implementado

| Caso de uso | Descripción |
|---|---|
| `SaveUserUseCase` | Guardar/registrar un usuario en Firestore |

---

## 🔐 Autenticación

| Caso de uso | Descripción |
|---|---|
| `LoginUseCase` | Autenticar usuario con email y contraseña |
| `LogoutUseCase` | Cerrar sesión del usuario actual |
| `RegisterUserUseCase` | Registrar un nuevo usuario con validaciones |

---

## 📦 CRUD

| Caso de uso | Descripción |
|---|---|
| `GetUserByIdUseCase` | Obtener un usuario por su ID |
| `GetAllUsersUseCase` | Listar todos los usuarios |
| `UpdateUserUseCase` | Actualizar datos del usuario (nombre, apellido, etc.) |
| `DeleteUserUseCase` | Eliminar un usuario por su ID |

---

## ✅ Validaciones / Reglas de Negocio

| Caso de uso | Descripción |
|---|---|
| `ValidateUserAgeUseCase` | Validar que el usuario tenga una edad mínima (usando `birthDay`) |
| `ValidateEmailUseCase` | Validar formato de email antes de registrar |
| `ChangePasswordUseCase` | Cambiar la contraseña del usuario |
| `CheckEmailExistsUseCase` | Comprobar si un email ya está registrado en Firestore |

---

## 👤 Perfil

| Caso de uso | Descripción |
|---|---|
| `GetCurrentUserUseCase` | Obtener el usuario actualmente autenticado |
| `UpdateProfileUseCase` | Actualizar solo los datos del perfil (sin tocar contraseña) |

---

## ⚠️ Nota de Seguridad

> El campo `password` en el modelo `User` almacenado en Firestore **no es una práctica segura**.
> Se recomienda delegar la autenticación completamente a **Firebase Authentication**
> y **no almacenar contraseñas** en Firestore.

---

## 🏗️ Estructura sugerida

```
domain/
├── model/
│   └── User.kt
├── repository/
│   └── UserFirestoreRepository.kt
└── usecase/
    ├── SaveUserUseCase.kt          ✅ Implementado
    ├── LoginUseCase.kt
    ├── LogoutUseCase.kt
    ├── RegisterUserUseCase.kt
    ├── GetUserByIdUseCase.kt
    ├── GetAllUsersUseCase.kt
    ├── UpdateUserUseCase.kt
    ├── DeleteUserUseCase.kt
    ├── ValidateUserAgeUseCase.kt
    ├── ValidateEmailUseCase.kt
    ├── ChangePasswordUseCase.kt
    ├── CheckEmailExistsUseCase.kt
    ├── GetCurrentUserUseCase.kt
    └── UpdateProfileUseCase.kt
```

