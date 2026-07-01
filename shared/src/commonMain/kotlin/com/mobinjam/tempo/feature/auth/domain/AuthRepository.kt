package com.mobinjam.tempo.feature.auth.domain

/**
 * Contract for authentication. The UI/ViewModel depend on this interface,
 * not on Supabase directly — that keeps the domain layer clean and testable.
 */
interface AuthRepository {

    suspend fun signUp(email: String, password: String): Result<Unit>

    suspend fun signIn(email: String, password: String): Result<Unit>

    suspend fun signOut(): Result<Unit>

    fun isLoggedIn(): Boolean
}
