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

data class ActiveFriendsUiState(
    val activeFriends: List<ActiveFriend> = emptyList(),
    val isLoading: Boolean = true,
)

class ActiveFriendsViewModel(
    private val profileRepository: ProfileRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ActiveFriendsUiState())
    val uiState: StateFlow<ActiveFriendsUiState> = _uiState.asStateFlow()

    init {
        startAutoRefresh()
    }

    private fun startAutoRefresh() {
        viewModelScope.launch {
            while (isActive) {
                loadActiveFriends()
                delay(5000) // refresh every 5 seconds
            }
        }
    }

    private suspend fun loadActiveFriends() {
        profileRepository.getActiveFriends().fold(
            onSuccess = { friends ->
                _uiState.update { it.copy(activeFriends = friends, isLoading = false) }
            },
            onFailure = {
                _uiState.update { it.copy(isLoading = false) }
            },
        )
    }
}