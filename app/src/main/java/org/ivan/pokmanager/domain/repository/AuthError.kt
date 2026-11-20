package org.ivan.pokmanager.domain.repository

/**
 * Error de autenticacion con un mensaje listo para mostrar al usuario.
 */
class AuthError(message: String, cause: Throwable? = null) : Exception(message, cause)
