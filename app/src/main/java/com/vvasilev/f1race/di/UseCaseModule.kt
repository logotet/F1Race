package com.vvasilev.f1race.di

import com.vvasilev.f1race.domain.usecase.GetDriversUseCase
import com.vvasilev.f1race.domain.usecase.GetTrackUseCase
import com.vvasilev.f1race.domain.usecase.ObserveDriverPositionsUseCase
import com.vvasilev.f1race.domain.usecase.StartRaceStreamingUseCase
import com.vvasilev.f1race.domain.usecase.StopRaceStreamingUseCase
import org.koin.dsl.module

val useCaseModule = module {
    factory { GetTrackUseCase(get()) }
    factory { GetDriversUseCase(get()) }
    factory { ObserveDriverPositionsUseCase(get()) }
    factory { StartRaceStreamingUseCase(get()) }
    factory { StopRaceStreamingUseCase(get()) }
}
