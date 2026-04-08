package com.vvasilev.f1race.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vvasilev.f1race.data.local.entity.DriverEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DriverDao {
    @Query("SELECT * FROM drivers")
    fun getAll(): Flow<List<DriverEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(drivers: List<DriverEntity>)

    @Query("DELETE FROM drivers")
    suspend fun deleteAll()
}
