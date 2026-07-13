package com.mobinjam.tempo.feature.social

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mobinjam.tempo.feature.social.domain.FriendProfile
import com.mobinjam.tempo.feature.social.domain.Profile
import com.mobinjam.tempo.feature.social.presentation.FriendsViewModel
import org.koin.compose.viewmodel.koinViewModel

private val AccentBlue = Color(0xFF3AC6FF)
private val CardBg = Color(0xFF1A1F2E)
private val StopRed = Color(0xFFE57373)

@Composable
fun FriendsScreen(
    onBack: () -> Unit = {},
    viewModel: FriendsViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(top = 40.dp, bottom = 100.dp),
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
                Text(
                    text = "Friends",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
            Spacer(Modifier.height(16.dp))

            // search bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(CardBg)
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("🔍", fontSize = 15.sp)
                Spacer(Modifier.size(8.dp))
                BasicTextField(
                    value = state.searchQuery,
                    onValueChange = viewModel::onSearchQueryChange,
                    textStyle = TextStyle(color = Color.White, fontSize = 14.sp),
                    cursorBrush = SolidColor(AccentBlue),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    decorationBox = { inner ->
                        if (state.searchQuery.isEmpty()) {
                            Text(
                                "Search by username",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 14.sp,
                            )
                        }
                        inner()
                    },
                )
            }

            Spacer(Modifier.height(16.dp))
        }

        // search results
        if (state.searchResults.isNotEmpty()) {
            item {
                SectionLabel("Search results")
                Spacer(Modifier.height(8.dp))
            }
            items(state.searchResults, key = { "search_${it.id}" }) { profile ->
                SearchResultRow(
                    profile = profile,
                    onAdd = { viewModel.sendRequest(profile) },
                )
                Spacer(Modifier.height(8.dp))
            }
            item { Spacer(Modifier.height(12.dp)) }
        }

        // incoming requests
        if (state.pendingReceived.isNotEmpty()) {
            item {
                SectionLabel("Friend requests")
                Spacer(Modifier.height(8.dp))
            }
            items(state.pendingReceived, key = { "recv_${it.friendshipId}" }) { fp ->
                RequestRow(
                    friend = fp,
                    onAccept = { fp.friendshipId?.let { viewModel.acceptRequest(it) } },
                    onReject = { fp.friendshipId?.let { viewModel.removeFriend(it) } },
                )
                Spacer(Modifier.height(8.dp))
            }
            item { Spacer(Modifier.height(12.dp)) }
        }

        // friends list
        item {
            SectionLabel("Your friends (${state.friends.size})")
            Spacer(Modifier.height(8.dp))
        }

        if (state.isLoading) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = AccentBlue)
                }
            }
        } else if (state.friends.isEmpty()) {
            item {
                Text(
                    text = "No friends yet. Search above to add some!",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp,
                )
            }
        } else {
            items(state.friends, key = { "friend_${it.friendshipId}" }) { fp ->
                FriendRow(
                    friend = fp,
                    onRemove = { fp.friendshipId?.let { viewModel.removeFriend(it) } },
                )
                Spacer(Modifier.height(8.dp))
            }
        }

        // pending sent (optional, at bottom)
        if (state.pendingSent.isNotEmpty()) {
            item {
                Spacer(Modifier.height(12.dp))
                SectionLabel("Sent requests")
                Spacer(Modifier.height(8.dp))
            }
            items(state.pendingSent, key = { "sent_${it.friendshipId}" }) { fp ->
                SentRow(friend = fp)
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
    )
}

@Composable
private fun Avatar(username: String) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(AccentBlue.copy(alpha = 0.2f)),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = username.take(1).uppercase(),
            color = AccentBlue,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun SearchResultRow(profile: Profile, onAdd: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CardBg)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Avatar(profile.username)
        Spacer(Modifier.size(12.dp))
        Text(
            text = profile.username,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f),
        )
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(AccentBlue)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onAdd,
                )
                .padding(horizontal = 14.dp, vertical = 8.dp),
        ) {
            Text("Add", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun RequestRow(friend: FriendProfile, onAccept: () -> Unit, onReject: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CardBg)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Avatar(friend.profile.username)
        Spacer(Modifier.size(12.dp))
        Text(
            text = friend.profile.username,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f),
        )
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(AccentBlue)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onAccept,
                )
                .padding(horizontal = 12.dp, vertical = 8.dp),
        ) {
            Text("Accept", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        }
        Spacer(Modifier.size(8.dp))
        Text(
            text = "✕",
            color = StopRed,
            fontSize = 16.sp,
            modifier = Modifier
                .clip(CircleShape)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onReject,
                )
                .padding(6.dp),
        )
    }
}

@Composable
private fun FriendRow(friend: FriendProfile, onRemove: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CardBg)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Avatar(friend.profile.username)
        Spacer(Modifier.size(12.dp))
        Text(
            text = friend.profile.username,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = "Remove",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onRemove,
                )
                .padding(horizontal = 10.dp, vertical = 6.dp),
        )
    }
}

@Composable
private fun SentRow(friend: FriendProfile) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CardBg.copy(alpha = 0.5f))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Avatar(friend.profile.username)
        Spacer(Modifier.size(12.dp))
        Text(
            text = friend.profile.username,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = "Pending",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp,
        )
    }
}