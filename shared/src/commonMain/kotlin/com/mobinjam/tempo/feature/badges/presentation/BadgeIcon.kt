package com.mobinjam.tempo.feature.badges.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.mobinjam.tempo.feature.badges.domain.BadgeCategory
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

// color pair (start, end) for each category gradient
private fun categoryColors(category: BadgeCategory): Pair<Color, Color> =
    when (category) {
        BadgeCategory.START -> Color(0xFF3AC6FF) to Color(0xFF2A7FFF)
        BadgeCategory.HOURS -> Color(0xFF4FC3F7) to Color(0xFF2196F3)
        BadgeCategory.STREAK -> Color(0xFFFF8A65) to Color(0xFFE53935)
        BadgeCategory.TIME_OF_DAY -> Color(0xFFB388FF) to Color(0xFF7C4DFF)
        BadgeCategory.GOAL -> Color(0xFF81C784) to Color(0xFF388E3C)
        BadgeCategory.DAILY -> Color(0xFFFFD54F) to Color(0xFFFFA000)
        BadgeCategory.VARIETY -> Color(0xFF4DD0E1) to Color(0xFF00ACC1)
    }

@Composable
fun BadgeIcon(
    category: BadgeCategory,
    unlocked: Boolean,
    modifier: Modifier = Modifier,
    size: Int = 64,
) {
    val (c1, c2) = if (unlocked) categoryColors(category)
    else Color(0xFF2A3040) to Color(0xFF1A1F2E)

    Box(
        modifier = modifier.size(size.dp),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.size(size.dp)) {
            val w = this.size.width
            val h = this.size.height
            val cx = w / 2
            val cy = h / 2
            val radius = w * 0.42f

            // outer medal circle with gradient
            drawCircle(
                brush = Brush.linearGradient(
                    colors = listOf(c1, c2),
                    start = Offset(0f, 0f),
                    end = Offset(w, h),
                ),
                radius = radius,
                center = Offset(cx, cy),
            )

            // inner ring
            drawCircle(
                color = Color.White.copy(alpha = if (unlocked) 0.25f else 0.08f),
                radius = radius * 0.78f,
                center = Offset(cx, cy),
                style = Stroke(width = w * 0.03f),
            )

            // draw a category-specific symbol in the center
            val symbolColor = Color.White.copy(alpha = if (unlocked) 0.95f else 0.3f)
            when (category) {
                BadgeCategory.START -> drawStar(cx, cy, radius * 0.45f, symbolColor)
                BadgeCategory.HOURS -> drawClock(cx, cy, radius * 0.5f, symbolColor, w)
                BadgeCategory.STREAK -> drawFlame(cx, cy, radius * 0.55f, symbolColor)
                BadgeCategory.TIME_OF_DAY -> drawMoon(cx, cy, radius * 0.5f, symbolColor)
                BadgeCategory.GOAL -> drawTarget(cx, cy, radius * 0.5f, symbolColor, w)
                BadgeCategory.DAILY -> drawStar(cx, cy, radius * 0.45f, symbolColor)
                BadgeCategory.VARIETY -> drawDiamond(cx, cy, radius * 0.5f, symbolColor)
            }
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawStar(
    cx: Float, cy: Float, r: Float, color: Color,
) {
    val path = Path()
    val points = 5
    for (i in 0 until points * 2) {
        val angle = (i * PI / points - PI / 2).toFloat()
        val rad = if (i % 2 == 0) r else r * 0.45f
        val x = cx + rad * cos(angle)
        val y = cy + rad * sin(angle)
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    drawPath(path, color)
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawClock(
    cx: Float, cy: Float, r: Float, color: Color, w: Float,
) {
    drawCircle(color, radius = r, center = Offset(cx, cy), style = Stroke(width = w * 0.035f))
    drawLine(color, Offset(cx, cy), Offset(cx, cy - r * 0.6f), strokeWidth = w * 0.035f, cap = StrokeCap.Round)
    drawLine(color, Offset(cx, cy), Offset(cx + r * 0.45f, cy), strokeWidth = w * 0.035f, cap = StrokeCap.Round)
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawFlame(
    cx: Float, cy: Float, r: Float, color: Color,
) {
    val path = Path()
    path.moveTo(cx, cy - r)
    path.cubicTo(cx + r * 0.9f, cy - r * 0.2f, cx + r * 0.5f, cy + r, cx, cy + r)
    path.cubicTo(cx - r * 0.5f, cy + r, cx - r * 0.9f, cy - r * 0.2f, cx, cy - r)
    path.close()
    drawPath(path, color)
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawMoon(
    cx: Float, cy: Float, r: Float, color: Color,
) {
    drawCircle(color, radius = r, center = Offset(cx, cy))
    drawCircle(
        color = Color(0xFF1A1F2E),
        radius = r * 0.85f,
        center = Offset(cx + r * 0.35f, cy - r * 0.25f),
    )
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawTarget(
    cx: Float, cy: Float, r: Float, color: Color, w: Float,
) {
    drawCircle(color, radius = r, center = Offset(cx, cy), style = Stroke(width = w * 0.035f))
    drawCircle(color, radius = r * 0.55f, center = Offset(cx, cy), style = Stroke(width = w * 0.035f))
    drawCircle(color, radius = r * 0.15f, center = Offset(cx, cy))
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawDiamond(
    cx: Float, cy: Float, r: Float, color: Color,
) {
    val path = Path()
    path.moveTo(cx, cy - r)
    path.lineTo(cx + r * 0.7f, cy)
    path.lineTo(cx, cy + r)
    path.lineTo(cx - r * 0.7f, cy)
    path.close()
    drawPath(path, color)
}