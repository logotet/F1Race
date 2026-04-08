package com.vvasilev.f1race.domain.model

data class DriverPosition(
    val driverId: String,
    val segmentIndex: Int,
    val segmentProgress: Float,
    val lap: Int,
    val speed: Float,
    val timestamp: Long
)
