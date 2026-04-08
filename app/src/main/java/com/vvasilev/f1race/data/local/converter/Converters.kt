package com.vvasilev.f1race.data.local.converter

import androidx.room.TypeConverter
import com.vvasilev.f1race.domain.model.TrackSegment
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Converters {
    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromSegmentList(segments: List<TrackSegment>): String {
        return json.encodeToString(segments)
    }

    @TypeConverter
    fun toSegmentList(data: String): List<TrackSegment> {
        return json.decodeFromString(data)
    }
}
