package com.vvasilev.f1race.domain.usecase

import com.vvasilev.f1race.domain.repository.RaceRepository

class StopRaceStreamingUseCase(private val repository: RaceRepository) {
    suspend operator fun invoke() = repository.stopStreaming()
}
