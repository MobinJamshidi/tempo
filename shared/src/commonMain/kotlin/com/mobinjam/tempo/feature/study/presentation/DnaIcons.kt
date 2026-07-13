package com.mobinjam.tempo.feature.study.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

private val AccentBlue = Color(0xFF3AC6FF)

// picks an icon based on the trait label
@Composable
fun DnaTraitIcon(label: String, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(22.dp)) {
        val w = size.width
        val cx = w / 2
        val cy = w / 2
        val stroke = w * 0.09f

        when {
            label.contains("rhythm", true) -> {
                // clock icon
                drawCircle(AccentBlue, radius = w * 0.4f, center = Offset(cx, cy), style = Stroke(stroke))
                drawLine(AccentBlue, Offset(cx, cy), Offset(cx, cy - w * 0.22f), strokeWidth = stroke, cap = StrokeCap.Round)
                drawLine(AccentBlue, Offset(cx, cy), Offset(cx + w * 0.16f, cy), strokeWidth = stroke, cap = StrokeCap.Round)
            }
            label.contains("style", true) -> {
                // waves icon
                for (i in 0..2) {
                    val y = cy - w * 0.18f + i * w * 0.18f
                    val path = Path()
                    path.moveTo(cx - w * 0.32f, y)
                    path.cubicTo(cx - w * 0.1f, y - w * 0.14f, cx + w * 0.1f, y + w * 0.14f, cx + w * 0.32f, y)
                    drawPath(path, AccentBlue, style = Stroke(stroke, cap = StrokeCap.Round))
                }
            }
            label.contains("focus", true) -> {
                // target icon
                drawCircle(AccentBlue, radius = w * 0.4f, center = Offset(cx, cy), style = Stroke(stroke))
                drawCircle(AccentBlue, radius = w * 0.2f, center = Offset(cx, cy), style = Stroke(stroke))
                drawCircle(AccentBlue, radius = w * 0.05f, center = Offset(cx, cy))
            }
            label.contains("consistency", true) -> {
                // bars icon (like a chart)
                val heights = listOf(0.3f, 0.55f, 0.8f)
                heights.forEachIndexed { i, h ->
                    val x = cx - w * 0.25f + i * w * 0.25f
                    drawLine(
                        AccentBlue,
                        Offset(x, cy + w * 0.3f),
                        Offset(x, cy + w * 0.3f - w * h),
                        strokeWidth = stroke * 1.4f,
                        cap = StrokeCap.Round,
                    )
                }
            }
            else -> {
                drawCircle(AccentBlue, radius = w * 0.3f, center = Offset(cx, cy))
            }
        }
    }
}