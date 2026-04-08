package com.vvasilev.f1race.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tracks")
data class TrackEntity(
    @PrimaryKey val name: String,
    val country: String,
    val segmentsJson: String,
    val totalLaps: Int
)
