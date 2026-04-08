package com.vvasilev.f1race.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.vvasilev.f1race.domain.model.Driver
import com.vvasilev.f1race.domain.model.DriverPosition

@Composable
fun DriverLeaderboard(
    standings: List<String>,
    drivers: List<Driver>,
    positions: Map<String, DriverPosition>,
    selectedDriverId: String?,
    onDriverClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val driverMap = drivers.associateBy { it.id }

    LazyColumn(
        modifier = modifier.padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        itemsIndexed(standings, key = { _, id -> id }) { index, driverId ->
            val driver = driverMap[driverId] ?: return@itemsIndexed
            val position = positions[driverId]
            val isSelected = driverId == selectedDriverId
            val hasSelection = selectedDriverId != null

            DriverRow(
                position = index + 1,
                driver = driver,
                lap = position?.lap ?: 0,
                isSelected = isSelected,
                dimmed = hasSelection && !isSelected,
                onClick = { onDriverClick(driverId) }
            )
        }
    }
}

@Composable
private fun DriverRow(
    position: Int,
    driver: Driver,
    lap: Int,
    isSelected: Boolean,
    dimmed: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(
                if (isSelected) MaterialTheme.colorScheme.surfaceVariant
                else MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .alpha(if (dimmed) 0.5f else 1f),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Position number
        Text(
            text = position.toString().padStart(2),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.width(24.dp)
        )

        Spacer(modifier = Modifier.width(6.dp))

        // Team color indicator
        Box(
            modifier = Modifier
                .size(4.dp, 20.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(driver.team.color)
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Driver info
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = driver.abbreviation,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = driver.name,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }

        // Lap info
        Text(
            text = "L$lap",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
