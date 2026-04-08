package com.vvasilev.f1race.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vvasilev.f1race.domain.model.RaceStatus
import com.vvasilev.f1race.domain.usecase.GetDriversUseCase
import com.vvasilev.f1race.domain.usecase.GetTrackUseCase
import com.vvasilev.f1race.domain.usecase.ObserveDriverPositionsUseCase
import com.vvasilev.f1race.domain.usecase.StartRaceStreamingUseCase
import com.vvasilev.f1race.domain.usecase.StopRaceStreamingUseCase
import com.vvasilev.f1race.presentation.intent.RaceIntent
import com.vvasilev.f1race.presentation.state.RaceState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RaceViewModel(
    private val getTrack: GetTrackUseCase,
    private val getDrivers: GetDriversUseCase,
    private val observePositions: ObserveDriverPositionsUseCase,
    private val startStreaming: StartRaceStreamingUseCase,
    private val stopStreaming: StopRaceStreamingUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(RaceState())
    val state: StateFlow<RaceState> = _state.asStateFlow()

    private var positionCollectionJob: Job? = null

    init {
        onIntent(RaceIntent.LoadRace)
    }

    fun onIntent(intent: RaceIntent) {
        when (intent) {
            is RaceIntent.LoadRace -> loadRace()
            is RaceIntent.StartStreaming -> startRaceStreaming()
            is RaceIntent.StopStreaming -> stopRaceStreaming()
            is RaceIntent.Refresh -> refresh()
            is RaceIntent.SelectDriver -> selectDriver(intent.driverId)
            is RaceIntent.ClearError -> _state.update { it.copy(error = null) }
        }
    }

    private fun loadRace() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val track = getTrack().first()
                val drivers = getDrivers().first()
                _state.update {
                    it.copy(
                        isLoading = false,
                        track = track,
                        drivers = drivers,
                        totalLaps = track.totalLaps
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(isLoading = false, error = e.message ?: "Failed to load race data")
                }
            }
        }
    }

    private fun startRaceStreaming() {
        if (_state.value.raceStatus == RaceStatus.IN_PROGRESS) return

        viewModelScope.launch {
            try {
                startStreaming()
                _state.update { it.copy(raceStatus = RaceStatus.IN_PROGRESS) }

                positionCollectionJob?.cancel()
                positionCollectionJob = viewModelScope.launch {
                    observePositions()
                        .catch { e ->
                            _state.update { it.copy(error = e.message ?: "Stream error") }
                        }
                        .collect { positions ->
                            // Compute standings (sorted by lap desc, then segment desc, then progress desc)
                            val standings = positions.entries.sortedWith(
                                compareByDescending<Map.Entry<String, com.vvasilev.f1race.domain.model.DriverPosition>> { it.value.lap }
                                    .thenByDescending { it.value.segmentIndex }
                                    .thenByDescending { it.value.segmentProgress }
                            ).map { it.key }

                            val leaderLap = positions.values.maxOfOrNull { it.lap } ?: 0

                            _state.update {
                                it.copy(
                                    positions = positions,
                                    standings = standings,
                                    leaderLap = leaderLap
                                )
                            }
                        }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(error = e.message ?: "Failed to start streaming")
                }
            }
        }
    }

    private fun stopRaceStreaming() {
        viewModelScope.launch {
            positionCollectionJob?.cancel()
            positionCollectionJob = null
            stopStreaming()
            _state.update { it.copy(raceStatus = RaceStatus.NOT_STARTED) }
        }
    }

    private fun refresh() {
        stopRaceStreaming()
        loadRace()
    }

    private fun selectDriver(driverId: String?) {
        _state.update {
            // Toggle: if same driver selected, deselect
            val newSelection = if (it.selectedDriverId == driverId) null else driverId
            it.copy(selectedDriverId = newSelection)
        }
    }

    override fun onCleared() {
        super.onCleared()
        positionCollectionJob?.cancel()
    }
}
