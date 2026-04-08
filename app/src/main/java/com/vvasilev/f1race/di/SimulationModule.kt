package com.vvasilev.f1race.di

import com.vvasilev.f1race.data.simulation.SimulationDataSource
import org.koin.dsl.module

val simulationModule = module {
    single { SimulationDataSource() }
}
