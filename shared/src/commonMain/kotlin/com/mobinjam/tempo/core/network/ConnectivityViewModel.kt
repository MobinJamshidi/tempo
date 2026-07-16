package com.mobinjam.tempo.core.network

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class ConnectivityUiState(
    val isConnected: Boolean = true,
    val showOfflineScreen: Boolean = false,
)

class ConnectivityViewModel(
    private val checker: ConnectivityChecker,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ConnectivityUiState())
    val uiState: StateFlow<ConnectivityUiState> = _uiState.asStateFlow()

    private var offlineSeconds = 0

    init {
        startMonitoring()
    }

    private fun startMonitoring() {
        viewModelScope.launch {
            while (isActive) {
                val connected = checker.isConnected()

                if (connected) {
                    offlineSeconds = 0
                    _uiState.update {
                        it.copy(isConnected = true, showOfflineScreen = false)
                    }
                } else {
                    offlineSeconds += 2
                    _uiState.update {
                        it.copy(
                            isConnected = false,
                            showOfflineScreen = offlineSeconds >= 20,
                        )
                    }
                }

                delay(2000)
            }
        }
    }

    fun retry() {
        offlineSeconds = 0
        val connected = checker.isConnected()
        _uiState.update {
            it.copy(
                isConnected = connected,
                showOfflineScreen = !connected,
            )
        }
    }
}