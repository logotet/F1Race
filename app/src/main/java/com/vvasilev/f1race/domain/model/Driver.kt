package com.vvasilev.f1race.domain.model

data class Driver(
    val id: String,
    val name: String,
    val abbreviation: String,
    val number: Int,
    val team: Team
)
