package com.mobinjam.tempo.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobinjam.tempo.core.util.friendlyErrorMessage
import com.mobinjam.tempo.feature.auth.domain.AuthRepository
import com.mobinjam.tempo.feature.badges.domain.BadgeEvaluator
import com.mobinjam.tempo.feature.settings.domain.SettingsRepository
import com.mobinjam.tempo.feature.social.domain.Profile
import com.mobinjam.tempo.feature.social.domain.ProfileRepository
import com.mobinjam.tempo.feature.study.domain.StudyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileUiState(
    val profile: Profile? = null,
    val unlockedBadgeIds: Set<String> = emptySet(),
    val totalHours: Double = 0.0,
    val streakDays: Int = 0,
    val friendCount: Int = 0,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val successMessage: String? = null,
)

class ProfileViewModel(
    private val studyRepository: StudyRepository,
    private val settingsRepository: SettingsRepository,
    private val authRepository: AuthRepository,
    private val profileRepository: ProfileRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val profile = profileRepository.getMyProfile().getOrNull()
            val sessions = studyRepository.getSessions().getOrNull().orEmpty()
            val stats = studyRepository.getStats().getOrNull()
            val settings = settingsRepository.getSettings().getOrNull()
            val friendCount = profileRepository.getFriendCount().getOrNull() ?: 0

            val streak = stats?.streakDays ?: 0
            val goalMinutes = settings?.dailyGoalMinutes ?: 120

            val unlocked = BadgeEvaluator.evaluate(
                sessions = sessions,
                currentStreak = streak,
                dailyGoalMinutes = goalMinutes,
            )

            val totalHours = sessions.sumOf { it.durationSeconds } / 3600.0

            _uiState.update {
                it.copy(
                    profile = profile,
                    unlockedBadgeIds = unlocked,
                    totalHours = totalHours,
                    streakDays = streak,
                    friendCount = friendCount,
                    isLoading = false,
                )
            }
        }
    }

    fun updateUsername(newUsername: String) {
        if (newUsername.isBlank()) return
        viewModelScope.launch {
            profileRepository.updateUsername(newUsername.trim()).fold(
                onSuccess = {
                    _uiState.update { it.copy(successMessage = "Username updated") }
                    loadProfile()
                },
                onFailure = { e ->
                    _uiState.update { it.copy(errorMessage = friendlyErrorMessage(e)) }
                },
            )
        }
    }

    fun updatePassword(newPassword: String, confirmPassword: String) {
        val error = validatePassword(newPassword, confirmPassword)
        if (error != null) {
            _uiState.update { it.copy(errorMessage = error) }
            return
        }
        viewModelScope.launch {
            profileRepository.updatePassword(newPassword).fold(
                onSuccess = {
                    _uiState.update { it.copy(successMessage = "Password updated", errorMessage = null) }
                },
                onFailure = { e ->
                    _uiState.update { it.copy(errorMessage = friendlyErrorMessage(e)) }
                },
            )
        }
    }

    private fun validatePassword(password: String, confirm: String): String? {
        if (password.length < 8) return "Password must be at least 8 characters"
        if (!password.any { it.isUpperCase() }) return "Password needs an uppercase letter"
        if (!password.any { it.isLowerCase() }) return "Password needs a lowercase letter"
        if (!password.any { it.isDigit() }) return "Password needs a number"
        if (password != confirm) return "Passwords do not match"
        return null
    }

    fun uploadAvatar(bytes: ByteArray) {
        viewModelScope.launch {
            _uiState.update { it.copy(successMessage = null, errorMessage = null) }
            profileRepository.uploadAvatar(bytes).fold(
                onSuccess = {
                    _uiState.update { it.copy(successMessage = "Photo updated") }
                    loadProfile()
                },
                onFailure = { e ->
                    _uiState.update { it.copy(errorMessage = friendlyErrorMessage(e)) }
                },
            )
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }

    fun logout(onDone: () -> Unit) {
        viewModelScope.launch {
            authRepository.signOut()
            onDone()
        }
    }
}