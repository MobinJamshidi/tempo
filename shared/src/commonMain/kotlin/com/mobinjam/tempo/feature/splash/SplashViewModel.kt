package com.mobinjam.tempo.feature.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobinjam.tempo.feature.auth.domain.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class SplashDestination { LOADING, LOGIN, MAIN }

class SplashViewModel(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _destination = MutableStateFlow(SplashDestination.LOADING)
    val destination: StateFlow<SplashDestination> = _destination.asStateFlow()

    init {
        checkSession()
    }

    private fun checkSession() {
        viewModelScope.launch {
            val isLoggedIn = authRepository.awaitSessionAndCheckLogin()
            _destination.value =
                if (isLoggedIn) SplashDestination.MAIN
                else SplashDestination.LOGIN
        }
    }
}