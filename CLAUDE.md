# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

CounterOverlay is a Kotlin Multiplatform (KMP) application targeting Android, Desktop (JVM), and Web (WASM). It demonstrates a counter management system with real-time synchronization via WebSockets and local persistence using Room 3.0.

## Common Build Commands

### Android
```bash
# Build Android APK
./gradlew :composeApp:assembleDebug

# Install on connected device
./gradlew :composeApp:installDebug
```

### Desktop
```bash
# Run desktop application
./gradlew :composeApp:runDistributable

# Create native installer (MSI/DMG/DEB)
./gradlew :composeApp:packageDistributionForCurrentOS
```

### Web (WASM)
```bash
# Run development server with hot reload
./gradlew :composeApp:wasmJsBrowserDevelopmentRun

# Build production bundle
./gradlew :composeApp:wasmJsBrowserDistribution
```

### General Tasks
```bash
# Clean build
./gradlew clean

# Build all targets
./gradlew build

# Room code generation (via KSP)
./gradlew :composeApp:kspGenerateCommonMainKotlinMetadata
```

## Architecture

### Layered Architecture Pattern

The project follows a clean 3-layer architecture:

1. **Data Layer** (`counter/local/`, `counter/remote/`)
   - `CounterLocalDataSource`: Room-based local persistence with Flow-based reactive queries
   - `CounterRemoteDataSource`: Ktor HTTP/WebSocket client with automatic reconnection logic

2. **Repository Layer** (`counter/repository/`)
   - `CounterRepositoryImpl`: Orchestrates local and remote data sources
   - Implements sync pattern: Remote API → Local DB → UI
   - Local database serves as single source of truth

3. **UI Layer** (`screens/`)
   - MVI (Model-View-Intent) pattern via `BaseViewModel`
   - Compose Multiplatform UI components
   - Type-safe navigation with serializable routes

### Key Architectural Decisions

**MVI State Management**
- Base class: `mvi/BaseViewModel.kt` provides generic `<UiState, UiEvent, UiAction>` pattern
- State exposed as `StateFlow<UiState>` for UI observation
- Events processed via `onEvent(uiEvent)` → `reduceState()` pattern
- ViewModels never directly observe remote sources; only the local database

**Data Flow Pattern**
```
Remote API/WebSocket
    ↓
CounterRemoteDataSource
    ↓
CounterRepository (maps DTOs → Entities)
    ↓
CounterLocalDataSource (Room 3.0)
    ↓
Flow<List<Counter>> (observable)
    ↓
ViewModel StateFlow
    ↓
Composable UI
```

**Error Handling**
- `LoadResult<S, E>` sealed class wraps success/error states
- Provides functional operators: `doOnSuccess`, `doOnError`, `mapSuccess`
- Repository methods return `LoadResult<Unit, Throwable>` for explicit error handling

**Real-time Synchronization**
- WebSocket connection managed by `CounterRemoteDataSource` with exponential backoff
- Updates flow through `MutableSharedFlow<LoadResult<CounterDto>>`
- Repository writes WebSocket updates to local DB, triggering UI updates via Flow
- Connection cleanup in `ViewModel.onCleared()` via `counterRepository.closeSession()`

### Dependency Injection with Koin

**Module Structure** (`di/AppModule.kt`, `di/AppModule.<platform>.kt`)
- `coreModule`: HttpClient, CounterDatabase, background dispatcher
- `counterModule`: ViewModels, Repository, Data Sources
- Platform modules (`android`, `desktop`, `wasmJs`): Provide platform-specific configuration

**Important DI Patterns**
- Room database initialized with KSP-generated code
- Platform-specific database builders use `expect`/`actual` pattern (e.g., `DatabaseBuilder`, `httpClient()`)
- Database configuration passed via `PlatformConfiguration`

### Platform-Specific Implementations

**Room Database Builders** (`counter/local/room/DatabaseBuilder.<platform>.kt`)
- **Android**: `Room.databaseBuilder` with persistent file storage (`counter_room.db`)
- **Desktop**: `Room.databaseBuilder` with temp file storage (`counter_room.db`)
- **WasmJs**: `Room.databaseBuilder` with `WebWorkerSQLiteDriver` using sql.js in Web Worker

**HTTP Clients** (`core/Platform.<platform>.kt`)
- **Android/Desktop**: OkHttp engine with 20-second ping intervals
- **WasmJs**: JavaScript engine (Ktor-JS)

**Background Dispatcher**
- **Android/Desktop**: `Dispatchers.IO` for blocking database operations
- **WasmJs**: `Dispatchers.Default` (no blocking I/O available)

**WasmJs-Specific Features**
- Includes `CounterOverlayRoute` for embedded overlay display (not available on other platforms)
- Browser navigation binding via `navController.bindToBrowserNavigation()`
- Custom webpack configuration in `src/webpack.config.d/`

### Domain Models & Mapping

Three representations of counter data:
- `Counter` (domain model): Used in UI layer
- `CounterEntity` (Room): Data class with `@Entity` annotation in `counter/local/room/`
- `CounterDto` (remote): JSON-serializable for API communication

Mapping functions in `counter/Counter.kt`:
- `CounterEntity.toDomain()`: Database → Domain
- `CounterDto.toEntity()`: Remote → Database
- `CounterDto.toDomain()`: Remote → Domain (via Entity)

### Navigation

Type-safe routes using `@Serializable` data objects (`navigation/Navigation.kt`):
- `CounterListRoute`: Main counter list screen
- `CounterDetailsRoute(id: String)`: Detail view with WebSocket connection
- `AddCounterDialogRoute`: Dialog for creating new counters
- `CounterOverlayRoute(counterId: String)`: WasmJs-only overlay screen

Platform-specific routes registered via `expect fun NavGraphBuilder.platformSpecificRoutes()`

## Database Schema

Room database schema defined in `composeApp/src/commonMain/kotlin/com/bashkevich/counteroverlay/counter/local/room/CounterEntity.kt`:

```kotlin
@Entity(tableName = "counters")
data class CounterEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val amount: Int
)
```

**DAO Queries** (`CounterDao.kt`):
- `getAllCounters()`: Returns Flow<List<CounterEntity>> for reactive queries
- `getCounterById(id: String)`: Returns Flow<CounterEntity?> for specific counter
- `insertCounter(counter: CounterEntity)`: Upsert operation (REPLACE conflict strategy)
- `insertCounters(counters: List<CounterEntity>)`: Batch insert
- `deleteAllCounters()`: Clear all counters

## Code Modification Guidelines

**When Adding New Features**
1. Define domain model in appropriate package
2. Add Room Entity/DAO changes with annotations, then rebuild project for KSP generation
3. Create/update data source interfaces and implementations
4. Update repository layer with mapping logic
5. Define MVI state/events/actions in ViewModel
6. Implement Composable UI consuming StateFlow

**When Adding Platform-Specific Code**
1. Define `expect` declaration in `commonMain`
2. Provide `actual` implementations in each platform sourceSet
3. Register platform-specific dependencies in `AppModule.<platform>.kt`
4. For UI differences, use `expect`/`actual` composables or platform checks

**When Modifying Database Schema**
1. Update Entity/DAO annotations in `counter/local/room/` package
2. Increment database version in `@Database` annotation
3. Rebuild project for KSP code generation: `./gradlew build`
4. Update mapping functions in domain model files
5. Consider migration strategy for existing users (currently no migrations defined)

**WebSocket Connection Management**
- Always clean up connections in `ViewModel.onCleared()`
- Use `counterRepository.closeSession()` to properly close WebSocket
- Reconnection logic is automatic with exponential backoff (managed by `CounterRemoteDataSource`)

## Common Patterns to Follow

**ViewModel Lifecycle**
```kotlin
init {
    // Fetch from remote on initialization
    viewModelScope.launch { repository.fetchCounters() }

    // Observe local database for UI updates
    viewModelScope.launch {
        repository.observeCountersFromDatabase()
            .collect { onEvent(UiEvent.ShowData(it)) }
    }
}

override fun onCleared() {
    viewModelScope.launch { repository.closeSession() }
    super.onCleared()
}
```

**Repository Pattern**
```kotlin
override suspend fun someOperation(): LoadResult<Unit, Throwable> {
    return remoteDataSource.apiCall()
        .doOnSuccess { dto ->
            localDataSource.save(dto.toEntity())
        }
        .mapSuccess { }
}
```

**State Reduction**
```kotlin
fun onEvent(event: UiEvent) {
    when (event) {
        is UiEvent.DataLoaded -> reduceState {
            it.copy(data = event.data, isLoading = false)
        }
    }
}
```

## Testing Considerations

Currently no test suites are defined. When adding tests:
- Unit tests for repositories should mock data sources
- ViewModel tests should verify state transitions via MVI events
- Use in-memory Room database for database tests
- Mock Ktor client for network tests

## Important Notes

- **Never block the main thread**: Use appropriate dispatchers for database operations
- **Lifecycle management is critical**: Always close WebSocket sessions in `onCleared()`
- **Single source of truth**: UI observes only the local database, never remote sources directly
- **Platform abstraction**: Prefer `expect`/`actual` over runtime platform checks when possible
- **KSP code generation**: Room generates code automatically via KSP during build
