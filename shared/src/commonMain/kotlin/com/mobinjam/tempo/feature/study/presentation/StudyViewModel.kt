package com.mobinjam.tempo.feature.study.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobinjam.tempo.core.util.DateUtils
import com.mobinjam.tempo.core.util.friendlyErrorMessage
import com.mobinjam.tempo.feature.settings.domain.SettingsRepository
import com.mobinjam.tempo.feature.study.domain.StudyRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class StudyViewModel(
    private val studyRepository: StudyRepository,
    private val settingsRepository: SettingsRepository,
    private val notifier: com.mobinjam.tempo.core.notification.Notifier,
) : ViewModel() {

    private val _uiState = MutableStateFlow(StudyUiState())
    val uiState: StateFlow<StudyUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var sessionStartedAt: String? = null

    init {
        loadStats()
    }

    fun loadStats() {
        viewModelScope.launch {
            settingsRepository.getSettings().fold(
                onSuccess = { settings ->
                    _uiState.update { it.copy(dailyGoalMinutes = settings.dailyGoalMinutes) }
                },
                onFailure = { },
            )
            studyRepository.getStats().fold(
                onSuccess = { stats -> _uiState.update { it.copy(stats = stats) } },
                onFailure = { },
            )
            studyRepository.getDailyTotals().fold(
                onSuccess = { totals -> _uiState.update { it.copy(dailyTotals = totals) } },
                onFailure = { },
            )
            studyRepository.getDailyBreakdown().fold(
                onSuccess = { breakdown -> _uiState.update { it.copy(dailyBreakdown = breakdown) } },
                onFailure = { },
            )
            studyRepository.getBestHour().fold(
                onSuccess = { best -> _uiState.update { it.copy(bestHour = best) } },
                onFailure = { },
            )        }
    }

    private fun loadStatsThenCheckCelebration() {
        viewModelScope.launch {
            studyRepository.getStats().fold(
                onSuccess = { stats ->
                    _uiState.update { it.copy(stats = stats) }
                    checkGoalReached(justFinishedSession = true)
                },
                onFailure = { },
            )
            studyRepository.getDailyTotals().fold(
                onSuccess = { totals -> _uiState.update { it.copy(dailyTotals = totals) } },
                onFailure = { },
            )
            studyRepository.getDailyBreakdown().fold(
                onSuccess = { breakdown -> _uiState.update { it.copy(dailyBreakdown = breakdown) } },
                onFailure = { },
            )
        }
    }

    private fun checkGoalReached(justFinishedSession: Boolean = false) {
        val s = _uiState.value
        val goalSeconds = s.dailyGoalMinutes * 60L
        val reachedNow = goalSeconds > 0 && s.stats.todaySeconds >= goalSeconds

        if (reachedNow && justFinishedSession && !s.goalCelebratedToday) {
            _uiState.update { it.copy(goalReached = true, goalCelebratedToday = true) }
            notifier.showGoalReached(
                title = "Goal reached! 🎉",
                message = "You hit your daily study goal. Great work!",
            )
        }
    }

    fun setDailyGoal(minutes: Int) {
        _uiState.update { it.copy(dailyGoalMinutes = minutes) }
        viewModelScope.launch {
            settingsRepository.setDailyGoal(minutes)
        }
    }

    fun dismissCelebration() {
        _uiState.update { it.copy(goalReached = false) }
    }

    fun onHeatmapDaySelected(date: String) {
        _uiState.update {
            it.copy(selectedHeatmapDate = if (it.selectedHeatmapDate == date) null else date)
        }
    }

    fun dismissHeatmapDay() {
        _uiState.update { it.copy(selectedHeatmapDate = null) }
    }

    fun onCategorySelected(category: String?) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun start() {
        if (_uiState.value.status == TimerStatus.RUNNING) return
        _uiState.update { it.copy(status = TimerStatus.RUNNING, errorMessage = null) }
        sessionStartedAt = DateUtils.nowTimestamp()
        startTicking()
    }

    fun startWithCategory(category: String?) {
        if (_uiState.value.status == TimerStatus.RUNNING) return
        _uiState.update {
            it.copy(
                selectedCategory = category,
                status = TimerStatus.RUNNING,
                errorMessage = null,
            )
        }
        sessionStartedAt = DateUtils.nowTimestamp()
        startTicking()
    }

    fun pause() {
        timerJob?.cancel()
        timerJob = null
        _uiState.update { it.copy(status = TimerStatus.PAUSED) }
    }

    fun resume() {
        if (_uiState.value.status == TimerStatus.RUNNING) return
        _uiState.update { it.copy(status = TimerStatus.RUNNING) }
        startTicking()
    }

    private fun startTicking() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                _uiState.update { it.copy(elapsedSeconds = it.elapsedSeconds + 1) }
                checkLiveGoal()
            }
        }
    }

    private fun checkLiveGoal() {
        val s = _uiState.value
        if (s.goalCelebratedToday) return

        val goalSeconds = s.dailyGoalMinutes * 60L
        if (goalSeconds <= 0) return

        val liveTotal = s.stats.todaySeconds + s.elapsedSeconds

        if (liveTotal >= goalSeconds) {
            _uiState.update { it.copy(goalReached = true, goalCelebratedToday = true) }
            notifier.showGoalReached(
                title = "Goal reached! 🎉",
                message = "You hit your daily study goal. Keep going!",
            )
        }
    }

    fun stopAndSave() {
        timerJob?.cancel()
        timerJob = null

        val current = _uiState.value
        val seconds = current.elapsedSeconds

        if (seconds < 1) {
            resetTimer()
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }

            studyRepository.addSession(
                durationSeconds = seconds,
                date = DateUtils.toDbString(DateUtils.today()),
                category = current.selectedCategory,
                startedAt = sessionStartedAt,
            ).fold(
                onSuccess = {
                    resetTimer()
                    loadStatsThenCheckCelebration()
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(isSaving = false, errorMessage = friendlyErrorMessage(error))
                    }
                },
            )
        }
    }

    fun cancelTimer() {
        timerJob?.cancel()
        timerJob = null
        resetTimer()
    }

    private fun resetTimer() {
        sessionStartedAt = null
        _uiState.update {
            it.copy(
                elapsedSeconds = 0,
                status = TimerStatus.IDLE,
                isSaving = false,
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}