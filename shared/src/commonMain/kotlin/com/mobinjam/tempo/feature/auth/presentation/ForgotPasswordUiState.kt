package com.mobinjam.tempo.feature.auth.presentation

data class ForgotPasswordUiState(
    val email: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isEmailSent: Boolean = false,
)