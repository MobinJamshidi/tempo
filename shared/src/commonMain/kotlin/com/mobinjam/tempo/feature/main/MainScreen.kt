package com.mobinjam.tempo.feature.main

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mobinjam.tempo.feature.profile.ProfileScreen
import com.mobinjam.tempo.feature.study.StudyScreen
import com.mobinjam.tempo.feature.tasks.TasksScreen

private val GlowBlue = Color(0xFF3AC6FF)

private enum class Tab(val label: String, val icon: String) {
    Tasks("Tasks", "✓"),
    Study("Study", "📚"),
    Profile("Profile", "👤"),
}

@Composable
fun MainScreen() {
    var selectedTab by remember { mutableStateOf(Tab.Tasks) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        // ---- content area (fills the space above the navbar) ----
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            when (selectedTab) {
                Tab.Tasks -> TasksScreen()
                Tab.Study -> StudyScreen()
                Tab.Profile -> ProfileScreen()
            }
        }

        // ---- bottom navbar ----
        BottomNavBar(
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it },
        )
    }
}

@Composable
private fun BottomNavBar(
    selectedTab: Tab,
    onTabSelected: (Tab) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0E0E10))
            .height(72.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
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
    // the glow light animates its width when selected
    val glowWidth by animateDpAsState(
        targetValue = if (isSelected) 28.dp else 0.dp,
        animationSpec = tween(durationMillis = 300),
        label = "glowWidth",
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Text(
            text = tab.icon,
            fontSize = 20.sp,
        )

        Spacer(Modifier.height(4.dp))

        Text(
            text = tab.label,
            color = if (isSelected) GlowBlue else Color(0xFF6A6A70),
            fontSize = 11.sp,
        )

        Spacer(Modifier.height(6.dp))

        // ---- the blue glowing light ----
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.height(10.dp),
        ) {
            if (isSelected) {
                // soft glow halo behind the bar
                Box(
                    modifier = Modifier
                        .width(glowWidth + 16.dp)
                        .height(10.dp)
                        .graphicsLayer { alpha = 0.55f }
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(GlowBlue, Color.Transparent),
                            ),
                            shape = CircleShape,
                        ),
                )
                // the bright core bar
                Box(
                    modifier = Modifier
                        .width(glowWidth)
                        .height(3.dp)
                        .background(GlowBlue, shape = RoundedCornerShape(50)),
                )
            }
        }
    }
}