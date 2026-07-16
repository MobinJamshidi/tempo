package com.mobinjam.tempo.feature.profile

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

enum class ProfileIconType { TROPHY, FRIENDS, EDIT, LOCK, SUPPORT, EMAIL, GITHUB, TELEGRAM, INSTAGRAM }

@Composable
fun ProfileIcon(
    type: ProfileIconType,
    color: Color,
    modifier: Modifier = Modifier,
    size: Int = 18,
) {
    Canvas(modifier = modifier.size(size.dp)) {
        val w = this.size.width
        val cx = w / 2
        val cy = w / 2
        val s = w * 0.09f

        when (type) {
            ProfileIconType.TROPHY -> drawTrophy(cx, cy, w, color, s)
            ProfileIconType.FRIENDS -> drawFriends(cx, cy, w, color, s)
            ProfileIconType.EDIT -> drawPencil(cx, cy, w, color, s)
            ProfileIconType.LOCK -> drawLock(cx, cy, w, color, s)
            ProfileIconType.SUPPORT -> drawChat(cx, cy, w, color, s)
            ProfileIconType.EMAIL -> drawEnvelope(cx, cy, w, color, s)
            ProfileIconType.GITHUB -> drawCode(cx, cy, w, color, s)
            ProfileIconType.TELEGRAM -> drawPaperPlane(cx, cy, w, color)
            ProfileIconType.INSTAGRAM -> drawCamera(cx, cy, w, color, s)
        }
    }
}

private fun DrawScope.drawTrophy(cx: Float, cy: Float, w: Float, c: Color, s: Float) {
    val r = w * 0.22f
    val path = Path()
    path.moveTo(cx - r, cy - r * 1.1f)
    path.lineTo(cx + r, cy - r * 1.1f)
    path.lineTo(cx + r * 0.75f, cy + r * 0.2f)
    path.lineTo(cx - r * 0.75f, cy + r * 0.2f)
    path.close()
    drawPath(path, c)
    drawLine(c, Offset(cx, cy + r * 0.2f), Offset(cx, cy + r * 0.9f), strokeWidth = s, cap = StrokeCap.Round)
    drawLine(c, Offset(cx - r * 0.7f, cy + r * 1.05f), Offset(cx + r * 0.7f, cy + r * 1.05f), strokeWidth = s, cap = StrokeCap.Round)
    drawArc(c, 90f, 180f, false, Offset(cx - r * 1.6f, cy - r * 0.9f), androidx.compose.ui.geometry.Size(r * 0.9f, r * 0.9f), style = Stroke(s * 0.8f))
    drawArc(c, 270f, 180f, false, Offset(cx + r * 0.7f, cy - r * 0.9f), androidx.compose.ui.geometry.Size(r * 0.9f, r * 0.9f), style = Stroke(s * 0.8f))
}

private fun DrawScope.drawFriends(cx: Float, cy: Float, w: Float, c: Color, s: Float) {
    val r = w * 0.13f
    drawCircle(c, r * 0.85f, Offset(cx - r * 0.9f, cy - r * 0.5f))
    drawCircle(c, r * 0.7f, Offset(cx + r * 1.1f, cy - r * 0.3f))
    drawArc(c, 200f, 140f, false, Offset(cx - r * 2.2f, cy + r * 0.1f), androidx.compose.ui.geometry.Size(r * 2.6f, r * 2.2f), style = Stroke(s))
    drawArc(c, 200f, 140f, false, Offset(cx + r * 0.1f, cy + r * 0.3f), androidx.compose.ui.geometry.Size(r * 2f, r * 1.8f), style = Stroke(s))
}

private fun DrawScope.drawPencil(cx: Float, cy: Float, w: Float, c: Color, s: Float) {
    val r = w * 0.26f
    drawLine(c, Offset(cx - r * 0.7f, cy + r * 0.7f), Offset(cx + r * 0.7f, cy - r * 0.7f), strokeWidth = s * 1.6f, cap = StrokeCap.Round)
    val tip = Path()
    tip.moveTo(cx - r * 0.95f, cy + r * 0.95f)
    tip.lineTo(cx - r * 0.5f, cy + r * 0.85f)
    tip.lineTo(cx - r * 0.85f, cy + r * 0.5f)
    tip.close()
    drawPath(tip, c)
}

private fun DrawScope.drawLock(cx: Float, cy: Float, w: Float, c: Color, s: Float) {
    val r = w * 0.2f
    drawRoundRect(
        color = c,
        topLeft = Offset(cx - r, cy - r * 0.1f),
        size = androidx.compose.ui.geometry.Size(r * 2, r * 1.5f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(s),
    )
    drawArc(c, 180f, 180f, false, Offset(cx - r * 0.6f, cy - r * 1.1f), androidx.compose.ui.geometry.Size(r * 1.2f, r * 1.2f), style = Stroke(s))
}

private fun DrawScope.drawChat(cx: Float, cy: Float, w: Float, c: Color, s: Float) {
    val r = w * 0.24f
    drawRoundRect(
        color = c,
        topLeft = Offset(cx - r, cy - r * 0.85f),
        size = androidx.compose.ui.geometry.Size(r * 2, r * 1.5f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(s * 1.5f),
        style = Stroke(s),
    )
    val tail = Path()
    tail.moveTo(cx - r * 0.35f, cy + r * 0.6f)
    tail.lineTo(cx - r * 0.15f, cy + r * 1.1f)
    tail.lineTo(cx + r * 0.15f, cy + r * 0.6f)
    tail.close()
    drawPath(tail, c)
}

private fun DrawScope.drawEnvelope(cx: Float, cy: Float, w: Float, c: Color, s: Float) {
    val r = w * 0.26f
    drawRoundRect(
        color = c,
        topLeft = Offset(cx - r, cy - r * 0.7f),
        size = androidx.compose.ui.geometry.Size(r * 2, r * 1.4f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(s),
        style = Stroke(s),
    )
    drawLine(c, Offset(cx - r, cy - r * 0.7f), Offset(cx, cy + r * 0.1f), strokeWidth = s, cap = StrokeCap.Round)
    drawLine(c, Offset(cx + r, cy - r * 0.7f), Offset(cx, cy + r * 0.1f), strokeWidth = s, cap = StrokeCap.Round)
}

private fun DrawScope.drawCode(cx: Float, cy: Float, w: Float, c: Color, s: Float) {
    val r = w * 0.26f
    drawLine(c, Offset(cx - r * 0.3f, cy - r * 0.7f), Offset(cx - r, cy), strokeWidth = s, cap = StrokeCap.Round)
    drawLine(c, Offset(cx - r, cy), Offset(cx - r * 0.3f, cy + r * 0.7f), strokeWidth = s, cap = StrokeCap.Round)
    drawLine(c, Offset(cx + r * 0.3f, cy - r * 0.7f), Offset(cx + r, cy), strokeWidth = s, cap = StrokeCap.Round)
    drawLine(c, Offset(cx + r, cy), Offset(cx + r * 0.3f, cy + r * 0.7f), strokeWidth = s, cap = StrokeCap.Round)
}

private fun DrawScope.drawPaperPlane(cx: Float, cy: Float, w: Float, c: Color) {
    val r = w * 0.28f
    val path = Path()
    path.moveTo(cx + r, cy - r * 0.7f)
    path.lineTo(cx - r, cy + r * 0.1f)
    path.lineTo(cx - r * 0.2f, cy + r * 0.3f)
    path.lineTo(cx + r, cy - r * 0.7f)
    path.close()
    drawPath(path, c)
    val tail = Path()
    tail.moveTo(cx - r * 0.2f, cy + r * 0.3f)
    tail.lineTo(cx + r * 0.1f, cy + r * 0.8f)
    tail.lineTo(cx + r * 0.35f, cy + r * 0.05f)
    tail.close()
    drawPath(tail, c)
}

private fun DrawScope.drawCamera(cx: Float, cy: Float, w: Float, c: Color, s: Float) {
    val r = w * 0.26f
    drawRoundRect(
        color = c,
        topLeft = Offset(cx - r, cy - r),
        size = androidx.compose.ui.geometry.Size(r * 2, r * 2),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(s * 2f),
        style = Stroke(s),
    )
    drawCircle(c, r * 0.42f, Offset(cx, cy), style = Stroke(s))
    drawCircle(c, s * 0.6f, Offset(cx + r * 0.55f, cy - r * 0.55f))
}