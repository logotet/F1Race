package com.vvasilev.f1race.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class TrackPoint(
    val x: Float,
    val y: Float
)
