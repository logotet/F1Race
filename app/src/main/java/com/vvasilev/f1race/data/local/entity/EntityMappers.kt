package com.vvasilev.f1race.data.local.entity

import com.vvasilev.f1race.domain.model.Driver
import com.vvasilev.f1race.domain.model.Team
import com.vvasilev.f1race.domain.model.Track
import com.vvasilev.f1race.domain.model.TrackSegment
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

private val json = Json { ignoreUnknownKeys = true }

fun Driver.toEntity() = DriverEntity(
    id = id,
    name = name,
    abbreviation = abbreviation,
    number = number,
    teamName = team.name
)

fun DriverEntity.toDomain(): Driver = Driver(
    id = id,
    name = name,
    abbreviation = abbreviation,
    number = number,
    team = Team.entries.first { it.name == teamName }
)

fun Track.toEntity() = TrackEntity(
    name = name,
    country = country,
    segmentsJson = json.encodeToString(segments),
    totalLaps = totalLaps
)

fun TrackEntity.toDomain(): Track = Track(
    name = name,
    country = country,
    segments = json.decodeFromString<List<TrackSegment>>(segmentsJson),
    totalLaps = totalLaps
)
