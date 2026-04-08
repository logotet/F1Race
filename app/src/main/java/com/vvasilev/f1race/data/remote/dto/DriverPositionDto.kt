package com.vvasilev.f1race.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class DriverPositionDto(
    val driverId: String,
    val segmentIndex: Int,
    val segmentProgress: Float,
    val lap: Int,
    val speed: Float,
    val timestamp: Long
)

@Serializable
data class DriverDto(
    val id: String,
    val name: String,
    val abbreviation: String,
    val number: Int,
    val team: String
)

@Serializable
data class TrackDto(
    val name: String,
    val country: String,
    val segments: List<TrackSegmentDto>,
    val totalLaps: Int
)

@Serializable
data class TrackSegmentDto(
    val startX: Float, val startY: Float,
    val cp1X: Float, val cp1Y: Float,
    val cp2X: Float, val cp2Y: Float,
    val endX: Float, val endY: Float
)
