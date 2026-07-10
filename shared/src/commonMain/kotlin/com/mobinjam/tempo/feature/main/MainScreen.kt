package com.mobinjam.tempo.feature.main

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mobinjam.tempo.core.designsystem.icons.ProfileIcon
import com.mobinjam.tempo.core.designsystem.icons.StudyIcon
import com.mobinjam.tempo.core.designsystem.icons.TasksIcon
import com.mobinjam.tempo.feature.profile.ProfileScreen
import com.mobinjam.tempo.feature.study.StudyScreen
import com.mobinjam.tempo.feature.tasks.TasksScreen
import org.koin.compose.viewmodel.koinViewModel

private val GlowBlue = Color(0xFF3AC6FF)

private enum class Tab(val label: String) {
    Tasks("Tasks"),
    Study("Study"),
    Profile("Profile"),
}

@Composable
fun MainScreen(
    studyLauncher: StudyLauncher = koinViewModel(),
    mainViewModel: MainViewModel = koinViewModel(),
) {
    var selectedTab by remember { mutableStateOf(Tab.Tasks) }

    val navigateToStudy by studyLauncher.navigateToStudy.collectAsStateWithLifecycle()

    LaunchedEffect(navigateToStudy) {
        if (navigateToStudy) {
            selectedTab = Tab.Study
            studyLauncher.consumeNavigation()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF05070D),
                        Color(0xFF0B1220),
                        Color(0xFF05070D),
                    ),
                ),
            ),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            when (selectedTab) {
                Tab.Tasks -> TasksScreen()
                Tab.Study -> StudyScreen()
                Tab.Profile -> ProfileScreen()
            }
        }

        GlassNavBar(
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 20.dp),
        )
    }
}

@Composable
private fun GlassNavBar(
    selectedTab: Tab,
    onTabSelected: (Tab) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            // translucent glass pill
            .background(
                color = Color(0xFF1A1F2E).copy(alpha = 0.7f),
                shape = RoundedCornerShape(50),
            )
            // subtle light border to give the glass edge
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.08f),
                shape = RoundedCornerShape(50),
            )
            .padding(horizontal = 8.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Tab.entries.forEach { tab ->
            NavBarItem(
                tab = tab,
                isSelected = tab == selectedTab,
                onClick = { onTabSelected(tab) },
            )
        }
    }
}

@Composable
private fun NavBarItem(
    tab: Tab,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val contentColor = if (isSelected) Color.White else Color(0xFF8A8A90)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            // selected tab gets a brighter glass capsule with a glowing border
            .background(
                brush = if (isSelected) {
                    Brush.verticalGradient(
                        colors = listOf(
                            GlowBlue.copy(alpha = 0.25f),
                            GlowBlue.copy(alpha = 0.08f),
                        ),
                    )
                } else {
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Transparent),
                    )
                },
                shape = RoundedCornerShape(50),
            )
            .then(
                if (isSelected) {
                    Modifier.border(
                        width = 1.dp,
                        color = GlowBlue.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(50),
                    )
                } else Modifier
            )
            .animateContentSize(animationSpec = tween(300))
            .padding(horizontal = 14.dp, vertical = 10.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            when (tab) {
                Tab.Tasks -> TasksIcon(color = contentColor)
                Tab.Study -> StudyIcon(color = contentColor)
                Tab.Profile -> ProfileIcon(color = contentColor)
            }

            if (isSelected) {
                Spacer(Modifier.width(8.dp))
                Text(
                    text = tab.label,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}