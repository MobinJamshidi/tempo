package com.mobinjam.tempo.feature.study.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobinjam.tempo.core.util.DateUtils
import com.mobinjam.tempo.core.util.friendlyErrorMessage
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
) : ViewModel() {

    private val _uiState = MutableStateFlow(StudyUiState())
    val uiState: StateFlow<StudyUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    fun onCategorySelected(category: String?) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun start() {
        if (_uiState.value.status == TimerStatus.RUNNING) return

        _uiState.update { it.copy(status = TimerStatus.RUNNING, errorMessage = null) }
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
            }
        }
    }

    fun stopAndSave() {
        timerJob?.cancel()
        timerJob = null

        val current = _uiState.value
        val seconds = current.elapsedSeconds

        // ignore very short sessions (less than 1 second) and just reset
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
                startedAt = null,
            ).fold(
                onSuccess = {
                    resetTimer()
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