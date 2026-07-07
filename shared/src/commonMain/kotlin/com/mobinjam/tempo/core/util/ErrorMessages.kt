package com.mobinjam.tempo.core.util

fun friendlyErrorMessage(error: Throwable?): String {
    val raw = error?.message?.lowercase() ?: ""

    return when {
        raw.contains("socket") || raw.contains("timeout") ->
            "Connection timed out. Please check your internet."

        raw.contains("unknownhost") || raw.contains("failed to connect") ||
                raw.contains("no address") || raw.contains("unable to resolve host") ->
            "No internet connection. Please try again."

        raw.contains("network") ->
            "Network error. Please check your connection."

        raw.contains("email not confirmed") ->
            "Please confirm your email first."

        raw.contains("invalid login") || raw.contains("invalid credentials") ->
            "Wrong email or password."

        raw.contains("user already registered") || raw.contains("already been registered") ->
            "This email is already registered."

        raw.contains("rate limit") || raw.contains("over_email_send_rate") ->
            "Too many attempts. Please wait a moment and try again."

        raw.contains("password") && raw.contains("weak") ->
            "Password is too weak."

        raw.isBlank() ->
            "Something went wrong. Please try again."

        else ->
            "Something went wrong. Please try again."
    }
}