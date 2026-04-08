package com.vvasilev.f1race.di

import com.vvasilev.f1race.data.repository.RaceRepositoryImpl
import com.vvasilev.f1race.domain.repository.RaceRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<RaceRepository> { RaceRepositoryImpl(get(), get(), get()) }
}
