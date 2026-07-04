package com.mobinjam.tempo.core.designsystem.icons

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable

// A simple checklist icon for the Tasks tab
@Composable
fun TasksIcon(color: Color, size: Dp = 22.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val stroke = Stroke(width = size.toPx() * 0.09f, cap = StrokeCap.Round)
        val w = this.size.width
        val h = this.size.height

        // checkmark
        drawLine(
            color = color,
            start = Offset(w * 0.20f, h * 0.52f),
            end = Offset(w * 0.42f, h * 0.72f),
            strokeWidth = stroke.width,
            cap = StrokeCap.Round,
        )
        drawLine(
            color = color,
            start = Offset(w * 0.42f, h * 0.72f),
            end = Offset(w * 0.80f, h * 0.28f),
            strokeWidth = stroke.width,
            cap = StrokeCap.Round,
        )
    }
}

// A simple book icon for the Study tab
@Composable
fun StudyIcon(color: Color, size: Dp = 22.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val strokeW = size.toPx() * 0.09f
        val w = this.size.width
        val h = this.size.height

        // book spine (center vertical line)
        drawLine(
            color = color,
            start = Offset(w * 0.5f, h * 0.22f),
            end = Offset(w * 0.5f, h * 0.80f),
            strokeWidth = strokeW,
            cap = StrokeCap.Round,
        )
        // left page (top curve as a line)
        drawLine(
            color = color,
            start = Offset(w * 0.5f, h * 0.22f),
            end = Offset(w * 0.16f, h * 0.30f),
            strokeWidth = strokeW,
            cap = StrokeCap.Round,
        )
        drawLine(
            color = color,
            start = Offset(w * 0.16f, h * 0.30f),
            end = Offset(w * 0.16f, h * 0.74f),
            strokeWidth = strokeW,
            cap = StrokeCap.Round,
        )
        drawLine(
            color = color,
            start = Offset(w * 0.16f, h * 0.74f),
            end = Offset(w * 0.5f, h * 0.80f),
            strokeWidth = strokeW,
            cap = StrokeCap.Round,
        )
        // right page
        drawLine(
            color = color,
            start = Offset(w * 0.5f, h * 0.22f),
            end = Offset(w * 0.84f, h * 0.30f),
            strokeWidth = strokeW,
            cap = StrokeCap.Round,
        )
        drawLine(
            color = color,
            start = Offset(w * 0.84f, h * 0.30f),
            end = Offset(w * 0.84f, h * 0.74f),
            strokeWidth = strokeW,
            cap = StrokeCap.Round,
        )
        drawLine(
            color = color,
            start = Offset(w * 0.84f, h * 0.74f),
            end = Offset(w * 0.5f, h * 0.80f),
            strokeWidth = strokeW,
            cap = StrokeCap.Round,
        )
    }
}

// A simple person icon for the Profile tab
@Composable
fun ProfileIcon(color: Color, size: Dp = 22.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val strokeW = size.toPx() * 0.09f
        val w = this.size.width
        val h = this.size.height

        // head (circle)
        drawCircle(
            color = color,
            radius = w * 0.16f,
            center = Offset(w * 0.5f, h * 0.34f),
            style = Stroke(width = strokeW),
        )
        // shoulders (arc-like using a line path approximation)
        drawArc(
            color = color,
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(w * 0.22f, h * 0.55f),
            size = androidx.compose.ui.geometry.Size(w * 0.56f, h * 0.45f),
            style = Stroke(width = strokeW, cap = StrokeCap.Round),
        )
    }
}