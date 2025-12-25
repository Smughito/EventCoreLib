# Project Structure

```
EventCoreLib/
├── .github/
│   └── workflows/
│       └── build.yml              # GitHub Actions build workflow
├── .gitignore                     # Git ignore patterns
├── build.gradle                   # Gradle build configuration
├── settings.gradle                # Gradle settings
├── gradlew                        # Gradle wrapper script (Unix)
├── gradlew.bat                    # Gradle wrapper script (Windows)
├── gradle/
│   └── wrapper/
│       ├── gradle-wrapper.jar     # Gradle wrapper JAR
│       └── gradle-wrapper.properties
├── LICENSE                        # MIT License
├── README.md                      # Main documentation
├── BUILD.md                       # Build instructions
├── GITHUB_SETUP.md               # GitHub setup guide
├── STRUCTURE.md                  # This file
├── examples/
│   ├── ExampleUsage.java         # Example plugin implementation
│   └── plugin.yml                # Example plugin configuration
└── src/
    └── main/
        ├── java/
        │   └── dev/
        │       └── smughito/
        │           └── eventcorelib/
        │               ├── EventCoreLib.java      # Main plugin class
        │               ├── api/                    # Public API interfaces
        │               │   ├── Team.java
        │               │   ├── TeamManager.java
        │               │   └── PlayerTeamService.java
        │               ├── core/                   # Core implementations
        │               │   ├── TeamImpl.java
        │               │   ├── TeamManagerImpl.java
        │               │   └── PlayerTeamServiceImpl.java
        │               ├── storage/                # Storage providers
        │               │   ├── StorageProvider.java
        │               │   ├── YamlStorageProvider.java
        │               │   └── SQLiteStorageProvider.java
        │               ├── integration/            # External integrations
        │               │   └── LuckPermsIntegration.java
        │               └── util/                   # Utility classes
        │                   ├── ChatFormatter.java
        │                   └── TabListFormatter.java
        └── resources/
            ├── plugin.yml          # Plugin metadata
            └── config.yml          # Configuration template

```

## Module Descriptions

### API Package (`api/`)
Contains only interfaces that other plugins interact with. This provides a stable contract.

- **Team**: Interface for team objects with getters/setters
- **TeamManager**: Main team management operations
- **PlayerTeamService**: Simplified player-centric operations

### Core Package (`core/`)
Contains implementations of the API interfaces. Hidden from external plugins.

- **TeamImpl**: Concrete team implementation
- **TeamManagerImpl**: Team management logic
- **PlayerTeamServiceImpl**: Player service wrapper

### Storage Package (`storage/`)
Handles data persistence with multiple backend support.

- **StorageProvider**: Storage interface
- **YamlStorageProvider**: YAML-based storage (default)
- **SQLiteStorageProvider**: SQLite database storage

### Integration Package (`integration/`)
External plugin integrations.

- **LuckPermsIntegration**: LuckPerms prefix/suffix support

### Util Package (`util/`)
Utility classes for formatting and helper functions.

- **ChatFormatter**: Formats chat messages with team colors
- **TabListFormatter**: Formats tab list display names

## Key Design Decisions

1. **Interface-based API**: Other plugins depend on interfaces, not implementations
2. **Single responsibility**: Each class has one clear purpose
3. **Configurable storage**: Easy to switch between YAML and SQLite
4. **Soft dependency**: LuckPerms is optional, plugin works without it
5. **Adventure API**: Modern text formatting with MiniMessage support
6. **No commands**: Pure library, no gameplay features

## Dependencies

- **Paper API 1.21.1**: Core Minecraft API
- **LuckPerms API 5.4**: Permission plugin integration (optional)
- **SQLite JDBC 3.45**: Database support (shaded)
- **Adventure API**: Text formatting (included in Paper)

## Build Outputs

- `build/libs/EventCoreLib-1.0.0.jar`: Complete plugin with shaded dependencies
