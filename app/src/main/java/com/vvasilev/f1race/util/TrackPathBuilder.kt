package com.vvasilev.f1race.util

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import com.vvasilev.f1race.domain.model.DriverPosition
import com.vvasilev.f1race.domain.model.Track
import com.vvasilev.f1race.domain.model.TrackSegment

object TrackPathBuilder {

    /**
     * Build a Compose Path from track segments, scaled to the given canvas size.
     */
    fun buildPath(segments: List<TrackSegment>, width: Float, height: Float): Path {
        if (segments.isEmpty()) return Path()

        // Add padding to keep track away from edges
        val padding = 0.05f
        val scaleX = width * (1f - 2f * padding)
        val scaleY = height * (1f - 2f * padding)
        val offsetX = width * padding
        val offsetY = height * padding

        return Path().apply {
            val first = segments.first()
            moveTo(
                first.start.x * scaleX + offsetX,
                first.start.y * scaleY + offsetY
            )

            for (segment in segments) {
                cubicTo(
                    segment.controlPoint1.x * scaleX + offsetX,
                    segment.controlPoint1.y * scaleY + offsetY,
                    segment.controlPoint2.x * scaleX + offsetX,
                    segment.controlPoint2.y * scaleY + offsetY,
                    segment.end.x * scaleX + offsetX,
                    segment.end.y * scaleY + offsetY
                )
            }
            close()
        }
    }

    /**
     * Convert a DriverPosition (segment index + progress) to a canvas Offset.
     */
    fun positionToOffset(
        track: Track,
        position: DriverPosition,
        width: Float,
        height: Float,
        arcLengthTables: List<FloatArray>
    ): Offset {
        val segments = track.segments
        if (segments.isEmpty()) return Offset.Zero

        val segIndex = position.segmentIndex.coerceIn(0, segments.size - 1)
        val segment = segments[segIndex]
        val table = arcLengthTables[segIndex]

        // Use arc-length parameterization for uniform speed
        val t = BezierInterpolator.arcLengthToT(table, position.segmentProgress)
        val point = BezierInterpolator.evaluate(segment, t)

        val padding = 0.05f
        val scaleX = width * (1f - 2f * padding)
        val scaleY = height * (1f - 2f * padding)
        val offsetX = width * padding
        val offsetY = height * padding

        return Offset(
            x = point.x * scaleX + offsetX,
            y = point.y * scaleY + offsetY
        )
    }
}
