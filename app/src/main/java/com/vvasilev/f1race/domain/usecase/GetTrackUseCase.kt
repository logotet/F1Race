package com.vvasilev.f1race.domain.usecase

import com.vvasilev.f1race.domain.model.Track
import com.vvasilev.f1race.domain.repository.RaceRepository
import kotlinx.coroutines.flow.Flow

class GetTrackUseCase(private val repository: RaceRepository) {
    operator fun invoke(): Flow<Track> = repository.getTrack()
}
