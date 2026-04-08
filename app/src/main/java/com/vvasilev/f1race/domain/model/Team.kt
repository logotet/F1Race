package com.vvasilev.f1race.domain.model

import androidx.compose.ui.graphics.Color

enum class Team(
    val teamName: String,
    val color: Color
) {
    RED_BULL("Red Bull Racing", Color(0xFF3671C6)),
    FERRARI("Scuderia Ferrari", Color(0xFFE8002D)),
    MCLAREN("McLaren", Color(0xFFFF8000)),
    MERCEDES("Mercedes-AMG", Color(0xFF27F4D2)),
    ASTON_MARTIN("Aston Martin", Color(0xFF229971)),
    ALPINE("Alpine", Color(0xFFFF87BC)),
    WILLIAMS("Williams", Color(0xFF64C4FF)),
    RB("Visa Cash App RB", Color(0xFF6692FF)),
    SAUBER("Stake F1 Team", Color(0xFF52E252)),
    HAAS("MoneyGram Haas", Color(0xFFB6BABD))
}
