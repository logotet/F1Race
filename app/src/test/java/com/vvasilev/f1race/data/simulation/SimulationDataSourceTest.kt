package com.vvasilev.f1race.data.simulation

import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SimulationDataSourceTest {

    @Test
    fun `initialize creates state for all drivers`() = runTest {
        val source = SimulationDataSource()
        source.initialize(TrackData.track, TrackData.drivers)

        val standings = source.getStandings()
        assertEquals(TrackData.drivers.size, standings.size)
    }

    @Test
    fun `simulation emits position updates after starting`() = runTest {
        val source = SimulationDataSource()
        source.initialize(TrackData.track, TrackData.drivers)

        source.positions.test {
            val job = launch { source.start(TrackData.track) }
            // Advance time to allow at least one tick
            advanceTimeBy(200L)

            val firstEmission = awaitItem()
            assertEquals(TrackData.drivers.size, firstEmission.size)
            firstEmission.values.forEach { pos ->
                assertNotNull(pos.driverId)
                assertTrue(pos.segmentProgress in 0f..1f)
            }

            source.stop()
            job.cancel()
            cancelAndIgnoreRemainingEvents()
        }
    }
}
