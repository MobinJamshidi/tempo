package com.mobinjam.tempo.feature.tasks.domain

enum class TaskCategory(val label: String, val icon: String) {
    STUDY("Study", "📚"),
    LANGUAGE("Language", "🗣️"),
    SPORT("Sport", "💪"),
    WORK("Work", "💼"),
    CODING("Coding", "💻"),
    READING("Reading", "📖"),
    WRITING("Writing", "✍️"),
    ART("Art", "🎨"),
    MUSIC("Music", "🎵"),
    HEALTH("Health", "🧘"),
    SHOPPING("Shopping", "🛒"),
    HOME("Home", "🏠"),
    PERSONAL("Personal", "🎯"),
    CUSTOM("Custom", "✏️"),
}