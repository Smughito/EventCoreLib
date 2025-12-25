# Project Structure

## Overview

EventCoreLib follows a clean, modular architecture with clear separation of concerns:

```
EventCoreLib/
├── src/main/
│   ├── java/com/smughito/eventcorelib/
│   │   ├── api/              # Public API interfaces
│   │   ├── core/             # Core implementations
│   │   ├── storage/          # Persistence layer
│   │   ├── integration/      # Third-party integrations
│   │   ├── util/             # Utility classes
│   │   └── EventCoreLib.java # Main plugin class
│   └── resources/
│       ├── plugin.yml        # Plugin manifest
│       └── config.yml        # Default configuration
├── build.gradle              # Build configuration
├── settings.gradle           # Gradle settings
└── README.md                # Documentation
```

## Package Breakdown

### `api` Package - Public API

Contains only interfaces that other plugins will use. These define the contract for team management.

**Files:**
- `EventCoreAPI.java` - Main API entry point
- `TeamManager.java` - Interface for managing teams
- `Team.java` - Interface representing a team

**Design Pattern:** Interface Segregation Principle (ISP)

### `core` Package - Implementation

Contains the actual implementation of the API interfaces.

**Files:**
- `TeamImpl.java` - Concrete implementation of Team
- `TeamManagerImpl.java` - Concrete implementation of TeamManager

**Design Pattern:** Repository Pattern for data access

### `storage` Package - Persistence Layer

Handles all data persistence operations with pluggable storage backends.

**Files:**
- `StorageProvider.java` - Storage interface
- `YamlStorageProvider.java` - YAML file storage implementation
- `SqliteStorageProvider.java` - SQLite database implementation

**Design Pattern:** Strategy Pattern for storage selection

### `integration` Package - External Dependencies

Handles integration with third-party plugins.

**Files:**
- `LuckPermsIntegration.java` - LuckPerms API integration

**Design Pattern:** Adapter Pattern for external systems

### `util` Package - Utilities

Helper classes for formatting and text processing.

**Files:**
- `ChatFormatter.java` - Chat message formatting
- `TabListFormatter.java` - Tab list name formatting
- `TextUtil.java` - General text utilities

**Design Pattern:** Utility/Helper classes

## Architecture Principles

### 1. Dependency Inversion
- High-level modules (core) don't depend on low-level modules (storage)
- Both depend on abstractions (interfaces)

### 2. Single Responsibility
- Each class has one clear purpose
- Storage classes only handle persistence
- Manager classes only handle business logic
- Utility classes only handle formatting

### 3. Open/Closed Principle
- Open for extension (new storage providers can be added)
- Closed for modification (existing code doesn't need to change)

### 4. Interface Segregation
- Clients only see what they need (API interfaces)
- Implementation details are hidden

## Data Flow

```
Other Plugin
    ↓
EventCoreAPI
    ↓
TeamManager (Interface)
    ↓
TeamManagerImpl
    ↓
StorageProvider (Interface)
    ↓
YamlStorageProvider OR SqliteStorageProvider
```

## Configuration Flow

```
EventCoreLib.onEnable()
    ↓
1. Load config.yml
    ↓
2. Initialize Storage (YAML or SQLite)
    ↓
3. Initialize LuckPerms Integration
    ↓
4. Initialize TeamManager
    ↓
5. Initialize Formatters
    ↓
6. Load persisted data
    ↓
Ready for API calls
```

## Storage Formats

### YAML Storage

**teams.yml:**
```yaml
teams:
  red:
    display-name: "Red Team"
    color: "RED"
    prefix: "<red>[RED]</red>"
    suffix: ""
    members:
      - "uuid-1"
      - "uuid-2"
```

**assignments.yml:**
```yaml
assignments:
  uuid-1: "red"
  uuid-2: "red"
```

### SQLite Storage

**Table: teams**
- id (TEXT PRIMARY KEY)
- display_name (TEXT)
- color (TEXT)
- prefix (TEXT)
- suffix (TEXT)

**Table: team_members**
- player_uuid (TEXT PRIMARY KEY)
- team_id (TEXT FOREIGN KEY)

## Extension Points

### Adding a New Storage Provider

1. Implement `StorageProvider` interface
2. Add initialization logic in `EventCoreLib.initializeStorage()`
3. Add config option for the new storage type

### Adding New Team Properties

1. Add getter/setter to `Team` interface
2. Update `TeamImpl` to store the property
3. Update both storage providers to persist it

### Adding New Integrations

1. Create new class in `integration` package
2. Initialize in `EventCoreLib.onEnable()`
3. Expose through getter method if needed by other plugins

## Thread Safety

- `TeamManagerImpl` uses `ConcurrentHashMap` for thread-safe operations
- Storage operations are synchronized at the provider level
- No shared mutable state between threads

## Performance Considerations

- Teams are cached in memory (no disk access for reads)
- Write operations are immediate but can be batched
- SQLite uses prepared statements to prevent SQL injection
- YAML operations are file-based (slower than SQLite for large datasets)

## Testing Strategy

While tests aren't included in v1.0.0, here's the recommended approach:

1. **Unit Tests:** Test each class in isolation with mocks
2. **Integration Tests:** Test storage providers with actual files/databases
3. **API Tests:** Test the public API from a dependent plugin's perspective
4. **Performance Tests:** Test with hundreds of teams and thousands of players
