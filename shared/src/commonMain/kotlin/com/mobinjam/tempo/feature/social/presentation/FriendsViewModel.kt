package com.mobinjam.tempo.feature.social.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobinjam.tempo.core.util.friendlyErrorMessage
import com.mobinjam.tempo.feature.social.domain.FriendProfile
import com.mobinjam.tempo.feature.social.domain.Profile
import com.mobinjam.tempo.feature.social.domain.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FriendsUiState(
    val searchQuery: String = "",
    val searchResults: List<Profile> = emptyList(),
    val isSearching: Boolean = false,
    val friends: List<FriendProfile> = emptyList(),
    val pendingReceived: List<FriendProfile> = emptyList(),
    val pendingSent: List<FriendProfile> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
)

class FriendsViewModel(
    private val profileRepository: ProfileRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(FriendsUiState())
    val uiState: StateFlow<FriendsUiState> = _uiState.asStateFlow()

    init {
        loadFriends()
    }

    fun loadFriends() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            profileRepository.getFriends().fold(
                onSuccess = { all ->
                    _uiState.update {
                        it.copy(
                            friends = all.filter { f -> f.status == com.mobinjam.tempo.feature.social.domain.FriendStatus.FRIENDS },
                            pendingReceived = all.filter { f -> f.status == com.mobinjam.tempo.feature.social.domain.FriendStatus.PENDING_RECEIVED },
                            pendingSent = all.filter { f -> f.status == com.mobinjam.tempo.feature.social.domain.FriendStatus.PENDING_SENT },
                            isLoading = false,
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = friendlyErrorMessage(error)) }
                },
            )
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        if (query.isBlank()) {
            _uiState.update { it.copy(searchResults = emptyList()) }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isSearching = true) }
            profileRepository.searchProfiles(query).fold(
                onSuccess = { results ->
                    _uiState.update { it.copy(searchResults = results, isSearching = false) }
                },
                onFailure = {
                    _uiState.update { it.copy(isSearching = false) }
                },
            )
        }
    }

    fun sendRequest(profile: Profile) {
        viewModelScope.launch {
            profileRepository.sendFriendRequest(profile.id).fold(
                onSuccess = {
                    _uiState.update { it.copy(searchQuery = "", searchResults = emptyList()) }
                    loadFriends()
                },
                onFailure = { error ->
                    _uiState.update { it.copy(errorMessage = friendlyErrorMessage(error)) }
                },
            )
        }
    }

    fun acceptRequest(friendshipId: Long) {
        viewModelScope.launch {
            profileRepository.acceptFriendRequest(friendshipId).fold(
                onSuccess = { loadFriends() },
                onFailure = { error ->
                    _uiState.update { it.copy(errorMessage = friendlyErrorMessage(error)) }
                },
            )
        }
    }

    fun removeFriend(friendshipId: Long) {
        viewModelScope.launch {
            profileRepository.removeFriendship(friendshipId).fold(
                onSuccess = { loadFriends() },
                onFailure = { error ->
                    _uiState.update { it.copy(errorMessage = friendlyErrorMessage(error)) }
                },
            )
        }
    }
}