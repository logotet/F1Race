package com.vvasilev.f1race.domain.model

data class Track(
    val name: String,
    val country: String,
    val segments: List<TrackSegment>,
    val totalLaps: Int = 53
)
