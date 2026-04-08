package com.vvasilev.f1race.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.vvasilev.f1race.data.local.converter.Converters
import com.vvasilev.f1race.data.local.dao.DriverDao
import com.vvasilev.f1race.data.local.dao.TrackDao
import com.vvasilev.f1race.data.local.entity.DriverEntity
import com.vvasilev.f1race.data.local.entity.TrackEntity

@Database(
    entities = [DriverEntity::class, TrackEntity::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class F1Database : RoomDatabase() {
    abstract fun driverDao(): DriverDao
    abstract fun trackDao(): TrackDao
}
