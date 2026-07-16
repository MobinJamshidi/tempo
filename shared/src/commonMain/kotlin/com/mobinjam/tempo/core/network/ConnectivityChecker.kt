package com.mobinjam.tempo.core.network

// checks whether the device currently has an active network
expect class ConnectivityChecker {
    fun isConnected(): Boolean
}