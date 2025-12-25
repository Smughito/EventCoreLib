# Example Usage

This document demonstrates how to use EventCoreLib in your own plugin.

## Setup

### 1. Add Dependency

Add the EventCoreLib JAR to your plugin's `libs/` folder and configure your build system:

**build.gradle:**
```gradle
dependencies {
    compileOnly fileTree(dir: 'libs', include: ['EventCoreLib-*.jar'])
}
```

### 2. Declare Dependency

**plugin.yml:**
```yaml
name: MyEventPlugin
version: 1.0.0
main: com.example.myeventplugin.MyEventPlugin
api-version: '1.21'

depend:
  - EventCoreLib
```

## Basic Usage

### Initialize the API

```java
package com.example.myeventplugin;

import com.smughito.eventcorelib.EventCoreLib;
import com.smughito.eventcorelib.api.TeamManager;
import org.bukkit.plugin.java.JavaPlugin;

public class MyEventPlugin extends JavaPlugin {

    private EventCoreLib coreLib;
    private TeamManager teamManager;

    @Override
    public void onEnable() {
        // Get EventCoreLib instance
        coreLib = (EventCoreLib) getServer().getPluginManager().getPlugin("EventCoreLib");

        if (coreLib == null) {
            getLogger().severe("EventCoreLib not found! Disabling plugin.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Get the team manager
        teamManager = coreLib.getTeamManager();

        getLogger().info("Successfully hooked into EventCoreLib!");
    }
}
```

## Creating Teams

```java
import com.smughito.eventcorelib.api.Team;
import net.kyori.adventure.text.format.NamedTextColor;

public void createGameTeams() {
    // Create Red Team
    Team redTeam = teamManager.createTeam("red", "Red Team");
    redTeam.setColor(NamedTextColor.RED);
    redTeam.setPrefix("<red>[RED]</red> ");

    // Create Blue Team
    Team blueTeam = teamManager.createTeam("blue", "Blue Team");
    blueTeam.setColor(NamedTextColor.BLUE);
    blueTeam.setPrefix("<blue>[BLUE]</blue> ");

    // Create Green Team
    Team greenTeam = teamManager.createTeam("green", "Green Team");
    greenTeam.setColor(NamedTextColor.GREEN);
    greenTeam.setPrefix("<green>[GREEN]</green> ");

    getLogger().info("Created 3 teams for the event!");
}
```

## Managing Players

### Assign Players to Teams

```java
import org.bukkit.entity.Player;
import java.util.UUID;

public void assignPlayerToTeam(Player player, String teamId) {
    try {
        boolean success = teamManager.assignPlayer(player.getUniqueId(), teamId);

        if (success) {
            player.sendMessage("You've been assigned to team: " + teamId);
        }
    } catch (IllegalArgumentException e) {
        getLogger().warning("Team doesn't exist: " + teamId);
        player.sendMessage("Failed to assign you to a team.");
    }
}
```

### Auto-Balance Teams

```java
import org.bukkit.entity.Player;
import java.util.*;

public void autoBalancePlayers(Collection<Player> players) {
    List<Team> teams = new ArrayList<>(teamManager.getAllTeams());

    if (teams.isEmpty()) {
        getLogger().warning("No teams exist for auto-balance!");
        return;
    }

    int teamIndex = 0;
    for (Player player : players) {
        Team team = teams.get(teamIndex);
        teamManager.assignPlayer(player.getUniqueId(), team.getId());

        player.sendMessage("Assigned to: " + team.getDisplayName());

        // Round-robin assignment
        teamIndex = (teamIndex + 1) % teams.size();
    }

    getLogger().info("Auto-balanced " + players.size() + " players across " + teams.size() + " teams");
}
```

### Remove Player from Team

```java
public void removePlayerFromTeam(UUID playerUuid) {
    boolean removed = teamManager.removePlayer(playerUuid);

    if (removed) {
        getLogger().info("Removed player from their team");
    } else {
        getLogger().info("Player was not in any team");
    }
}
```

## Querying Teams

### Get Player's Team

```java
import java.util.Optional;

public void checkPlayerTeam(Player player) {
    Optional<Team> teamOpt = teamManager.getPlayerTeam(player.getUniqueId());

    if (teamOpt.isPresent()) {
        Team team = teamOpt.get();
        player.sendMessage("You are in: " + team.getDisplayName());
        player.sendMessage("Team color: " + team.getColor());
        player.sendMessage("Members: " + team.getMemberCount());
    } else {
        player.sendMessage("You are not in any team.");
    }
}
```

### List All Teams

```java
public void listAllTeams(Player player) {
    Collection<Team> teams = teamManager.getAllTeams();

    player.sendMessage("=== Teams ===");
    for (Team team : teams) {
        player.sendMessage(String.format("%s - %s (%d members)",
            team.getId(),
            team.getDisplayName(),
            team.getMemberCount()));
    }
}
```

### Get Team by ID

```java
public void showTeamInfo(Player player, String teamId) {
    Optional<Team> teamOpt = teamManager.getTeam(teamId);

    if (teamOpt.isEmpty()) {
        player.sendMessage("Team not found: " + teamId);
        return;
    }

    Team team = teamOpt.get();
    player.sendMessage("=== " + team.getDisplayName() + " ===");
    player.sendMessage("ID: " + team.getId());
    player.sendMessage("Color: " + team.getColor());
    player.sendMessage("Members: " + team.getMemberCount());
    player.sendMessage("Prefix: " + team.getPrefix());
}
```

## Advanced Usage

### Team-Based Event System

```java
public class CaptureTheFlagGame {
    private final TeamManager teamManager;
    private final Map<String, Integer> teamScores;

    public CaptureTheFlagGame(TeamManager teamManager) {
        this.teamManager = teamManager;
        this.teamScores = new HashMap<>();
    }

    public void startGame() {
        // Initialize scores for all teams
        for (Team team : teamManager.getAllTeams()) {
            teamScores.put(team.getId(), 0);
        }
    }

    public void onFlagCapture(Player player) {
        Optional<Team> teamOpt = teamManager.getPlayerTeam(player.getUniqueId());

        if (teamOpt.isEmpty()) {
            player.sendMessage("You're not in a team!");
            return;
        }

        Team team = teamOpt.get();
        int newScore = teamScores.getOrDefault(team.getId(), 0) + 1;
        teamScores.put(team.getId(), newScore);

        // Notify all team members
        notifyTeam(team, player.getName() + " captured a flag! Score: " + newScore);
    }

    private void notifyTeam(Team team, String message) {
        for (UUID memberUuid : team.getMembers()) {
            Player member = Bukkit.getPlayer(memberUuid);
            if (member != null && member.isOnline()) {
                member.sendMessage(message);
            }
        }
    }

    public Team getWinningTeam() {
        return teamScores.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(entry -> teamManager.getTeam(entry.getKey()))
            .flatMap(opt -> opt)
            .orElse(null);
    }
}
```

### Clean Up After Event

```java
public void cleanupEvent() {
    // Remove all players from teams
    for (Team team : teamManager.getAllTeams()) {
        for (UUID member : team.getMembers()) {
            teamManager.removePlayer(member);
        }
    }

    // Delete all teams
    for (Team team : new ArrayList<>(teamManager.getAllTeams())) {
        teamManager.deleteTeam(team.getId());
    }

    // Save changes
    teamManager.save();

    getLogger().info("Event cleanup complete!");
}
```

## Using Formatters

### Chat Formatting

```java
import com.smughito.eventcorelib.util.ChatFormatter;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ChatListener implements Listener {
    private final EventCoreLib coreLib;
    private final TeamManager teamManager;

    public ChatListener(EventCoreLib coreLib) {
        this.coreLib = coreLib;
        this.teamManager = coreLib.getTeamManager();
    }

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        String message = ((TextComponent) event.message()).content();

        Optional<Team> teamOpt = teamManager.getPlayerTeam(player.getUniqueId());

        ChatFormatter formatter = coreLib.getChatFormatter();
        Component formatted;

        if (teamOpt.isPresent()) {
            formatted = formatter.formatChatMessage(player, teamOpt.get(), message);
        } else {
            formatted = formatter.formatChatMessage(player, message);
        }

        event.message(formatted);
    }
}
```

### Tab List Formatting

```java
import com.smughito.eventcorelib.util.TabListFormatter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {
    private final EventCoreLib coreLib;
    private final TeamManager teamManager;

    public JoinListener(EventCoreLib coreLib) {
        this.coreLib = coreLib;
        this.teamManager = coreLib.getTeamManager();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        Optional<Team> teamOpt = teamManager.getPlayerTeam(player.getUniqueId());
        TabListFormatter formatter = coreLib.getTabListFormatter();

        if (teamOpt.isPresent()) {
            Component displayName = formatter.formatTabListName(player, teamOpt.get());
            player.playerListName(displayName);
        }
    }
}
```

## Error Handling

Always handle potential errors:

```java
public void safeTeamOperation(Player player, String teamId) {
    // Check if team exists
    if (!teamManager.teamExists(teamId)) {
        player.sendMessage("Team doesn't exist!");
        return;
    }

    try {
        // Assign player
        teamManager.assignPlayer(player.getUniqueId(), teamId);
        player.sendMessage("Successfully joined team!");

    } catch (IllegalArgumentException e) {
        // This shouldn't happen if we checked teamExists, but handle it anyway
        getLogger().severe("Failed to assign player: " + e.getMessage());
        player.sendMessage("An error occurred. Please try again.");
    }
}
```

## Persistence

Teams and assignments are automatically saved, but you can manually trigger a save:

```java
@Override
public void onDisable() {
    if (teamManager != null) {
        teamManager.save();
        getLogger().info("Teams saved successfully!");
    }
}
```

To reload from disk:

```java
public void reloadTeams() {
    teamManager.reload();
    getLogger().info("Teams reloaded from storage!");
}
```
