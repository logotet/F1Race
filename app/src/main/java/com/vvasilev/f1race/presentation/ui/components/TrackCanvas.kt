package com.vvasilev.f1race.presentation.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vvasilev.f1race.domain.model.Driver
import com.vvasilev.f1race.domain.model.DriverPosition
import com.vvasilev.f1race.domain.model.Track
import com.vvasilev.f1race.presentation.ui.theme.DarkSurfaceVariant
import com.vvasilev.f1race.presentation.ui.theme.StartFinishLine
import com.vvasilev.f1race.presentation.ui.theme.TrackCurb
import com.vvasilev.f1race.presentation.ui.theme.TrackEdge
import com.vvasilev.f1race.presentation.ui.theme.TrackSurface
import com.vvasilev.f1race.util.BezierInterpolator
import com.vvasilev.f1race.util.TrackPathBuilder
import kotlinx.coroutines.launch
import kotlin.math.sqrt

@Composable
fun TrackCanvas(
    track: Track,
    positions: Map<String, DriverPosition>,
    drivers: List<Driver>,
    selectedDriverId: String?,
    onDriverTap: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val textMeasurer = rememberTextMeasurer()

    // Pre-compute arc-length tables (stable across recompositions)
    val arcLengthTables = remember(track) {
        track.segments.map { BezierInterpolator.buildArcLengthTable(it) }
    }

    // Animated positions: one Animatable per driver
    val animatedPositions = remember { mutableMapOf<String, Animatable<Offset, *>>() }

    // Update animated positions when position data changes
    LaunchedEffect(positions) {
        positions.forEach { (driverId, pos) ->
            val targetOffset = TrackPathBuilder.positionToOffset(
                track, pos, 1f, 1f, arcLengthTables
            )
            val anim = animatedPositions.getOrPut(driverId) {
                Animatable(targetOffset, Offset.VectorConverter)
            }
            launch {
                anim.animateTo(
                    targetValue = targetOffset,
                    animationSpec = tween(durationMillis = 80, easing = LinearEasing)
                )
            }
        }
    }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(drivers, animatedPositions) {
                detectTapGestures { tapOffset ->
                    // Find the closest driver dot to the tap
                    val tapThreshold = 30.dp.toPx()
                    var closestDriver: String? = null
                    var closestDist = Float.MAX_VALUE

                    animatedPositions.forEach { (driverId, anim) ->
                        val driverPos = Offset(
                            anim.value.x * size.width,
                            anim.value.y * size.height
                        )
                        val dist = sqrt(
                            (tapOffset.x - driverPos.x).let { it * it } +
                                    (tapOffset.y - driverPos.y).let { it * it }
                        )
                        if (dist < tapThreshold && dist < closestDist) {
                            closestDist = dist
                            closestDriver = driverId
                        }
                    }
                    onDriverTap(closestDriver)
                }
            }
    ) {
        val w = size.width
        val h = size.height

        // Draw track
        val trackPath = TrackPathBuilder.buildPath(track.segments, w, h)

        // Outer edge (slightly larger, kerb color)
        drawPath(
            path = trackPath,
            color = TrackCurb,
            style = Stroke(
                width = with(density) { 32.dp.toPx() },
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

        // Track surface (wide stroke)
        drawPath(
            path = trackPath,
            color = TrackSurface,
            style = Stroke(
                width = with(density) { 28.dp.toPx() },
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

        // Track edge highlight (thin white inner stroke)
        drawPath(
            path = trackPath,
            color = TrackEdge.copy(alpha = 0.6f),
            style = Stroke(
                width = with(density) { 26.dp.toPx() },
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

        // Inner dark fill so the path looks like an asphalt strip
        drawPath(
            path = trackPath,
            color = TrackSurface,
            style = Stroke(
                width = with(density) { 24.dp.toPx() },
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

        // Dashed center line
        drawPath(
            path = trackPath,
            color = DarkSurfaceVariant,
            style = Stroke(
                width = with(density) { 1.dp.toPx() },
                pathEffect = PathEffect.dashPathEffect(
                    intervals = floatArrayOf(
                        with(density) { 8.dp.toPx() },
                        with(density) { 6.dp.toPx() }
                    )
                )
            )
        )

        // Start/finish line
        val startPoint = track.segments.firstOrNull()?.start
        if (startPoint != null) {
            val padding = 0.05f
            val sx = startPoint.x * w * (1f - 2f * padding) + w * padding
            val sy = startPoint.y * h * (1f - 2f * padding) + h * padding
            val lineLen = with(density) { 16.dp.toPx() }
            drawLine(
                color = StartFinishLine,
                start = Offset(sx, sy - lineLen),
                end = Offset(sx, sy + lineLen),
                strokeWidth = with(density) { 3.dp.toPx() }
            )
        }

        // Draw driver dots
        drawDriverDots(
            drivers = drivers,
            animatedPositions = animatedPositions,
            positions = positions,
            selectedDriverId = selectedDriverId,
            canvasWidth = w,
            canvasHeight = h,
            density = density,
            textMeasurer = textMeasurer
        )
    }
}

private fun DrawScope.drawDriverDots(
    drivers: List<Driver>,
    animatedPositions: Map<String, Animatable<Offset, *>>,
    positions: Map<String, DriverPosition>,
    selectedDriverId: String?,
    canvasWidth: Float,
    canvasHeight: Float,
    density: androidx.compose.ui.unit.Density,
    textMeasurer: TextMeasurer
) {
    val baseDotRadius = with(density) { 6.dp.toPx() }
    val selectedDotRadius = with(density) { 10.dp.toPx() }

    // Draw non-selected drivers first, then selected on top
    val sortedDrivers = drivers.sortedBy { it.id == selectedDriverId }

    for (driver in sortedDrivers) {
        val anim = animatedPositions[driver.id] ?: continue
        val pos = positions[driver.id]

        val center = Offset(
            anim.value.x * canvasWidth,
            anim.value.y * canvasHeight
        )

        val isSelected = driver.id == selectedDriverId
        val hasSelection = selectedDriverId != null

        // Speed-based dot sizing
        val speedFactor = pos?.speed?.div(0.015f)?.coerceIn(0.8f, 1.3f) ?: 1f
        val radius = if (isSelected) {
            selectedDotRadius
        } else {
            baseDotRadius * speedFactor
        }

        val alpha = when {
            !hasSelection -> 1f
            isSelected -> 1f
            else -> 0.4f
        }

        // Dot outline
        drawCircle(
            color = Color.Black,
            radius = radius + with(density) { 1.5.dp.toPx() },
            center = center,
            alpha = alpha
        )

        // Dot fill with team color
        drawCircle(
            color = driver.team.color,
            radius = radius,
            center = center,
            alpha = alpha
        )

        // Driver abbreviation label for selected driver
        if (isSelected) {
            val textResult = textMeasurer.measure(
                text = driver.abbreviation,
                style = TextStyle(
                    color = Color.White,
                    fontSize = with(density) { 10.sp },
                    fontWeight = FontWeight.Bold
                )
            )
            drawText(
                textLayoutResult = textResult,
                topLeft = Offset(
                    center.x - textResult.size.width / 2f,
                    center.y - radius - textResult.size.height - with(density) { 4.dp.toPx() }
                )
            )
        }
    }
}
