package com.vvasilev.f1race.data.simulation

import com.vvasilev.f1race.domain.model.Driver
import com.vvasilev.f1race.domain.model.DriverPosition
import com.vvasilev.f1race.domain.model.Track
import com.vvasilev.f1race.util.BezierInterpolator
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlin.math.sin
import kotlin.random.Random

class SimulationDataSource {

    companion object {
        private const val TICK_MS = 80L
        private const val BASE_SPEED = 0.012f  // segments per tick (normalized)
        private const val SPEED_VARIATION = 0.003f
        private const val DRS_BONUS = 1.15f
    }

    private val _positions = MutableSharedFlow<Map<String, DriverPosition>>(replay = 1)
    val positions: SharedFlow<Map<String, DriverPosition>> = _positions.asSharedFlow()

    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()

    private var simulationJob: Job? = null
    private val driverStates = mutableMapOf<String, DriverSimState>()
    private var segmentLengths: List<Float> = emptyList()
    private var totalTrackLength: Float = 0f

    private data class DriverSimState(
        var segmentIndex: Int,
        var segmentProgress: Float,
        var lap: Int,
        var baseSpeedFactor: Float,
        var oscillationPhase: Float,
        var oscillationFrequency: Float,
        var currentSpeed: Float
    )

    fun initialize(track: Track, drivers: List<Driver>) {
        // Pre-compute segment lengths for proportional speed
        segmentLengths = track.segments.map { BezierInterpolator.segmentLength(it) }
        totalTrackLength = segmentLengths.sum()

        driverStates.clear()
        val random = Random(42)

        drivers.forEachIndexed { index, driver ->
            // Stagger initial positions: spread drivers across first 2 segments
            val startSegment = 0
            val startProgress = 1.0f - (index * 0.04f).coerceIn(0f, 0.95f)

            // Each driver has a unique speed profile
            val speedFactor = 1.0f + (random.nextFloat() - 0.5f) * 0.08f // +/- 4%
            val oscPhase = random.nextFloat() * 6.28f
            val oscFreq = 0.5f + random.nextFloat() * 1.5f // different oscillation per driver

            driverStates[driver.id] = DriverSimState(
                segmentIndex = startSegment,
                segmentProgress = startProgress.coerceIn(0f, 1f),
                lap = 0,
                baseSpeedFactor = speedFactor,
                oscillationPhase = oscPhase,
                oscillationFrequency = oscFreq,
                currentSpeed = BASE_SPEED * speedFactor
            )
        }
    }

    suspend fun start(track: Track) = coroutineScope {
        if (_isRunning.value) return@coroutineScope
        _isRunning.value = true

        var tick = 0L
        while (isActive && _isRunning.value) {
            tick++
            advanceAll(track, tick)
            emitPositions()
            delay(TICK_MS)
        }
    }

    fun stop() {
        _isRunning.value = false
    }

    private fun advanceAll(track: Track, tick: Long) {
        val segmentCount = track.segments.size
        val time = tick * TICK_MS / 1000.0

        for ((driverId, state) in driverStates) {
            // Speed varies over time with a unique oscillation per driver
            val oscillation = sin(time * state.oscillationFrequency + state.oscillationPhase.toDouble()).toFloat()
            var speed = BASE_SPEED * state.baseSpeedFactor * (1f + SPEED_VARIATION * oscillation)

            // DRS boost on designated segments
            if (state.segmentIndex in TrackData.drsZones) {
                speed *= DRS_BONUS
            }

            // Adjust speed based on segment length (go faster on shorter segments to maintain
            // visual consistency — otherwise cars would appear to slow down on short chicanes)
            val segLen = segmentLengths.getOrElse(state.segmentIndex) { 1f }
            val avgLen = totalTrackLength / segmentCount
            val lengthFactor = avgLen / segLen.coerceAtLeast(0.001f)
            speed *= lengthFactor.coerceIn(0.5f, 2.0f)

            state.currentSpeed = speed
            state.segmentProgress += speed

            // Wrap to next segment(s)
            while (state.segmentProgress >= 1f) {
                state.segmentProgress -= 1f
                state.segmentIndex++
                if (state.segmentIndex >= segmentCount) {
                    state.segmentIndex = 0
                    state.lap++
                }
            }
        }
    }

    private suspend fun emitPositions() {
        val positions = driverStates.mapValues { (_, state) ->
            DriverPosition(
                driverId = "",  // filled by key
                segmentIndex = state.segmentIndex,
                segmentProgress = state.segmentProgress,
                lap = state.lap,
                speed = state.currentSpeed,
                timestamp = System.currentTimeMillis()
            )
        }.mapKeys { it.key }.mapValues { (driverId, pos) ->
            pos.copy(driverId = driverId)
        }
        _positions.emit(positions)
    }

    /**
     * Get the race standings sorted by position (most laps + furthest along track).
     */
    fun getStandings(): List<String> {
        return driverStates.entries.sortedWith(
            compareByDescending<Map.Entry<String, DriverSimState>> { it.value.lap }
                .thenByDescending { it.value.segmentIndex }
                .thenByDescending { it.value.segmentProgress }
        ).map { it.key }
    }
}
