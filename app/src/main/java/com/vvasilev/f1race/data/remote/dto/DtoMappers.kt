package com.vvasilev.f1race.data.remote.dto

import com.vvasilev.f1race.domain.model.Driver
import com.vvasilev.f1race.domain.model.DriverPosition
import com.vvasilev.f1race.domain.model.Team
import com.vvasilev.f1race.domain.model.Track
import com.vvasilev.f1race.domain.model.TrackPoint
import com.vvasilev.f1race.domain.model.TrackSegment

fun DriverPositionDto.toDomain() = DriverPosition(
    driverId = driverId,
    segmentIndex = segmentIndex,
    segmentProgress = segmentProgress,
    lap = lap,
    speed = speed,
    timestamp = timestamp
)

fun DriverDto.toDomain() = Driver(
    id = id,
    name = name,
    abbreviation = abbreviation,
    number = number,
    team = Team.entries.firstOrNull { it.name.equals(team, ignoreCase = true) } ?: Team.RED_BULL
)

fun TrackSegmentDto.toDomain() = TrackSegment(
    start = TrackPoint(startX, startY),
    controlPoint1 = TrackPoint(cp1X, cp1Y),
    controlPoint2 = TrackPoint(cp2X, cp2Y),
    end = TrackPoint(endX, endY)
)

fun TrackDto.toDomain() = Track(
    name = name,
    country = country,
    segments = segments.map { it.toDomain() },
    totalLaps = totalLaps
)
