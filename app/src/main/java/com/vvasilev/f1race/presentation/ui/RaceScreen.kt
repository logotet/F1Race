package com.vvasilev.f1race.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vvasilev.f1race.domain.model.RaceStatus
import com.vvasilev.f1race.presentation.intent.RaceIntent
import com.vvasilev.f1race.presentation.ui.components.DriverLeaderboard
import com.vvasilev.f1race.presentation.ui.components.ErrorBanner
import com.vvasilev.f1race.presentation.ui.components.LoadingOverlay
import com.vvasilev.f1race.presentation.ui.components.RaceControls
import com.vvasilev.f1race.presentation.ui.components.TrackCanvas
import com.vvasilev.f1race.presentation.viewmodel.RaceViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun RaceScreen(
    viewModel: RaceViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    // Stop the race simulation when leaving composition (e.g., backgrounded)
    DisposableEffect(Unit) {
        onDispose {
            viewModel.onIntent(RaceIntent.StopStreaming)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        if (state.isLoading) {
            LoadingOverlay()
            return@Box
        }

        Column(modifier = Modifier.fillMaxSize()) {
            // Header: race info + lap counter
            RaceHeader(
                trackName = state.track?.name ?: "",
                raceStatus = state.raceStatus,
                leaderLap = state.leaderLap,
                totalLaps = state.totalLaps,
                modifier = Modifier.fillMaxWidth()
            )

            // Main content: track visualization + leaderboard side by side
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                // Track canvas (takes 60% of width)
                Box(
                    modifier = Modifier
                        .weight(0.6f)
                        .fillMaxHeight()
                        .padding(4.dp)
                ) {
                    val track = state.track
                    if (track != null) {
                        TrackCanvas(
                            track = track,
                            positions = state.positions,
                            drivers = state.drivers,
                            selectedDriverId = state.selectedDriverId,
                            onDriverTap = { driverId ->
                                viewModel.onIntent(RaceIntent.SelectDriver(driverId))
                            }
                        )
                    }
                }

                // Leaderboard (takes 40% of width)
                Column(
                    modifier = Modifier
                        .weight(0.4f)
                        .fillMaxHeight()
                ) {
                    Text(
                        text = "STANDINGS",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    DriverLeaderboard(
                        standings = state.standings,
                        drivers = state.drivers,
                        positions = state.positions,
                        selectedDriverId = state.selectedDriverId,
                        onDriverClick = { driverId ->
                            viewModel.onIntent(RaceIntent.SelectDriver(driverId))
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Controls
            RaceControls(
                raceStatus = state.raceStatus,
                onStart = { viewModel.onIntent(RaceIntent.StartStreaming) },
                onStop = { viewModel.onIntent(RaceIntent.StopStreaming) },
                onRefresh = { viewModel.onIntent(RaceIntent.Refresh) }
            )
        }

        // Error banner overlay
        state.error?.let { error ->
            ErrorBanner(
                message = error,
                onDismiss = { viewModel.onIntent(RaceIntent.ClearError) },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

@Composable
private fun RaceHeader(
    trackName: String,
    raceStatus: RaceStatus,
    leaderLap: Int,
    totalLaps: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = trackName,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = when (raceStatus) {
                    RaceStatus.NOT_STARTED -> "Race not started"
                    RaceStatus.IN_PROGRESS -> "Race in progress"
                    RaceStatus.SAFETY_CAR -> "Safety car deployed"
                    RaceStatus.RED_FLAG -> "Red flag"
                    RaceStatus.FINISHED -> "Race finished"
                },
                style = MaterialTheme.typography.bodySmall,
                color = when (raceStatus) {
                    RaceStatus.IN_PROGRESS -> MaterialTheme.colorScheme.primary
                    RaceStatus.RED_FLAG -> MaterialTheme.colorScheme.error
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }

        // Lap counter
        if (raceStatus == RaceStatus.IN_PROGRESS) {
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "LAP",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "$leaderLap / $totalLaps",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
