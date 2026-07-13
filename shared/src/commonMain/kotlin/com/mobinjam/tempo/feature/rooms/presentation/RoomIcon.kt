package com.mobinjam.tempo.feature.rooms.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

// 8 preset designs: gradient pair + shape
private val presets = listOf(
    Color(0xFF3AC6FF) to Color(0xFF2A7FFF),   // 0 blue
    Color(0xFFFF8A65) to Color(0xFFE53935),   // 1 red
    Color(0xFF81C784) to Color(0xFF388E3C),   // 2 green
    Color(0xFFB388FF) to Color(0xFF7C4DFF),   // 3 purple
    Color(0xFFFFD54F) to Color(0xFFFFA000),   // 4 amber
    Color(0xFF4DD0E1) to Color(0xFF00ACC1),   // 5 cyan
    Color(0xFFF06292) to Color(0xFFC2185B),   // 6 pink
    Color(0xFF90A4AE) to Color(0xFF455A64),   // 7 slate
)

val roomIconCount = presets.size

@Composable
fun RoomIcon(
    iconIndex: Int,
    modifier: Modifier = Modifier,
    size: Int = 56,
) {
    val idx = iconIndex.coerceIn(0, presets.size - 1)
    val (c1, c2) = presets[idx]

    Box(
        modifier = modifier
            .size(size.dp)
            .clip(RoundedCornerShape((size * 0.28f).dp)),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.size(size.dp)) {
            val w = this.size.width
            val h = this.size.height

            drawRect(
                brush = Brush.linearGradient(
                    colors = listOf(c1, c2),
                    start = Offset(0f, 0f),
                    end = Offset(w, h),
                ),
            )

            val cx = w / 2
            val cy = h / 2
            val r = w * 0.24f
            val symbol = Color.White.copy(alpha = 0.92f)

            when (idx % 4) {
                0 -> drawRoomStar(cx, cy, r, symbol)
                1 -> drawRoomBook(cx, cy, r, symbol, w)
                2 -> drawRoomTriangle(cx, cy, r, symbol)
                else -> drawRoomRings(cx, cy, r, symbol, w)
            }
        }
    }
}

private fun DrawScope.drawRoomStar(cx: Float, cy: Float, r: Float, color: Color) {
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

private fun DrawScope.drawRoomBook(cx: Float, cy: Float, r: Float, color: Color, w: Float) {
    val stroke = w * 0.045f
    drawLine(color, Offset(cx, cy - r), Offset(cx, cy + r), strokeWidth = stroke, cap = StrokeCap.Round)
    drawLine(color, Offset(cx - r, cy - r * 0.7f), Offset(cx, cy - r * 0.4f), strokeWidth = stroke, cap = StrokeCap.Round)
    drawLine(color, Offset(cx + r, cy - r * 0.7f), Offset(cx, cy - r * 0.4f), strokeWidth = stroke, cap = StrokeCap.Round)
    drawLine(color, Offset(cx - r, cy + r * 0.7f), Offset(cx, cy + r), strokeWidth = stroke, cap = StrokeCap.Round)
    drawLine(color, Offset(cx + r, cy + r * 0.7f), Offset(cx, cy + r), strokeWidth = stroke, cap = StrokeCap.Round)
    drawLine(color, Offset(cx - r, cy - r * 0.7f), Offset(cx - r, cy + r * 0.7f), strokeWidth = stroke, cap = StrokeCap.Round)
    drawLine(color, Offset(cx + r, cy - r * 0.7f), Offset(cx + r, cy + r * 0.7f), strokeWidth = stroke, cap = StrokeCap.Round)
}

private fun DrawScope.drawRoomTriangle(cx: Float, cy: Float, r: Float, color: Color) {
    val path = Path()
    path.moveTo(cx, cy - r)
    path.lineTo(cx + r * 0.9f, cy + r * 0.7f)
    path.lineTo(cx - r * 0.9f, cy + r * 0.7f)
    path.close()
    drawPath(path, color)
}

private fun DrawScope.drawRoomRings(cx: Float, cy: Float, r: Float, color: Color, w: Float) {
    val stroke = w * 0.05f
    drawCircle(color, radius = r, center = Offset(cx, cy), style = Stroke(stroke))
    drawCircle(color, radius = r * 0.5f, center = Offset(cx, cy), style = Stroke(stroke))
}