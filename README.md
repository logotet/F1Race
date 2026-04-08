# F1Race

> A real-time Formula 1 race visualization app for Android — built as a showcase of modern Android development and a playground for [Claude Code](https://claude.ai/code).

![Min SDK](https://img.shields.io/badge/min%20SDK-26-brightgreen)
![Kotlin](https://img.shields.io/badge/Kotlin-2.1.0-blue)
![Compose](https://img.shields.io/badge/Compose%20BOM-2024.12.01-purple)
![License](https://img.shields.io/badge/license-MIT-lightgrey)

---

## Features

- Live race simulation with all 20 F1 2024 drivers
- Monza-inspired track rendered using cubic Bezier curves with arc-length parameterization for uniform-speed motion
- Animated driver dots colored by constructor (Red Bull, Ferrari, Mercedes, etc.)
- Real-time leaderboard sorted by lap / segment / progress
- Lap counter, race status banner (Safety Car, Red Flag, Finished)
- Tap a driver on the track or leaderboard to select them
- MVI architecture — single immutable `RaceState`, all mutations via `RaceIntent`
- Room database persistence + stub WebSocket client ready for a live race feed

## Architecture

```
┌─────────────────────────────────────────────────────┐
│                   Presentation                       │
│  RaceScreen → RaceViewModel → RaceState / RaceIntent │
│  TrackCanvas · DriverLeaderboard · RaceControls      │
└────────────────────┬────────────────────────────────┘
                     │ use cases
┌────────────────────▼────────────────────────────────┐
│                    Domain                            │
│  Track · Driver · DriverPosition · RaceStatus        │
│  GetTrackUseCase · ObserveDriverPositionsUseCase …   │
│  RaceRepository (interface)                          │
└────────────────────┬────────────────────────────────┘
                     │ implements
┌────────────────────▼────────────────────────────────┐
│                     Data                             │
│  SimulationDataSource · RaceRepositoryImpl           │
│  Room (F1Database, DAOs, Entities)                   │
│  Ktor WebSocket client (stub → swap for real feed)   │
└─────────────────────────────────────────────────────┘
        ↑ wired by Koin DI (di/)
        ↑ math helpers in util/ (BezierInterpolator …)
```

---

## Tech Stack

| Layer | Library | Version |
|---|---|---|
| UI | Jetpack Compose + Material3 | BOM 2024.12.01 |
| State | Kotlin Coroutines + Flow | 1.9.0 |
| DI | Koin | 4.0.2 |
| Networking | Ktor (OkHttp engine, WebSockets) | 3.0.3 |
| Database | Room + KSP | 2.6.1 |
| Serialization | kotlinx.serialization | 1.7.3 |
| Language | Kotlin | 2.1.0 |
| Build | AGP | 8.7.3 |

---

## Project Structure

```
app/src/main/java/com/vvasilev/f1race/
├── data/
│   ├── local/          # Room database, DAOs, entities, converters
│   ├── remote/         # WebSocket client, DTOs, mappers
│   ├── repository/     # RaceRepositoryImpl
│   └── simulation/     # SimulationDataSource, TrackData
├── di/                 # Koin modules (Network, Database, Repo, VM …)
├── domain/
│   ├── model/          # Track, Driver, DriverPosition, Team, RaceStatus …
│   ├── repository/     # RaceRepository interface
│   └── usecase/        # GetTrack, GetDrivers, ObservePositions, Start/Stop
├── presentation/
│   ├── intent/         # RaceIntent (sealed interface)
│   ├── state/          # RaceState (immutable data class)
│   ├── ui/
│   │   ├── components/ # TrackCanvas, DriverLeaderboard, RaceControls …
│   │   └── theme/      # Color, Type, Theme
│   └── viewmodel/      # RaceViewModel
├── util/               # BezierInterpolator, TrackPathBuilder, PositionInterpolator
└── F1RaceApplication.kt
```

---

## Getting Started

**Prerequisites**
- Android Studio Iguana or newer
- JDK 17
- Android device / emulator running API 26+

**Build & run**
```bash
git clone https://github.com/logotet/F1Race.git
cd F1Race
./gradlew assembleDebug
# Install on connected device:
./gradlew installDebug
```

**Run unit tests**
```bash
./gradlew testDebugUnitTest
```

---

## Claude Code Playground

This project is intentionally well-layered so it works as a hands-on demo of what [Claude Code](https://claude.ai/code) can do on a real Android codebase.

### Extend the App

```
Add a pit-stop mechanic: once per race, each driver pauses for 3 seconds at segment 5.
```
```
Add a speed indicator — a small colored arc around each driver dot showing relative speed.
```
```
Replace StubRaceWebSocketClient with a real Ktor WebSocket connection to a configurable URL.
```
```
Add a race countdown: show "3 … 2 … 1 … GO" before StartStreaming triggers the simulation.
```

### Refactor & Improve

```
Extract the driver dot drawing logic from TrackCanvas into its own composable function.
```
```
Add a dark/light theme toggle button to RaceControls and wire it to the app theme.
```
```
Migrate the Room database from version 1 to version 2 by adding a fastestLap Float? column to DriverEntity with a proper migration.
```

---

## License

MIT — see [LICENSE](LICENSE) for details.
