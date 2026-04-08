package com.vvasilev.f1race.util

import com.vvasilev.f1race.domain.model.TrackPoint
import com.vvasilev.f1race.domain.model.TrackSegment
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BezierInterpolatorTest {

    private val segment = TrackSegment(
        start = TrackPoint(0f, 0f),
        controlPoint1 = TrackPoint(0.33f, 1f),
        controlPoint2 = TrackPoint(0.67f, 1f),
        end = TrackPoint(1f, 0f)
    )

    @Test
    fun `evaluate at t=0 returns start point`() {
        val result = BezierInterpolator.evaluate(segment, 0f)
        assertEquals(0f, result.x, 0.0001f)
        assertEquals(0f, result.y, 0.0001f)
    }

    @Test
    fun `evaluate at t=1 returns end point`() {
        val result = BezierInterpolator.evaluate(segment, 1f)
        assertEquals(1f, result.x, 0.0001f)
        assertEquals(0f, result.y, 0.0001f)
    }

    @Test
    fun `evaluate at t=0_5 returns midpoint of symmetric curve`() {
        val result = BezierInterpolator.evaluate(segment, 0.5f)
        // For our symmetric curve, x at t=0.5 should be 0.5
        assertEquals(0.5f, result.x, 0.0001f)
        // y should be at the peak of the bell curve (~0.75 for these control points)
        assertTrue("y should be > 0", result.y > 0f)
    }

    @Test
    fun `arc length table is normalized to 0 and 1`() {
        val table = BezierInterpolator.buildArcLengthTable(segment)
        // First entry should be 0
        assertEquals(0f, table[0], 0.0001f)
        // Last arcLength entry should be 1.0
        val lastIdx = table.size - 2
        assertEquals(1f, table[lastIdx], 0.0001f)
    }

    @Test
    fun `arcLengthToT returns 0 for fraction 0`() {
        val table = BezierInterpolator.buildArcLengthTable(segment)
        val t = BezierInterpolator.arcLengthToT(table, 0f)
        assertEquals(0f, t, 0.0001f)
    }

    @Test
    fun `arcLengthToT returns 1 for fraction 1`() {
        val table = BezierInterpolator.buildArcLengthTable(segment)
        val t = BezierInterpolator.arcLengthToT(table, 1f)
        assertEquals(1f, t, 0.0001f)
    }

    @Test
    fun `segment length is positive for non-degenerate curve`() {
        val length = BezierInterpolator.segmentLength(segment)
        assertTrue("length should be > 0", length > 0f)
    }
}
