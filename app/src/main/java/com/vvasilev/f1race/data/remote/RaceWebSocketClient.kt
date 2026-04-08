package com.vvasilev.f1race.data.remote

import com.vvasilev.f1race.data.remote.dto.DriverPositionDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

/**
 * Interface for a WebSocket client that streams live race position data.
 * Currently stubbed — swap in a real implementation when a live server is available.
 */
interface RaceWebSocketClient {
    fun connect(url: String): Flow<List<DriverPositionDto>>
    suspend fun disconnect()
}

/**
 * Stub implementation that returns empty flows.
 * Replace with Ktor WebSocket implementation for real-time data:
 *
 * ```
 * class KtorRaceWebSocketClient(private val client: HttpClient) : RaceWebSocketClient {
 *     override fun connect(url: String) = flow {
 *         client.webSocket(url) {
 *             for (frame in incoming) {
 *                 if (frame is Frame.Text) {
 *                     val positions = Json.decodeFromString<List<DriverPositionDto>>(frame.readText())
 *                     emit(positions)
 *                 }
 *             }
 *         }
 *     }
 * }
 * ```
 */
class StubRaceWebSocketClient : RaceWebSocketClient {
    override fun connect(url: String): Flow<List<DriverPositionDto>> = emptyFlow()
    override suspend fun disconnect() {}
}
