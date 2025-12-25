package com.smughito.eventcorelib.core;

import com.smughito.eventcorelib.api.Team;
import com.smughito.eventcorelib.api.TeamManager;
import com.smughito.eventcorelib.storage.StorageProvider;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TeamManagerImpl implements TeamManager {

    private final StorageProvider storageProvider;
    private final Map<String, TeamImpl> teams;
    private final Map<UUID, String> playerTeams;

    public TeamManagerImpl(StorageProvider storageProvider) {
        this.storageProvider = storageProvider;
        this.teams = new ConcurrentHashMap<>();
        this.playerTeams = new ConcurrentHashMap<>();
    }

    public void initialize() {
        reload();
    }

    @Override
    public Team createTeam(String id, String displayName) {
        if (teams.containsKey(id)) {
            throw new IllegalArgumentException("Team with ID '" + id + "' already exists");
        }

        TeamImpl team = new TeamImpl(id, displayName);
        teams.put(id, team);
        storageProvider.saveTeam(team);

        return team;
    }

    @Override
    public boolean deleteTeam(String id) {
        TeamImpl team = teams.remove(id);
        if (team == null) {
            return false;
        }

        for (UUID member : team.getMembers()) {
            playerTeams.remove(member);
        }

        storageProvider.deleteTeam(id);
        return true;
    }

    @Override
    public Optional<Team> getTeam(String id) {
        return Optional.ofNullable(teams.get(id));
    }

    @Override
    public Collection<Team> getAllTeams() {
        return Collections.unmodifiableCollection(teams.values());
    }

    @Override
    public Optional<Team> getPlayerTeam(UUID playerUuid) {
        String teamId = playerTeams.get(playerUuid);
        if (teamId == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(teams.get(teamId));
    }

    @Override
    public boolean assignPlayer(UUID playerUuid, String teamId) {
        TeamImpl team = teams.get(teamId);
        if (team == null) {
            throw new IllegalArgumentException("Team with ID '" + teamId + "' does not exist");
        }

        String currentTeamId = playerTeams.get(playerUuid);
        if (currentTeamId != null) {
            TeamImpl currentTeam = teams.get(currentTeamId);
            if (currentTeam != null) {
                currentTeam.removeMember(playerUuid);
            }
        }

        team.addMember(playerUuid);
        playerTeams.put(playerUuid, teamId);
        storageProvider.savePlayerAssignment(playerUuid, teamId);

        return true;
    }

    @Override
    public boolean removePlayer(UUID playerUuid) {
        String teamId = playerTeams.remove(playerUuid);
        if (teamId == null) {
            return false;
        }

        TeamImpl team = teams.get(teamId);
        if (team != null) {
            team.removeMember(playerUuid);
        }

        storageProvider.removePlayerAssignment(playerUuid);
        return true;
    }

    @Override
    public boolean teamExists(String id) {
        return teams.containsKey(id);
    }

    @Override
    public void reload() {
        teams.clear();
        playerTeams.clear();

        Map<String, TeamImpl> loadedTeams = storageProvider.loadTeams();
        teams.putAll(loadedTeams);

        Map<UUID, String> loadedAssignments = storageProvider.loadPlayerAssignments();
        playerTeams.putAll(loadedAssignments);
    }

    @Override
    public void save() {
        for (TeamImpl team : teams.values()) {
            storageProvider.saveTeam(team);
        }

        for (Map.Entry<UUID, String> entry : playerTeams.entrySet()) {
            storageProvider.savePlayerAssignment(entry.getKey(), entry.getValue());
        }
    }

    public TeamImpl getTeamImpl(String id) {
        return teams.get(id);
    }
}
