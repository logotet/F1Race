package com.vvasilev.f1race.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class TrackSegment(
    val start: TrackPoint,
    val controlPoint1: TrackPoint,
    val controlPoint2: TrackPoint,
    val end: TrackPoint
)
