package com.vvasilev.f1race.di

import androidx.room.Room
import com.vvasilev.f1race.data.local.F1Database
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            F1Database::class.java,
            "f1race.db"
        ).fallbackToDestructiveMigration().build()
    }

    single { get<F1Database>().driverDao() }
    single { get<F1Database>().trackDao() }
}
