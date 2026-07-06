package com.mobinjam.tempo.feature.auth.data

import com.mobinjam.tempo.core.data.remote.SupabaseClientProvider
import com.mobinjam.tempo.feature.auth.domain.AuthRepository
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class SupabaseAuthRepository : AuthRepository {

    private val auth = SupabaseClientProvider.client.auth

    override suspend fun signUp(
        email: String,
        password: String,
        username: String,
    ): Result<Unit> =
        runCatching {
            auth.signUpWith(Email) {
                this.email = email
                this.password = password
                this.data = buildJsonObject {
                    put("username", username)
                }
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

    override suspend fun resetPassword(email: String): Result<Unit> =
        runCatching {
            auth.resetPasswordForEmail(email.trim())
        }

    override fun isLoggedIn(): Boolean =
        auth.currentSessionOrNull() != null

    override suspend fun awaitSessionAndCheckLogin(): Boolean {
        val status = auth.sessionStatus.first { it !is SessionStatus.Initializing }
        return status is SessionStatus.Authenticated
    }
}