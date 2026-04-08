package com.vvasilev.f1race.domain.usecase

import com.vvasilev.f1race.domain.model.Driver
import com.vvasilev.f1race.domain.repository.RaceRepository
import kotlinx.coroutines.flow.Flow

class GetDriversUseCase(private val repository: RaceRepository) {
    operator fun invoke(): Flow<List<Driver>> = repository.getDrivers()
}
