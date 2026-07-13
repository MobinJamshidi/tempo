package com.mobinjam.tempo.feature.rooms

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.mobinjam.tempo.core.util.DateUtils
import com.mobinjam.tempo.feature.rooms.domain.Room
import com.mobinjam.tempo.feature.rooms.domain.RoomMember
import com.mobinjam.tempo.feature.rooms.domain.RoomTask
import com.mobinjam.tempo.feature.rooms.presentation.RoomIcon
import com.mobinjam.tempo.feature.social.domain.FriendProfile
import kotlinx.coroutines.delay

private val AccentBlue = Color(0xFF3AC6FF)
private val CardBg = Color(0xFF1A1F2E)
private val LiveGreen = Color(0xFF66BB6A)
private val StopRed = Color(0xFFE57373)

@Composable
fun RoomDetailScreen(
    room: Room,
    members: List<RoomMember>,
    tasks: List<RoomTask>,
    friends: List<FriendProfile>,
    myUserId: String,
    onBack: () -> Unit,
    onAddFriend: (String) -> Unit,
    onLeave: () -> Unit,
    onDelete: () -> Unit,
    onAddTask: (String) -> Unit,
    onAddSubtask: (Long, String) -> Unit,
    onDeleteTask: (Long) -> Unit,
    onToggleTask: (Long, Boolean) -> Unit,
    onToggleSubtask: (Long, Boolean) -> Unit,
) {
    var showAddFriendDialog by remember { mutableStateOf(false) }
    var showAddTaskDialog by remember { mutableStateOf(false) }
    var tick by remember { mutableStateOf(0L) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            tick++
        }
    }

    val isOwner = room.ownerId == myUserId
    val studyingCount = members.count { it.isStudying }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(top = 40.dp, bottom = 110.dp),
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "‹",
                        color = AccentBlue,
                        fontSize = 28.sp,
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable { onBack() }
                            .padding(horizontal = 8.dp),
                    )
                    Spacer(Modifier.width(8.dp))
                    RoomIcon(iconIndex = room.icon, size = 44)
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = room.name,
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = "$studyingCount of ${members.size} studying",
                            color = if (studyingCount > 0) LiveGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp,
                        )
                    }
                    if (isOwner) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(AccentBlue.copy(alpha = 0.15f))
                                .clickable { showAddFriendDialog = true }
                                .padding(horizontal = 12.dp, vertical = 7.dp),
                        ) {
                            Text(
                                text = "+ Add",
                                color = AccentBlue,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    text = "Members",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(Modifier.height(10.dp))
            }

            items(members, key = { "m_${it.profile.id}" }) { member ->
                MemberCard(member = member, tick = tick)
                Spacer(Modifier.height(8.dp))
            }

            item {
                Spacer(Modifier.height(20.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Group tasks",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f),
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(AccentBlue.copy(alpha = 0.15f))
                            .clickable { showAddTaskDialog = true }
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                    ) {
                        Text(
                            text = "+ Task",
                            color = AccentBlue,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
                Spacer(Modifier.height(10.dp))

                if (tasks.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(CardBg)
                            .padding(20.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "No tasks yet. Add one for the group!",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.sp,
                        )
                    }
                }
            }

            items(tasks, key = { "t_${it.id}" }) { task ->
                GroupTaskCard(
                    task = task,
                    myUserId = myUserId,
                    memberCount = members.size,
                    onToggleTask = onToggleTask,
                    onToggleSubtask = onToggleSubtask,
                    onAddSubtask = onAddSubtask,
                    onDeleteTask = onDeleteTask,
                )
                Spacer(Modifier.height(10.dp))
            }

            item {
                Spacer(Modifier.height(20.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(StopRed.copy(alpha = 0.12f))
                        .clickable { if (isOwner) onDelete() else onLeave() }
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = if (isOwner) "Delete room" else "Leave room",
                        color = StopRed,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }

    if (showAddFriendDialog) {
        AddFriendToRoomDialog(
            friends = friends,
            existingMemberIds = members.map { it.profile.id }.toSet(),
            onAdd = { userId ->
                onAddFriend(userId)
                showAddFriendDialog = false
            },
            onDismiss = { showAddFriendDialog = false },
        )
    }

    if (showAddTaskDialog) {
        TextInputDialog(
            title = "New group task",
            placeholder = "Task title",
            onConfirm = { title ->
                onAddTask(title)
                showAddTaskDialog = false
            },
            onDismiss = { showAddTaskDialog = false },
        )
    }
}

@Composable
private fun GroupTaskCard(
    task: RoomTask,
    myUserId: String,
    memberCount: Int,
    onToggleTask: (Long, Boolean) -> Unit,
    onToggleSubtask: (Long, Boolean) -> Unit,
    onAddSubtask: (Long, String) -> Unit,
    onDeleteTask: (Long) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    var showSubtaskDialog by remember { mutableStateOf(false) }

    val iDidIt = task.completedBy.any { it.id == myUserId }
    val arrowRotation by animateFloatAsState(
        targetValue = if (expanded) 90f else 0f,
        animationSpec = tween(250),
        label = "arrow",
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CardBg)
            .padding(14.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(if (iDidIt) AccentBlue else Color(0xFF2A3040))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { onToggleTask(task.id, iDidIt) },
                    ),
                contentAlignment = Alignment.Center,
            ) {
                if (iDidIt) Text("✓", color = Color.White, fontSize = 14.sp)
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    color = if (iDidIt) MaterialTheme.colorScheme.onSurfaceVariant
                    else MaterialTheme.colorScheme.onBackground,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    textDecoration = if (iDidIt) TextDecoration.LineThrough else TextDecoration.None,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "${task.completedBy.size}/$memberCount done",
                    color = if (task.completedBy.isNotEmpty()) LiveGreen
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp,
                )
            }

            Text(
                text = "✕",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 15.sp,
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable { onDeleteTask(task.id) }
                    .padding(5.dp),
            )
        }

        // avatars of who completed it
        if (task.completedBy.isNotEmpty()) {
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier.padding(start = 36.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                task.completedBy.take(5).forEach { p ->
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(LiveGreen.copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = p.username.take(1).uppercase(),
                            color = LiveGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    Spacer(Modifier.width(5.dp))
                }
                if (task.completedBy.size > 5) {
                    Text(
                        text = "+${task.completedBy.size - 5}",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp,
                    )
                }
            }
        }

        // subtasks toggle
        Spacer(Modifier.height(10.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { expanded = !expanded },
                )
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "▸",
                color = AccentBlue,
                fontSize = 13.sp,
                modifier = Modifier.rotate(arrowRotation),
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = if (task.subtasks.isEmpty()) "Subtasks" else "Subtasks (${task.subtasks.size})",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = "+ Add",
                color = AccentBlue,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .clickable { showSubtaskDialog = true }
                    .padding(horizontal = 6.dp, vertical = 3.dp),
            )
        }

        AnimatedVisibility(visible = expanded) {
            Column(modifier = Modifier.padding(start = 8.dp, top = 6.dp)) {
                task.subtasks.forEach { sub ->
                    val iDidSub = sub.completedBy.any { it.id == myUserId }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(if (iDidSub) AccentBlue else Color(0xFF2A3040))
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    onClick = { onToggleSubtask(sub.id, iDidSub) },
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            if (iDidSub) Text("✓", color = Color.White, fontSize = 10.sp)
                        }

                        Spacer(Modifier.width(10.dp))

                        Text(
                            text = sub.title,
                            color = if (iDidSub) MaterialTheme.colorScheme.onSurfaceVariant
                            else MaterialTheme.colorScheme.onBackground,
                            fontSize = 13.sp,
                            textDecoration = if (iDidSub) TextDecoration.LineThrough else TextDecoration.None,
                            modifier = Modifier.weight(1f),
                        )

                        Text(
                            text = "${sub.completedBy.size}",
                            color = if (sub.completedBy.isNotEmpty()) LiveGreen
                            else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }
        }
    }

    if (showSubtaskDialog) {
        TextInputDialog(
            title = "New subtask",
            placeholder = "Subtask title",
            onConfirm = { title ->
                onAddSubtask(task.id, title)
                showSubtaskDialog = false
            },
            onDismiss = { showSubtaskDialog = false },
        )
    }
}

@Composable
private fun MemberCard(member: RoomMember, tick: Long) {
    val currentSeconds = remember(tick, member.startedAt) {
        if (member.isStudying) DateUtils.secondsSince(member.startedAt) else 0L
    }
    val liveTotal = member.todaySecondsBefore + currentSeconds

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CardBg)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (member.isStudying) AccentBlue.copy(alpha = 0.25f)
                        else Color(0xFF2A3040)
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = member.profile.username.take(1).uppercase(),
                    color = if (member.isStudying) AccentBlue else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
            if (member.isStudying) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(LiveGreen)
                        .align(Alignment.BottomEnd),
                )
            }
        }

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = member.profile.username,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = when {
                    member.isStudying && member.category != null -> "Studying ${member.category}"
                    member.isStudying -> "Studying"
                    else -> "Offline"
                },
                color = if (member.isStudying) LiveGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp,
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = formatTime(liveTotal),
                color = if (member.isStudying) AccentBlue else MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "today",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 10.sp,
            )
        }
    }
}

@Composable
private fun TextInputDialog(
    title: String,
    placeholder: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var text by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF16181C))
                .padding(20.dp),
        ) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(CardBg)
                    .padding(horizontal = 14.dp, vertical = 14.dp),
            ) {
                BasicTextField(
                    value = text,
                    onValueChange = { text = it },
                    textStyle = TextStyle(color = Color.White, fontSize = 15.sp),
                    cursorBrush = SolidColor(AccentBlue),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    decorationBox = { inner ->
                        if (text.isEmpty()) {
                            Text(
                                placeholder,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 15.sp,
                            )
                        }
                        inner()
                    },
                )
            }

            Spacer(Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (text.isNotBlank()) AccentBlue else Color(0xFF2A3040))
                    .clickable(enabled = text.isNotBlank()) { onConfirm(text) }
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text("Add", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun AddFriendToRoomDialog(
    friends: List<FriendProfile>,
    existingMemberIds: Set<String>,
    onAdd: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    val available = friends.filter { it.profile.id !in existingMemberIds }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF16181C))
                .padding(20.dp),
        ) {
            Text(
                text = "Add a friend",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(16.dp))

            if (available.isEmpty()) {
                Text(
                    text = "No friends left to add.\nAdd more friends in your profile.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp,
                )
            } else {
                available.forEach { friend ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(CardBg)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = { onAdd(friend.profile.id) },
                            )
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(AccentBlue.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = friend.profile.username.take(1).uppercase(),
                                color = AccentBlue,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text = friend.profile.username,
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 15.sp,
                            modifier = Modifier.weight(1f),
                        )
                        Text(
                            text = "Add",
                            color = AccentBlue,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }
        }
    }
}

private fun formatTime(totalSeconds: Long): String {
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return if (hours > 0) {
        "$hours:${pad(minutes)}:${pad(seconds)}"
    } else {
        "${minutes}:${pad(seconds)}"
    }
}

private fun pad(v: Long): String = if (v < 10) "0$v" else v.toString()