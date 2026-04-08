package com.vvasilev.f1race.util

import androidx.compose.ui.geometry.Offset

object PositionInterpolator {

    /**
     * Linear interpolation between two offsets.
     */
    fun lerp(start: Offset, end: Offset, fraction: Float): Offset {
        val f = fraction.coerceIn(0f, 1f)
        return Offset(
            x = start.x + (end.x - start.x) * f,
            y = start.y + (end.y - start.y) * f
        )
    }

    /**
     * Smooth interpolation using cubic ease-in-out.
     */
    fun smoothLerp(start: Offset, end: Offset, fraction: Float): Offset {
        val f = fraction.coerceIn(0f, 1f)
        val smooth = if (f < 0.5f) {
            2f * f * f
        } else {
            1f - (-2f * f + 2f).let { it * it } / 2f
        }
        return lerp(start, end, smooth)
    }
}
