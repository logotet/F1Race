package com.vvasilev.f1race.presentation.intent

sealed interface RaceIntent {
    data object LoadRace : RaceIntent
    data object StartStreaming : RaceIntent
    data object StopStreaming : RaceIntent
    data object Refresh : RaceIntent
    data class SelectDriver(val driverId: String?) : RaceIntent
    data object ClearError : RaceIntent
}
