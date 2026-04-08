package com.vvasilev.f1race.data.repository

import com.vvasilev.f1race.data.local.dao.DriverDao
import com.vvasilev.f1race.data.local.dao.TrackDao
import com.vvasilev.f1race.data.local.entity.toDomain
import com.vvasilev.f1race.data.local.entity.toEntity
import com.vvasilev.f1race.data.simulation.SimulationDataSource
import com.vvasilev.f1race.data.simulation.TrackData
import com.vvasilev.f1race.domain.model.Driver
import com.vvasilev.f1race.domain.model.DriverPosition
import com.vvasilev.f1race.domain.model.Track
import com.vvasilev.f1race.domain.repository.RaceRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class RaceRepositoryImpl(
    private val simulationDataSource: SimulationDataSource,
    private val driverDao: DriverDao,
    private val trackDao: TrackDao
) : RaceRepository {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val track = TrackData.track
    private val drivers = TrackData.drivers

    init {
        // Seed the local cache on first use
        scope.launch {
            trackDao.insert(track.toEntity())
            driverDao.insertAll(drivers.map { it.toEntity() })
        }
    }

    override fun getTrack(): Flow<Track> = flow {
        emit(track)
    }

    override fun getDrivers(): Flow<List<Driver>> = flow {
        emit(drivers)
    }

    override fun observePositions(): Flow<Map<String, DriverPosition>> {
        return simulationDataSource.positions
    }

    override suspend fun startStreaming() {
        simulationDataSource.initialize(track, drivers)
        // Launch simulation in a separate coroutine (non-blocking)
        scope.launch {
            simulationDataSource.start(track)
        }
    }

    override suspend fun stopStreaming() {
        simulationDataSource.stop()
    }
}
