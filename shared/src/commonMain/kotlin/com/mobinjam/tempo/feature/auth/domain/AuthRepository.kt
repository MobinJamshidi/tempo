package com.mobinjam.tempo.feature.auth.domain

interface AuthRepository {

    suspend fun signUp(email: String, password: String, username: String): Result<Unit>

    suspend fun signIn(email: String, password: String): Result<Unit>

    suspend fun signOut(): Result<Unit>

    suspend fun resetPassword(email: String): Result<Unit>

    fun isLoggedIn(): Boolean

    suspend fun awaitSessionAndCheckLogin(): Boolean
}