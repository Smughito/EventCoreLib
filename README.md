# EventCoreLib

A lightweight, reusable team management library for Minecraft Paper 1.21.11 event plugins.

## Overview

EventCoreLib is a pure library plugin that provides a clean API for managing teams and player assignments. It does not add any gameplay features, commands, or event logic—it simply provides infrastructure that other plugins can build upon.

## Features

- **Team Management API**: Create, delete, and manage teams with unique IDs, display names, and colors
- **Player Assignment**: Assign players to teams (one player per team at a time)
- **Flexible Storage**: Choose between YAML or SQLite storage backends
- **LuckPerms Integration**: Automatically merge team prefixes with LuckPerms metadata
- **Adventure MiniMessage**: Full support for modern text formatting with RGB colors and gradients
- **Configurable Formatting**: Customizable chat and tab list formatting through config.yml

## Installation

1. Download the latest release JAR file
2. Place it in your server's `plugins/` folder
3. Restart your server
4. Configure the plugin in `plugins/EventCoreLib/config.yml`

## Configuration

```yaml
# Storage type: YAML or SQLITE
storage-type: YAML

# SQLite database file name (used if storage-type is SQLITE)
sqlite-database: eventcorelib.db

# Chat formatting
chat:
  enabled: true
  format: "{team_prefix}{luckperms_prefix}{player}{luckperms_suffix} <dark_gray>»</dark_gray> {message}"

# Tab list formatting
tablist:
  enabled: true
  format: "{team_prefix}{luckperms_prefix}{player}{luckperms_suffix}"

# LuckPerms integration
luckperms:
  enabled: true
  merge-prefix: true
  rgb-support: true
```

## API Usage

### Adding EventCoreLib as a Dependency

**Gradle:**
```gradle
dependencies {
    compileOnly fileTree(dir: 'libs', include: ['EventCoreLib-*.jar'])
}
```

**plugin.yml:**
```yaml
depend:
  - EventCoreLib
```

### Using the API

```java
import com.smughito.eventcorelib.EventCoreLib;
import com.smughito.eventcorelib.api.TeamManager;
import com.smughito.eventcorelib.api.Team;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.plugin.java.JavaPlugin;

public class MyEventPlugin extends JavaPlugin {

    private TeamManager teamManager;

    @Override
    public void onEnable() {
        EventCoreLib coreLib = (EventCoreLib) getServer().getPluginManager().getPlugin("EventCoreLib");
        teamManager = coreLib.getTeamManager();

        // Create a team
        Team redTeam = teamManager.createTeam("red", "Red Team");
        redTeam.setColor(NamedTextColor.RED);
        redTeam.setPrefix("<red>[RED]</red>");

        // Assign a player to the team
        UUID playerUuid = player.getUniqueId();
        teamManager.assignPlayer(playerUuid, "red");

        // Get a player's team
        Optional<Team> team = teamManager.getPlayerTeam(playerUuid);

        // Remove a player from their team
        teamManager.removePlayer(playerUuid);

        // Delete a team
        teamManager.deleteTeam("red");
    }
}
```

## API Documentation

### TeamManager Interface

- `Team createTeam(String id, String displayName)` - Create a new team
- `boolean deleteTeam(String id)` - Delete a team
- `Optional<Team> getTeam(String id)` - Get a team by ID
- `Collection<Team> getAllTeams()` - Get all teams
- `Optional<Team> getPlayerTeam(UUID playerUuid)` - Get a player's current team
- `boolean assignPlayer(UUID playerUuid, String teamId)` - Assign a player to a team
- `boolean removePlayer(UUID playerUuid)` - Remove a player from their team
- `boolean teamExists(String id)` - Check if a team exists
- `void reload()` - Reload from storage
- `void save()` - Save to storage

### Team Interface

- `String getId()` - Get team ID
- `String getDisplayName()` - Get display name
- `void setDisplayName(String displayName)` - Set display name
- `NamedTextColor getColor()` - Get team color
- `void setColor(NamedTextColor color)` - Set team color
- `String getPrefix()` - Get team prefix (MiniMessage format)
- `void setPrefix(String prefix)` - Set team prefix
- `String getSuffix()` - Get team suffix (MiniMessage format)
- `void setSuffix(String suffix)` - Set team suffix
- `Set<UUID> getMembers()` - Get all team members
- `boolean isMember(UUID playerUuid)` - Check if player is a member
- `int getMemberCount()` - Get member count

## Building from Source

```bash
./gradlew build
```

The compiled JAR will be in `build/libs/EventCoreLib-1.0.0.jar`

## Requirements

- Java 21
- Paper 1.21.11 or higher
- LuckPerms (optional, for prefix/suffix integration)

## License

This project is provided as-is for use in Minecraft event servers.

## Support

For issues, questions, or contributions, visit: https://github.com/Smughito/EventCoreLib
