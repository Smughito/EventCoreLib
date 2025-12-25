package dev.smughito.eventcorelib.storage;

import dev.smughito.eventcorelib.api.Team;

import java.util.Map;
import java.util.UUID;

public interface StorageProvider {

    void initialize();

    void saveTeam(Team team);

    void deleteTeam(String teamId);

    Map<String, Team> loadTeams();

    void savePlayerTeamAssignment(UUID playerId, String teamId);

    void removePlayerTeamAssignment(UUID playerId);

    Map<UUID, String> loadPlayerTeamAssignments();

    void shutdown();
}
