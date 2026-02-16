package org.ivan.pokmanager.domain.repository

import com.google.firebase.auth.FirebaseUser
import org.ivan.pokmanager.domain.util.Result

/**
 * Interface for authentication repository.
 * Defines operations for user authentication using Firebase Auth.
 */
interface AuthRepository {
    /**
     * Register a new user with email and password
     * @param email User's email
     * @param password User's password
     * @return Result containing FirebaseUser or error
     */
    suspend fun register(email: String, password: String): Result<FirebaseUser>

    /**
     * Login user with email and password
     * @param email User's email
     * @param password User's password
     * @return Result containing FirebaseUser or error
     */
    suspend fun login(email: String, password: String): Result<FirebaseUser>

    /**
     * Logout current user
     * @return Result indicating success or error
     */
    suspend fun logout(): Result<Unit>

    /**
     * Get current authenticated user
     * @return Current FirebaseUser or null if not authenticated
     */
    fun getCurrentUser(): FirebaseUser?
}
