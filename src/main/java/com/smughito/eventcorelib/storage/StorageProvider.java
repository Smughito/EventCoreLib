package com.smughito.eventcorelib.storage;

import com.smughito.eventcorelib.core.TeamImpl;

import java.util.Map;
import java.util.UUID;

public interface StorageProvider {

    void initialize();

    void shutdown();

    Map<String, TeamImpl> loadTeams();

    void saveTeam(TeamImpl team);

    void deleteTeam(String teamId);

    Map<UUID, String> loadPlayerAssignments();

    void savePlayerAssignment(UUID playerUuid, String teamId);

    void removePlayerAssignment(UUID playerUuid);
}
