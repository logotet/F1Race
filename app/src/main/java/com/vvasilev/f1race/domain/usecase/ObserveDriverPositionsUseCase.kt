package com.vvasilev.f1race.domain.usecase

import com.vvasilev.f1race.domain.model.DriverPosition
import com.vvasilev.f1race.domain.repository.RaceRepository
import kotlinx.coroutines.flow.Flow

class ObserveDriverPositionsUseCase(private val repository: RaceRepository) {
    operator fun invoke(): Flow<Map<String, DriverPosition>> = repository.observePositions()
}
