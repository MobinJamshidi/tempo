package com.mobinjam.tempo.feature.auth.presentation

data class SignUpUiState(
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val passwordStrength: PasswordStrength = PasswordStrength.NONE,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSignUpSuccessful: Boolean = false,
)

enum class PasswordStrength {
    NONE,   // خالی
    WEAK,   // ضعیف
    MEDIUM, // متوسط
    STRONG, // قوی
}