package dev.smughito.eventcorelib.core;

import dev.smughito.eventcorelib.api.Team;
import dev.smughito.eventcorelib.api.TeamManager;
import dev.smughito.eventcorelib.storage.StorageProvider;
import net.kyori.adventure.text.format.TextColor;

import java.util.*;

public class TeamManagerImpl implements TeamManager {

    private final Map<String, Team> teams;
    private final Map<UUID, String> playerTeams;
    private final StorageProvider storageProvider;

    public TeamManagerImpl(StorageProvider storageProvider) {
        this.teams = new HashMap<>();
        this.playerTeams = new HashMap<>();
        this.storageProvider = storageProvider;
    }

    @Override
    public Team createTeam(String id, String displayName, TextColor color) {
        if (teams.containsKey(id)) {
            throw new IllegalArgumentException("Team with ID " + id + " already exists");
        }

        Team team = new TeamImpl(id, displayName, color);
        teams.put(id, team);
        storageProvider.saveTeam(team);
        return team;
    }

    @Override
    public boolean deleteTeam(String id) {
        Team team = teams.remove(id);
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
        return new ArrayList<>(teams.values());
    }

    @Override
    public Optional<Team> getPlayerTeam(UUID playerId) {
        String teamId = playerTeams.get(playerId);
        if (teamId == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(teams.get(teamId));
    }

    @Override
    public boolean assignPlayerToTeam(UUID playerId, String teamId) {
        Team team = teams.get(teamId);
        if (team == null) {
            return false;
        }

        String currentTeamId = playerTeams.get(playerId);
        if (currentTeamId != null) {
            Team currentTeam = teams.get(currentTeamId);
            if (currentTeam != null) {
                currentTeam.removeMember(playerId);
                storageProvider.saveTeam(currentTeam);
            }
        }

        team.addMember(playerId);
        playerTeams.put(playerId, teamId);

        storageProvider.saveTeam(team);
        storageProvider.savePlayerTeamAssignment(playerId, teamId);

        return true;
    }

    @Override
    public boolean removePlayerFromTeam(UUID playerId) {
        String teamId = playerTeams.remove(playerId);
        if (teamId == null) {
            return false;
        }

        Team team = teams.get(teamId);
        if (team != null) {
            team.removeMember(playerId);
            storageProvider.saveTeam(team);
        }

        storageProvider.removePlayerTeamAssignment(playerId);
        return true;
    }

    @Override
    public void saveAll() {
        for (Team team : teams.values()) {
            storageProvider.saveTeam(team);
        }

        for (Map.Entry<UUID, String> entry : playerTeams.entrySet()) {
            storageProvider.savePlayerTeamAssignment(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void loadAll() {
        teams.clear();
        playerTeams.clear();

        Map<String, Team> loadedTeams = storageProvider.loadTeams();
        teams.putAll(loadedTeams);

        Map<UUID, String> loadedAssignments = storageProvider.loadPlayerTeamAssignments();
        playerTeams.putAll(loadedAssignments);
    }

    public Map<String, Team> getTeamsMap() {
        return teams;
    }

    public Map<UUID, String> getPlayerTeamsMap() {
        return playerTeams;
    }
}
