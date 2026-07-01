package com.mobinjam.tempo.feature.auth.data

import com.mobinjam.tempo.core.data.remote.SupabaseClientProvider
import com.mobinjam.tempo.feature.auth.domain.AuthRepository
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email

/**
 * Supabase-backed implementation of [AuthRepository].
 * runCatching wraps calls so failures come back as Result.failure
 * instead of crashing.
 */
class SupabaseAuthRepository : AuthRepository {

    private val auth = SupabaseClientProvider.client.auth

    override suspend fun signUp(email: String, password: String): Result<Unit> =
        runCatching {
            auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }
            Unit
        }

    override suspend fun signIn(email: String, password: String): Result<Unit> =
        runCatching {
            auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
        }

    override suspend fun signOut(): Result<Unit> =
        runCatching { auth.signOut() }

    override fun isLoggedIn(): Boolean =
        auth.currentSessionOrNull() != null
}
