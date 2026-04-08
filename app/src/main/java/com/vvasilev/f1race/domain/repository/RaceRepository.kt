package com.vvasilev.f1race.domain.repository

import com.vvasilev.f1race.domain.model.Driver
import com.vvasilev.f1race.domain.model.DriverPosition
import com.vvasilev.f1race.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface RaceRepository {
    fun getTrack(): Flow<Track>
    fun getDrivers(): Flow<List<Driver>>
    fun observePositions(): Flow<Map<String, DriverPosition>>
    suspend fun startStreaming()
    suspend fun stopStreaming()
}
