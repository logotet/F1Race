package com.vvasilev.f1race.di

import com.vvasilev.f1race.presentation.viewmodel.RaceViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { RaceViewModel(get(), get(), get(), get(), get()) }
}
