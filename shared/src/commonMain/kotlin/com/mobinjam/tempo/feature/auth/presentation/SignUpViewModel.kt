package com.mobinjam.tempo.feature.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobinjam.tempo.feature.auth.domain.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SignUpViewModel(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState: StateFlow<SignUpUiState> = _uiState.asStateFlow()

    fun onUsernameChange(value: String) {
        _uiState.update { it.copy(username = value, errorMessage = null) }
    }

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value, errorMessage = null) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update {
            it.copy(
                password = value,
                passwordStrength = calculateStrength(value),
                errorMessage = null,
            )
        }
    }

    fun onConfirmPasswordChange(value: String) {
        _uiState.update { it.copy(confirmPassword = value, errorMessage = null) }
    }

    fun togglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun toggleConfirmPasswordVisibility() {
        _uiState.update { it.copy(isConfirmPasswordVisible = !it.isConfirmPasswordVisible) }
    }

    fun onSignUpClick() {
        val state = _uiState.value

        // ---- validation ----
        if (state.username.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please enter a username") }
            return
        }
        if (state.email.isBlank() || !isEmailValid(state.email)) {
            _uiState.update { it.copy(errorMessage = "Please enter a valid email") }
            return
        }
        if (state.password.length < 6) {
            _uiState.update { it.copy(errorMessage = "Password must be at least 6 characters") }
            return
        }
        if (state.password != state.confirmPassword) {
            _uiState.update { it.copy(errorMessage = "Passwords do not match") }
            return
        }

        // ---- call Supabase ----
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = authRepository.signUp(
                email = state.email.trim(),
                password = state.password,
                username = state.username.trim(),
            )
            result.fold(
                onSuccess = {
                    _uiState.update { it.copy(isLoading = false, isSignUpSuccessful = true) }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Sign up failed",
                        )
                    }
                },
            )
        }
    }

    fun resetSignUpState() {
        _uiState.update { it.copy(isSignUpSuccessful = false) }
    }

    // ---- helpers ----

    private fun isEmailValid(email: String): Boolean {
        // ساده ولی کافی: باید یه @ و یه نقطه بعدش داشته باشه
        return email.contains("@") && email.substringAfter("@").contains(".")
    }

    private fun calculateStrength(password: String): PasswordStrength {
        if (password.isEmpty()) return PasswordStrength.NONE

        var score = 0
        if (password.length >= 6) score++
        if (password.length >= 10) score++
        if (password.any { it.isDigit() }) score++
        if (password.any { it.isLetter() }) score++
        if (password.any { !it.isLetterOrDigit() }) score++ // نماد مثل !@#

        return when {
            score <= 2 -> PasswordStrength.WEAK
            score <= 3 -> PasswordStrength.MEDIUM
            else -> PasswordStrength.STRONG
        }
    }
}