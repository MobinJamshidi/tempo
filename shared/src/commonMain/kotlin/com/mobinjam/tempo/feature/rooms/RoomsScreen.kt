package com.mobinjam.tempo.feature.rooms

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mobinjam.tempo.feature.rooms.domain.Room
import com.mobinjam.tempo.feature.rooms.presentation.RoomIcon
import com.mobinjam.tempo.feature.rooms.presentation.RoomsViewModel
import com.mobinjam.tempo.feature.rooms.presentation.roomIconCount
import org.koin.compose.viewmodel.koinViewModel

private val AccentBlue = Color(0xFF3AC6FF)
private val CardBg = Color(0xFF1A1F2E)
private val StopRed = Color(0xFFE57373)

@Composable
fun RoomsScreen(
    onBack: () -> Unit,
    myUserId: String,
    viewModel: RoomsViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showCreateDialog by remember { mutableStateOf(false) }

    val open = state.openRoom
    if (open != null) {
        RoomDetailScreen(
            room = open,
            members = state.roomMembers,
            tasks = state.roomTasks,
            friends = state.friendsToAdd,
            myUserId = myUserId,
            onBack = viewModel::closeRoom,
            onAddFriend = { userId -> viewModel.addFriendToRoom(open.id, userId) },
            onLeave = { viewModel.leaveRoom(open.id, myUserId) },
            onDelete = { viewModel.deleteRoom(open.id) },
            onAddTask = { title -> viewModel.addTask(open.id, title) },
            onAddSubtask = { taskId, title -> viewModel.addSubtask(open.id, taskId, title) },
            onDeleteTask = { taskId -> viewModel.deleteTask(open.id, taskId) },
            onToggleTask = { taskId, done -> viewModel.toggleTask(open.id, taskId, done) },
            onToggleSubtask = { subId, done -> viewModel.toggleSubtask(open.id, subId, done) },
        )
        return
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
        ) {
            Spacer(Modifier.height(40.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "‹",
                    color = AccentBlue,
                    fontSize = 28.sp,
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable { onBack() }
                        .padding(horizontal = 10.dp),
                )
                Spacer(Modifier.size(6.dp))
                Text(
                    text = "Study rooms",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                )
            }

            Spacer(Modifier.height(16.dp))

            if (state.errorMessage != null) {
                Text(
                    text = state.errorMessage!!,
                    color = StopRed,
                    fontSize = 13.sp,
                )
                Spacer(Modifier.height(12.dp))
            }

            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(150.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = AccentBlue)
                }
            } else if (state.rooms.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxWidth().height(240.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    RoomIcon(iconIndex = 0, size = 64)
                    Spacer(Modifier.height(14.dp))
                    Text(
                        text = "No rooms yet",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Create a room and study with friends",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp,
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 110.dp),
                ) {
                    items(state.rooms, key = { it.id }) { room ->
                        RoomGridCard(room = room, onClick = { viewModel.openRoom(room) })
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .padding(bottom = 80.dp)
                .size(56.dp)
                .clip(CircleShape)
                .background(AccentBlue)
                .clickable { showCreateDialog = true },
            contentAlignment = Alignment.Center,
        ) {
            Text("+", color = Color.White, fontSize = 28.sp)
        }
    }

    if (showCreateDialog) {
        CreateRoomDialog(
            onConfirm = { name, icon ->
                viewModel.createRoom(name, icon)
                showCreateDialog = false
            },
            onDismiss = { showCreateDialog = false },
        )
    }
}

@Composable
private fun RoomGridCard(room: Room, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(20.dp))
            .background(CardBg)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        RoomIcon(iconIndex = room.icon, size = 56)

        Spacer(Modifier.height(12.dp))

        Text(
            text = room.name,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            maxLines = 2,
        )

        Spacer(Modifier.height(4.dp))

        Text(
            text = "${room.memberCount} members",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 11.sp,
        )
    }
}

@Composable
private fun CreateRoomDialog(
    onConfirm: (String, Int) -> Unit,
    onDismiss: () -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var selectedIcon by remember { mutableStateOf(0) }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(Color(0xFF16181C))
                .padding(22.dp),
        ) {
            Text(
                text = "New room",
                color = Color.White,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
            )

            Spacer(Modifier.height(18.dp))

            Text(
                text = "Pick a style",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp,
            )
            Spacer(Modifier.height(10.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.height(120.dp),
            ) {
                items((0 until roomIconCount).toList()) { idx ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { selectedIcon = idx }
                            .padding(3.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Box(
                            modifier = if (selectedIcon == idx) {
                                Modifier
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(AccentBlue.copy(alpha = 0.25f))
                                    .padding(3.dp)
                            } else Modifier,
                        ) {
                            RoomIcon(iconIndex = idx, size = 48)
                        }
                    }
                }
            }

            Spacer(Modifier.height(18.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(CardBg)
                    .padding(horizontal = 14.dp, vertical = 14.dp),
            ) {
                BasicTextField(
                    value = name,
                    onValueChange = { name = it },
                    textStyle = TextStyle(color = Color.White, fontSize = 15.sp),
                    cursorBrush = SolidColor(AccentBlue),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    decorationBox = { inner ->
                        if (name.isEmpty()) {
                            Text(
                                "Room name",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 15.sp,
                            )
                        }
                        inner()
                    },
                )
            }

            Spacer(Modifier.height(18.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (name.isNotBlank()) AccentBlue else Color(0xFF2A3040))
                    .clickable(enabled = name.isNotBlank()) { onConfirm(name, selectedIcon) }
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text("Create", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}