package com.mobinjam.tempo.feature.social.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobinjam.tempo.feature.social.domain.ActiveFriend
import com.mobinjam.tempo.feature.social.domain.ProfileRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class GlobalStudyUiState(
    val activeUsers: List<ActiveFriend> = emptyList(),
    val isLoading: Boolean = true,
)

class GlobalStudyViewModel(
    private val profileRepository: ProfileRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(GlobalStudyUiState())
    val uiState: StateFlow<GlobalStudyUiState> = _uiState.asStateFlow()

    init {
        startAutoRefresh()
    }

    private fun startAutoRefresh() {
        viewModelScope.launch {
            while (isActive) {
                profileRepository.getGlobalActive().fold(
                    onSuccess = { users ->
                        _uiState.update { it.copy(activeUsers = users, isLoading = false) }
                    },
                    onFailure = {
                        _uiState.update { it.copy(isLoading = false) }
                    },
                )
                delay(5000)
            }
        }
    }
}