package com.vvasilev.f1race.data.simulation

import com.vvasilev.f1race.domain.model.Driver
import com.vvasilev.f1race.domain.model.Team
import com.vvasilev.f1race.domain.model.Track
import com.vvasilev.f1race.domain.model.TrackPoint
import com.vvasilev.f1race.domain.model.TrackSegment

/**
 * Hard-coded race data: a Monza-inspired track layout and the 2024 F1 driver roster.
 * All track coordinates are normalized to 0.0-1.0.
 */
object TrackData {

    // DRS zone segment indices (the long straights where drivers get a speed boost)
    val drsZones: Set<Int> = setOf(0, 1, 7, 8)

    val track = Track(
        name = "Autodromo Nazionale Monza",
        country = "Italy",
        segments = listOf(
            // Segment 0: Start/Finish straight (bottom, left to right)
            TrackSegment(
                start = TrackPoint(0.15f, 0.78f),
                controlPoint1 = TrackPoint(0.30f, 0.78f),
                controlPoint2 = TrackPoint(0.45f, 0.78f),
                end = TrackPoint(0.60f, 0.78f)
            ),
            // Segment 1: Continuation of main straight
            TrackSegment(
                start = TrackPoint(0.60f, 0.78f),
                controlPoint1 = TrackPoint(0.70f, 0.78f),
                controlPoint2 = TrackPoint(0.78f, 0.76f),
                end = TrackPoint(0.82f, 0.72f)
            ),
            // Segment 2: Variante del Rettifilo (first chicane, sharp right-left)
            TrackSegment(
                start = TrackPoint(0.82f, 0.72f),
                controlPoint1 = TrackPoint(0.86f, 0.68f),
                controlPoint2 = TrackPoint(0.88f, 0.62f),
                end = TrackPoint(0.86f, 0.56f)
            ),
            // Segment 3: Curva Grande approach
            TrackSegment(
                start = TrackPoint(0.86f, 0.56f),
                controlPoint1 = TrackPoint(0.84f, 0.48f),
                controlPoint2 = TrackPoint(0.82f, 0.40f),
                end = TrackPoint(0.78f, 0.34f)
            ),
            // Segment 4: Curva Grande (sweeping right-hander)
            TrackSegment(
                start = TrackPoint(0.78f, 0.34f),
                controlPoint1 = TrackPoint(0.74f, 0.28f),
                controlPoint2 = TrackPoint(0.68f, 0.22f),
                end = TrackPoint(0.60f, 0.20f)
            ),
            // Segment 5: Variante della Roggia (second chicane)
            TrackSegment(
                start = TrackPoint(0.60f, 0.20f),
                controlPoint1 = TrackPoint(0.54f, 0.18f),
                controlPoint2 = TrackPoint(0.48f, 0.16f),
                end = TrackPoint(0.42f, 0.18f)
            ),
            // Segment 6: Lesmo 1
            TrackSegment(
                start = TrackPoint(0.42f, 0.18f),
                controlPoint1 = TrackPoint(0.36f, 0.20f),
                controlPoint2 = TrackPoint(0.30f, 0.24f),
                end = TrackPoint(0.26f, 0.30f)
            ),
            // Segment 7: Lesmo 2 to back straight
            TrackSegment(
                start = TrackPoint(0.26f, 0.30f),
                controlPoint1 = TrackPoint(0.22f, 0.36f),
                controlPoint2 = TrackPoint(0.18f, 0.42f),
                end = TrackPoint(0.16f, 0.48f)
            ),
            // Segment 8: Back straight (Serraglio)
            TrackSegment(
                start = TrackPoint(0.16f, 0.48f),
                controlPoint1 = TrackPoint(0.14f, 0.54f),
                controlPoint2 = TrackPoint(0.12f, 0.60f),
                end = TrackPoint(0.12f, 0.66f)
            ),
            // Segment 9: Ascari chicane
            TrackSegment(
                start = TrackPoint(0.12f, 0.66f),
                controlPoint1 = TrackPoint(0.12f, 0.70f),
                controlPoint2 = TrackPoint(0.13f, 0.74f),
                end = TrackPoint(0.15f, 0.78f)
            )
        ),
        totalLaps = 53
    )

    val drivers = listOf(
        Driver("VER", "Max Verstappen", "VER", 1, Team.RED_BULL),
        Driver("PER", "Sergio Perez", "PER", 11, Team.RED_BULL),
        Driver("LEC", "Charles Leclerc", "LEC", 16, Team.FERRARI),
        Driver("SAI", "Carlos Sainz", "SAI", 55, Team.FERRARI),
        Driver("NOR", "Lando Norris", "NOR", 4, Team.MCLAREN),
        Driver("PIA", "Oscar Piastri", "PIA", 81, Team.MCLAREN),
        Driver("HAM", "Lewis Hamilton", "HAM", 44, Team.MERCEDES),
        Driver("RUS", "George Russell", "RUS", 63, Team.MERCEDES),
        Driver("ALO", "Fernando Alonso", "ALO", 14, Team.ASTON_MARTIN),
        Driver("STR", "Lance Stroll", "STR", 18, Team.ASTON_MARTIN),
        Driver("GAS", "Pierre Gasly", "GAS", 10, Team.ALPINE),
        Driver("OCO", "Esteban Ocon", "OCO", 31, Team.ALPINE),
        Driver("ALB", "Alexander Albon", "ALB", 23, Team.WILLIAMS),
        Driver("SAR", "Logan Sargeant", "SAR", 2, Team.WILLIAMS),
        Driver("TSU", "Yuki Tsunoda", "TSU", 22, Team.RB),
        Driver("RIC", "Daniel Ricciardo", "RIC", 3, Team.RB),
        Driver("BOT", "Valtteri Bottas", "BOT", 77, Team.SAUBER),
        Driver("ZHO", "Zhou Guanyu", "ZHO", 24, Team.SAUBER),
        Driver("MAG", "Kevin Magnussen", "MAG", 20, Team.HAAS),
        Driver("HUL", "Nico Hulkenberg", "HUL", 27, Team.HAAS)
    )
}
