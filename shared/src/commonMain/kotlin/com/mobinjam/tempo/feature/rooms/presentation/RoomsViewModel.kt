package com.mobinjam.tempo.feature.rooms.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobinjam.tempo.core.util.friendlyErrorMessage
import com.mobinjam.tempo.feature.rooms.domain.Room
import com.mobinjam.tempo.feature.rooms.domain.RoomMember
import com.mobinjam.tempo.feature.rooms.domain.RoomRepository
import com.mobinjam.tempo.feature.rooms.domain.RoomTask
import com.mobinjam.tempo.feature.social.domain.FriendProfile
import com.mobinjam.tempo.feature.social.domain.FriendStatus
import com.mobinjam.tempo.feature.social.domain.ProfileRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class RoomsUiState(
    val rooms: List<Room> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val openRoom: Room? = null,
    val roomMembers: List<RoomMember> = emptyList(),
    val friendsToAdd: List<FriendProfile> = emptyList(),
    val roomTasks: List<RoomTask> = emptyList(),
)

class RoomsViewModel(
    private val roomRepository: RoomRepository,
    private val profileRepository: ProfileRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(RoomsUiState())
    val uiState: StateFlow<RoomsUiState> = _uiState.asStateFlow()

    private var refreshJob: Job? = null

    init {
        loadRooms()
    }

    fun loadRooms() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            roomRepository.getMyRooms().fold(
                onSuccess = { rooms ->
                    _uiState.update { it.copy(rooms = rooms, isLoading = false) }
                },
                onFailure = { e ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = friendlyErrorMessage(e))
                    }
                },
            )
        }
    }

    fun createRoom(name: String, icon: Int) {
        if (name.isBlank()) return
        viewModelScope.launch {
            roomRepository.createRoom(name.trim(), icon).fold(
                onSuccess = { loadRooms() },
                onFailure = { e ->
                    _uiState.update { it.copy(errorMessage = friendlyErrorMessage(e)) }
                },
            )
        }
    }

    fun openRoom(room: Room) {
        _uiState.update { it.copy(openRoom = room) }
        startMemberRefresh(room.id)
        loadFriendsForRoom(room.id)
        loadRoomTasks(room.id)
    }

    fun closeRoom() {
        refreshJob?.cancel()
        refreshJob = null
        _uiState.update {
            it.copy(
                openRoom = null,
                roomMembers = emptyList(),
                friendsToAdd = emptyList(),
                roomTasks = emptyList(),
            )
        }
        loadRooms()
    }

    private fun startMemberRefresh(roomId: Long) {
        refreshJob?.cancel()
        refreshJob = viewModelScope.launch {
            while (isActive) {
                roomRepository.getRoomMembers(roomId).fold(
                    onSuccess = { members ->
                        _uiState.update { it.copy(roomMembers = members) }
                    },
                    onFailure = { },
                )
                delay(5000)
            }
        }
    }

    private fun loadFriendsForRoom(roomId: Long) {
        viewModelScope.launch {
            profileRepository.getFriends().fold(
                onSuccess = { all ->
                    val friends = all.filter { it.status == FriendStatus.FRIENDS }
                    _uiState.update { it.copy(friendsToAdd = friends) }
                },
                onFailure = { },
            )
        }
    }

    fun addFriendToRoom(roomId: Long, userId: String) {
        viewModelScope.launch {
            roomRepository.addMember(roomId, userId).fold(
                onSuccess = {
                    roomRepository.getRoomMembers(roomId).onSuccess { members ->
                        _uiState.update { it.copy(roomMembers = members) }
                    }
                },
                onFailure = { e ->
                    _uiState.update { it.copy(errorMessage = friendlyErrorMessage(e)) }
                },
            )
        }
    }

    fun leaveRoom(roomId: Long, userId: String) {
        viewModelScope.launch {
            roomRepository.removeMember(roomId, userId).fold(
                onSuccess = { closeRoom() },
                onFailure = { e ->
                    _uiState.update { it.copy(errorMessage = friendlyErrorMessage(e)) }
                },
            )
        }
    }

    fun deleteRoom(roomId: Long) {
        viewModelScope.launch {
            roomRepository.deleteRoom(roomId).fold(
                onSuccess = { closeRoom() },
                onFailure = { e ->
                    _uiState.update { it.copy(errorMessage = friendlyErrorMessage(e)) }
                },
            )
        }
    }

    private fun loadRoomTasks(roomId: Long) {
        viewModelScope.launch {
            roomRepository.getRoomTasks(roomId).fold(
                onSuccess = { tasks -> _uiState.update { it.copy(roomTasks = tasks) } },
                onFailure = { },
            )
        }
    }

    fun addTask(roomId: Long, title: String) {
        if (title.isBlank()) return
        viewModelScope.launch {
            roomRepository.addRoomTask(roomId, title.trim()).fold(
                onSuccess = { loadRoomTasks(roomId) },
                onFailure = { e ->
                    _uiState.update { it.copy(errorMessage = friendlyErrorMessage(e)) }
                },
            )
        }
    }

    fun addSubtask(roomId: Long, taskId: Long, title: String) {
        if (title.isBlank()) return
        viewModelScope.launch {
            roomRepository.addRoomSubtask(taskId, title.trim()).fold(
                onSuccess = { loadRoomTasks(roomId) },
                onFailure = { e ->
                    _uiState.update { it.copy(errorMessage = friendlyErrorMessage(e)) }
                },
            )
        }
    }

    fun deleteTask(roomId: Long, taskId: Long) {
        viewModelScope.launch {
            roomRepository.deleteRoomTask(taskId).fold(
                onSuccess = { loadRoomTasks(roomId) },
                onFailure = { },
            )
        }
    }

    fun toggleTask(roomId: Long, taskId: Long, currentlyDone: Boolean) {
        viewModelScope.launch {
            roomRepository.toggleTaskCompletion(taskId, currentlyDone).fold(
                onSuccess = { loadRoomTasks(roomId) },
                onFailure = { },
            )
        }
    }

    fun toggleSubtask(roomId: Long, subtaskId: Long, currentlyDone: Boolean) {
        viewModelScope.launch {
            roomRepository.toggleSubtaskCompletion(subtaskId, currentlyDone).fold(
                onSuccess = { loadRoomTasks(roomId) },
                onFailure = { },
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        refreshJob?.cancel()
    }
}