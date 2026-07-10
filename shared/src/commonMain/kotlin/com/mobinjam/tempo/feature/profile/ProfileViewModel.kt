package com.mobinjam.tempo.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobinjam.tempo.feature.badges.domain.BadgeEvaluator
import com.mobinjam.tempo.feature.settings.domain.SettingsRepository
import com.mobinjam.tempo.feature.study.domain.StudyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileUiState(
    val unlockedBadgeIds: Set<String> = emptySet(),
    val totalHours: Double = 0.0,
    val isLoading: Boolean = true,
)

class ProfileViewModel(
    private val studyRepository: StudyRepository,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadBadges()
    }

    fun loadBadges() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val sessions = studyRepository.getSessions().getOrNull().orEmpty()
            val stats = studyRepository.getStats().getOrNull()
            val settings = settingsRepository.getSettings().getOrNull()

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
                    unlockedBadgeIds = unlocked,
                    totalHours = totalHours,
                    isLoading = false,
                )
            }
        }
    }
}