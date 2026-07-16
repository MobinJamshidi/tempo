package com.mobinjam.tempo.core.data.remote

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage

/**
 * Central place that creates and holds the Supabase client.
 * The publishable key is safe to keep in client code.
 */
object SupabaseClientProvider {

    private const val SUPABASE_URL = "https://sxlambsvvcbhewywrhda.supabase.co"
    private const val SUPABASE_KEY = "sb_publishable_heYUX6dumwH1Y0hiqZQbqw_KuJNKkTm"

    val client: SupabaseClient by lazy {
        createSupabaseClient(
            supabaseUrl = SUPABASE_URL,
            supabaseKey = SUPABASE_KEY,
        ) {
            install(Auth)
            install(Postgrest)
            install(Storage)
        }
    }
}