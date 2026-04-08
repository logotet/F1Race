package com.vvasilev.f1race.util

import com.vvasilev.f1race.domain.model.TrackPoint
import com.vvasilev.f1race.domain.model.TrackSegment
import kotlin.math.sqrt

object BezierInterpolator {

    private const val ARC_LENGTH_SUBDIVISIONS = 50

    /**
     * Evaluate a cubic Bezier curve at parameter t (0..1).
     * Returns the (x, y) point on the curve.
     */
    fun evaluate(segment: TrackSegment, t: Float): TrackPoint {
        val u = 1f - t
        val u2 = u * u
        val u3 = u2 * u
        val t2 = t * t
        val t3 = t2 * t

        val x = u3 * segment.start.x +
                3f * u2 * t * segment.controlPoint1.x +
                3f * u * t2 * segment.controlPoint2.x +
                t3 * segment.end.x

        val y = u3 * segment.start.y +
                3f * u2 * t * segment.controlPoint1.y +
                3f * u * t2 * segment.controlPoint2.y +
                t3 * segment.end.y

        return TrackPoint(x, y)
    }

    /**
     * Compute the derivative of a cubic Bezier at parameter t.
     */
    fun derivative(segment: TrackSegment, t: Float): TrackPoint {
        val u = 1f - t
        val u2 = u * u
        val t2 = t * t

        val dx = 3f * u2 * (segment.controlPoint1.x - segment.start.x) +
                6f * u * t * (segment.controlPoint2.x - segment.controlPoint1.x) +
                3f * t2 * (segment.end.x - segment.controlPoint2.x)

        val dy = 3f * u2 * (segment.controlPoint1.y - segment.start.y) +
                6f * u * t * (segment.controlPoint2.y - segment.controlPoint1.y) +
                3f * t2 * (segment.end.y - segment.controlPoint2.y)

        return TrackPoint(dx, dy)
    }

    /**
     * Build an arc-length lookup table for a segment.
     * Returns an array of (arcLength, t) pairs, normalized so that
     * the last entry's arcLength is 1.0.
     */
    fun buildArcLengthTable(segment: TrackSegment): FloatArray {
        val n = ARC_LENGTH_SUBDIVISIONS
        // Store pairs as [arcLen0, t0, arcLen1, t1, ...]
        val table = FloatArray((n + 1) * 2)
        var totalLength = 0f
        var prevPoint = evaluate(segment, 0f)

        table[0] = 0f  // arcLength
        table[1] = 0f  // t

        for (i in 1..n) {
            val t = i.toFloat() / n
            val point = evaluate(segment, t)
            val dx = point.x - prevPoint.x
            val dy = point.y - prevPoint.y
            totalLength += sqrt(dx * dx + dy * dy)
            table[i * 2] = totalLength
            table[i * 2 + 1] = t
            prevPoint = point
        }

        // Normalize arc lengths to 0..1
        if (totalLength > 0f) {
            for (i in 0..n) {
                table[i * 2] /= totalLength
            }
        }

        return table
    }

    /**
     * Given a normalized arc-length fraction (0..1), find the corresponding
     * Bezier parameter t using a pre-built lookup table.
     * This enables uniform-speed movement along the curve.
     */
    fun arcLengthToT(table: FloatArray, arcFraction: Float): Float {
        val clamped = arcFraction.coerceIn(0f, 1f)
        val n = table.size / 2 - 1

        // Binary search for the surrounding entries
        var low = 0
        var high = n
        while (low < high - 1) {
            val mid = (low + high) / 2
            if (table[mid * 2] < clamped) low = mid else high = mid
        }

        val arcLow = table[low * 2]
        val arcHigh = table[high * 2]
        val tLow = table[low * 2 + 1]
        val tHigh = table[high * 2 + 1]

        return if ((arcHigh - arcLow) < 1e-6f) {
            tLow
        } else {
            val frac = (clamped - arcLow) / (arcHigh - arcLow)
            tLow + frac * (tHigh - tLow)
        }
    }

    /**
     * Compute the approximate total arc length of a segment in normalized coordinates.
     */
    fun segmentLength(segment: TrackSegment): Float {
        val n = ARC_LENGTH_SUBDIVISIONS
        var totalLength = 0f
        var prevPoint = evaluate(segment, 0f)

        for (i in 1..n) {
            val t = i.toFloat() / n
            val point = evaluate(segment, t)
            val dx = point.x - prevPoint.x
            val dy = point.y - prevPoint.y
            totalLength += sqrt(dx * dx + dy * dy)
            prevPoint = point
        }

        return totalLength
    }
}
