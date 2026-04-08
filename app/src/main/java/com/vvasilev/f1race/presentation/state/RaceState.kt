package com.vvasilev.f1race.presentation.state

import com.vvasilev.f1race.domain.model.Driver
import com.vvasilev.f1race.domain.model.DriverPosition
import com.vvasilev.f1race.domain.model.RaceStatus
import com.vvasilev.f1race.domain.model.Track

data class RaceState(
    val isLoading: Boolean = true,
    val track: Track? = null,
    val drivers: List<Driver> = emptyList(),
    val positions: Map<String, DriverPosition> = emptyMap(),
    val standings: List<String> = emptyList(),
    val selectedDriverId: String? = null,
    val raceStatus: RaceStatus = RaceStatus.NOT_STARTED,
    val leaderLap: Int = 0,
    val totalLaps: Int = 53,
    val error: String? = null
)
