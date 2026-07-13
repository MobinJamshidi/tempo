package com.mobinjam.tempo.feature.profile

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mobinjam.tempo.feature.badges.domain.allBadges
import org.koin.compose.viewmodel.koinViewModel

private val AccentBlue = Color(0xFF3AC6FF)
private val CardBg = Color(0xFF1A1F2E)
private val LogoutRed = Color(0xFFE57373)
private val OkGreen = Color(0xFF66BB6A)

private enum class ProfilePage { Main, Achievements, Friends, Support }

@Composable
fun ProfileScreen(
    onLogout: () -> Unit = {},
    viewModel: ProfileViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var page by remember { mutableStateOf(ProfilePage.Main) }
    var showEditUsername by remember { mutableStateOf(false) }
    var showChangePassword by remember { mutableStateOf(false) }

    val imagePicker = com.mobinjam.tempo.core.image.rememberImagePicker { bytes ->
        viewModel.uploadAvatar(bytes)
    }

    when (page) {
        ProfilePage.Friends -> {
            com.mobinjam.tempo.feature.social.FriendsScreen(
                onBack = { page = ProfilePage.Main },
            )
            return
        }
        ProfilePage.Achievements -> {
            AchievementsPage(
                unlockedIds = state.unlockedBadgeIds,
                onBack = { page = ProfilePage.Main },
            )
            return
        }
        ProfilePage.Support -> {
            SupportPage(onBack = { page = ProfilePage.Main })
            return
        }
        ProfilePage.Main -> Unit
    }

    if (state.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator(color = AccentBlue)
        }
        return
    }

    val username = state.profile?.username ?: "user"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
    ) {
        Spacer(Modifier.height(40.dp))

        Text(
            text = "Profile",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
        )

        Spacer(Modifier.height(24.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(contentAlignment = Alignment.BottomEnd) {
                com.mobinjam.tempo.core.designsystem.UserAvatar(
                    username = username,
                    avatarUrl = state.profile?.avatarUrl,
                    size = 96,
                )
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(AccentBlue)
                        .clickable { imagePicker.launch() },
                    contentAlignment = Alignment.Center,
                ) {
                    ProfileIcon(type = ProfileIconType.EDIT, color = Color.White, size = 14)
                }
            }

            Spacer(Modifier.height(14.dp))

            Text(
                text = username,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "@$username",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp,
            )
        }

        Spacer(Modifier.height(24.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(CardBg)
                .padding(vertical = 18.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            StatItem(value = formatHours(state.totalHours), label = "Studied")
            StatItem(value = "${state.streakDays}", label = "Streak")
            StatItem(value = "${state.friendCount}", label = "Friends")
        }

        Spacer(Modifier.height(22.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(CardBg),
        ) {
            MenuRow(
                iconType = ProfileIconType.TROPHY,
                title = "Achievements",
                subtitle = "${state.unlockedBadgeIds.size} of ${allBadges.size} unlocked",
                onClick = { page = ProfilePage.Achievements },
            )
            ThinDivider()
            MenuRow(
                iconType = ProfileIconType.FRIENDS,
                title = "Friends",
                subtitle = "${state.friendCount} friends",
                onClick = { page = ProfilePage.Friends },
            )
            ThinDivider()
            MenuRow(
                iconType = ProfileIconType.EDIT,
                title = "Edit username",
                subtitle = null,
                onClick = {
                    viewModel.clearMessages()
                    showEditUsername = true
                },
            )
            ThinDivider()
            MenuRow(
                iconType = ProfileIconType.LOCK,
                title = "Change password",
                subtitle = null,
                onClick = {
                    viewModel.clearMessages()
                    showChangePassword = true
                },
            )
            ThinDivider()
            MenuRow(
                iconType = ProfileIconType.SUPPORT,
                title = "Support",
                subtitle = null,
                onClick = { page = ProfilePage.Support },
            )
        }

        if (state.errorMessage != null) {
            Spacer(Modifier.height(12.dp))
            Text(text = state.errorMessage!!, color = LogoutRed, fontSize = 13.sp)
        }
        if (state.successMessage != null) {
            Spacer(Modifier.height(12.dp))
            Text(text = state.successMessage!!, color = OkGreen, fontSize = 13.sp)
        }

        Spacer(Modifier.height(22.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(LogoutRed.copy(alpha = 0.12f))
                .clickable { viewModel.logout(onLogout) }
                .padding(vertical = 15.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "Logout",
                color = LogoutRed,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }

        Spacer(Modifier.height(110.dp))
    }

    if (showEditUsername) {
        UsernameDialog(
            initial = username,
            onConfirm = {
                viewModel.updateUsername(it)
                showEditUsername = false
            },
            onDismiss = { showEditUsername = false },
        )
    }

    if (showChangePassword) {
        PasswordDialog(
            onConfirm = { pass, confirm ->
                viewModel.updatePassword(pass, confirm)
                showChangePassword = false
            },
            onDismiss = { showChangePassword = false },
        )
    }
}

@Composable
private fun StatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            color = AccentBlue,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(3.dp))
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 11.sp,
        )
    }
}

@Composable
private fun MenuRow(
    iconType: ProfileIconType,
    title: String,
    subtitle: String?,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .padding(horizontal = 16.dp, vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(11.dp))
                .background(AccentBlue.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center,
        ) {
            ProfileIcon(type = iconType, color = AccentBlue, size = 18)
        }

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp,
                )
            }
        }

        Text(
            text = "›",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 18.sp,
        )
    }
}

@Composable
internal fun ThinDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Color.White.copy(alpha = 0.05f)),
    )
}

@Composable
private fun UsernameDialog(
    initial: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var text by remember { mutableStateOf(initial) }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF16181C))
                .padding(20.dp),
        ) {
            Text(
                text = "Edit username",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(16.dp))

            DialogField(
                value = text,
                onValueChange = { text = it },
                placeholder = "New username",
                isPassword = false,
            )

            Spacer(Modifier.height(16.dp))

            DialogButton(
                text = "Save",
                enabled = text.isNotBlank(),
                onClick = { onConfirm(text) },
            )
        }
    }
}

@Composable
private fun PasswordDialog(
    onConfirm: (String, String) -> Unit,
    onDismiss: () -> Unit,
) {
    var pass by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }

    val hasLength = pass.length >= 8
    val hasUpper = pass.any { it.isUpperCase() }
    val hasLower = pass.any { it.isLowerCase() }
    val hasDigit = pass.any { it.isDigit() }
    val matches = pass.isNotEmpty() && pass == confirm
    val allOk = hasLength && hasUpper && hasLower && hasDigit && matches

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF16181C))
                .padding(20.dp),
        ) {
            Text(
                text = "Change password",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(16.dp))

            DialogField(
                value = pass,
                onValueChange = { pass = it },
                placeholder = "New password",
                isPassword = true,
            )

            Spacer(Modifier.height(10.dp))

            DialogField(
                value = confirm,
                onValueChange = { confirm = it },
                placeholder = "Confirm password",
                isPassword = true,
            )

            Spacer(Modifier.height(14.dp))

            RuleRow("At least 8 characters", hasLength)
            RuleRow("One uppercase letter", hasUpper)
            RuleRow("One lowercase letter", hasLower)
            RuleRow("One number", hasDigit)
            RuleRow("Passwords match", matches)

            Spacer(Modifier.height(16.dp))

            DialogButton(
                text = "Save",
                enabled = allOk,
                onClick = { onConfirm(pass, confirm) },
            )
        }
    }
}

@Composable
private fun RuleRow(text: String, ok: Boolean) {
    Row(
        modifier = Modifier.padding(vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(14.dp)
                .clip(CircleShape)
                .background(if (ok) OkGreen else Color(0xFF2A3040)),
            contentAlignment = Alignment.Center,
        ) {
            if (ok) Text("✓", color = Color.White, fontSize = 8.sp)
        }
        Spacer(Modifier.width(8.dp))
        Text(
            text = text,
            color = if (ok) OkGreen else MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 11.sp,
        )
    }
}

@Composable
private fun DialogField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isPassword: Boolean,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CardBg)
            .padding(horizontal = 14.dp, vertical = 14.dp),
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = TextStyle(color = Color.White, fontSize = 15.sp),
            cursorBrush = SolidColor(AccentBlue),
            singleLine = true,
            visualTransformation = if (isPassword) PasswordVisualTransformation()
            else VisualTransformation.None,
            modifier = Modifier.fillMaxWidth(),
            decorationBox = { inner ->
                if (value.isEmpty()) {
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
}

@Composable
private fun DialogButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (enabled) AccentBlue else Color(0xFF2A3040))
            .clickable(enabled = enabled) { onClick() }
            .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
    }
}

private fun formatHours(hours: Double): String {
    return if (hours >= 1) "${hours.toInt()}h" else "${(hours * 60).toInt()}m"
}