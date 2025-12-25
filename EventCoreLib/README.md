# EventCoreLib

A clean, reusable team management library for Minecraft Paper 1.21+ event plugins.

## Features

- Team management API with interfaces
- Player-to-team assignment system (one team per player)
- Configurable storage (YAML or SQLite)
- LuckPerms integration with RGB/gradient support
- Adventure MiniMessage formatting
- Configurable chat and tab list formatting

## Installation

1. Download the latest release
2. Place `EventCoreLib.jar` in your server's `plugins/` folder
3. Restart the server
4. Configure `plugins/EventCoreLib/config.yml` as needed

## Usage

### Maven Dependency

```xml
<dependency>
    <groupId>dev.smughito</groupId>
    <artifactId>EventCoreLib</artifactId>
    <version>1.0.0</version>
    <scope>provided</scope>
</dependency>
```

### Gradle Dependency

```gradle
dependencies {
    compileOnly 'dev.smughito:EventCoreLib:1.0.0'
}
```

### API Usage

Add `EventCoreLib` to your `plugin.yml`:

```yaml
depend: [EventCoreLib]
```

Access the API from your plugin:

```java
import dev.smughito.eventcorelib.EventCoreLib;
import dev.smughito.eventcorelib.api.TeamManager;
import dev.smughito.eventcorelib.api.Team;
import net.kyori.adventure.text.format.NamedTextColor;

public class MyPlugin extends JavaPlugin {

    private TeamManager teamManager;

    @Override
    public void onEnable() {
        EventCoreLib coreLib = EventCoreLib.getInstance();
        teamManager = coreLib.getTeamManager();

        // Create a team
        Team redTeam = teamManager.createTeam("red", "Red Team", NamedTextColor.RED);
        redTeam.setPrefix("<red>[RED] </red>");

        // Assign player to team
        UUID playerId = player.getUniqueId();
        teamManager.assignPlayerToTeam(playerId, "red");

        // Get player's team
        teamManager.getPlayerTeam(playerId).ifPresent(team -> {
            System.out.println("Player is in: " + team.getDisplayName());
        });
    }
}
```

## API Reference

### TeamManager

```java
Team createTeam(String id, String displayName, TextColor color);
boolean deleteTeam(String id);
Optional<Team> getTeam(String id);
Collection<Team> getAllTeams();
Optional<Team> getPlayerTeam(UUID playerId);
boolean assignPlayerToTeam(UUID playerId, String teamId);
boolean removePlayerFromTeam(UUID playerId);
```

### Team

```java
String getId();
String getDisplayName();
void setDisplayName(String displayName);
TextColor getColor();
void setColor(TextColor color);
String getPrefix();
void setPrefix(String prefix);
String getSuffix();
void setSuffix(String suffix);
List<UUID> getMembers();
boolean isMember(UUID playerId);
```

### PlayerTeamService

```java
Optional<Team> getPlayerTeam(UUID playerId);
boolean isInTeam(UUID playerId);
boolean isInTeam(UUID playerId, String teamId);
boolean assignToTeam(UUID playerId, String teamId);
boolean removeFromCurrentTeam(UUID playerId);
```

## Configuration

```yaml
storage:
  type: YAML  # YAML or SQLITE
  sqlite-filename: eventcorelib.db

chat:
  enabled: true
  format: "<team_prefix><luckperms_prefix><gray><player_name></gray><luckperms_suffix><team_suffix><white>: <message>"

tablist:
  enabled: true
  format: "<team_prefix><luckperms_prefix><player_name><luckperms_suffix>"
```

## Building

```bash
./gradlew shadowJar
```

Output: `build/libs/EventCoreLib-1.0.0.jar`

## License

MIT License - See LICENSE file for details

## Support

Issues: https://github.com/Smughito/EventCoreLib/issues
