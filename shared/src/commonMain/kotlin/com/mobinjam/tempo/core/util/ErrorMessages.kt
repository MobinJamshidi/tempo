package com.mobinjam.tempo.core.util

fun friendlyErrorMessage(error: Throwable?): String {
    val raw = error?.message?.lowercase() ?: ""

    return when {
        // network
        raw.contains("socket") || raw.contains("timeout") ->
            "Connection timed out. Please check your internet."

        raw.contains("unknownhost") || raw.contains("failed to connect") ||
                raw.contains("no address") || raw.contains("unable to resolve host") ->
            "No internet connection. Please try again."

        raw.contains("network") ->
            "Network error. Please check your connection."

        // auth — login / signup
        raw.contains("email not confirmed") ->
            "Please confirm your email first."

        raw.contains("invalid login") || raw.contains("invalid credentials") ->
            "Wrong email or password."

        raw.contains("user already registered") || raw.contains("already been registered") ->
            "This email is already registered."

        raw.contains("email") && raw.contains("invalid") ->
            "Please enter a valid email address."

        raw.contains("rate limit") || raw.contains("over_email_send_rate") ->
            "Too many attempts. Please wait a moment and try again."

        raw.contains("password") && (raw.contains("weak") || raw.contains("should be at least")) ->
            "Password is too weak. Use at least 8 characters."

        // session expired
        raw.contains("jwt") || raw.contains("token") && raw.contains("expired") ||
                raw.contains("not logged in") || raw.contains("session") ->
            "Your session expired. Please log in again."

        // username / profile
        raw.contains("duplicate") && raw.contains("username") ->
            "This username is already taken."

        raw.contains("profiles_username") ->
            "This username is already taken."

        // friendships
        raw.contains("friendships") && raw.contains("duplicate") ->
            "You already sent a request to this person."

        raw.contains("duplicate") && raw.contains("friend") ->
            "You already sent a request to this person."

        // avatar upload
        raw.contains("payload too large") || raw.contains("entity too large") ||
                raw.contains("exceeded the maximum allowed size") ->
            "Image is too large. Please pick one under 5 MB."

        raw.contains("mime type") || raw.contains("invalid_mime") ->
            "That file type isn't supported. Please pick a JPG or PNG image."

        raw.contains("storage") && raw.contains("not found") ->
            "Upload failed. Please try again."

        // rooms
        raw.contains("room_members") && raw.contains("duplicate") ->
            "This person is already in the room."

        raw.contains("rooms") && raw.contains("not found") ->
            "This room no longer exists."

        // permission / RLS / database
        raw.contains("permission denied") || raw.contains("row-level security") ||
                raw.contains("violates row-level") ->
            "You don't have permission to do that."

        raw.contains("duplicate key") || raw.contains("unique constraint") ->
            "This already exists."

        raw.isBlank() ->
            "Something went wrong. Please try again."

        else ->
            "Something went wrong. Please try again."
    }
}