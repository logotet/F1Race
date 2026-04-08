package com.vvasilev.f1race.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "drivers")
data class DriverEntity(
    @PrimaryKey val id: String,
    val name: String,
    val abbreviation: String,
    val number: Int,
    val teamName: String
)
