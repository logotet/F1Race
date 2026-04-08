package com.vvasilev.f1race

import android.app.Application
import com.vvasilev.f1race.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class F1RaceApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@F1RaceApplication)
            modules(appModules)
        }
    }
}
