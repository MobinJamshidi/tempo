package com.mobinjam.tempo.core.notification

expect class Notifier {
    fun showGoalReached(title: String, message: String)
}