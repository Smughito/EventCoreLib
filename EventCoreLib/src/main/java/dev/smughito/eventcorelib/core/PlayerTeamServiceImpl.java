package dev.smughito.eventcorelib.core;

import dev.smughito.eventcorelib.api.PlayerTeamService;
import dev.smughito.eventcorelib.api.Team;
import dev.smughito.eventcorelib.api.TeamManager;

import java.util.Optional;
import java.util.UUID;

public class PlayerTeamServiceImpl implements PlayerTeamService {

    private final TeamManager teamManager;

    public PlayerTeamServiceImpl(TeamManager teamManager) {
        this.teamManager = teamManager;
    }

    @Override
    public Optional<Team> getPlayerTeam(UUID playerId) {
        return teamManager.getPlayerTeam(playerId);
    }

    @Override
    public boolean isInTeam(UUID playerId) {
        return teamManager.getPlayerTeam(playerId).isPresent();
    }

    @Override
    public boolean isInTeam(UUID playerId, String teamId) {
        Optional<Team> team = teamManager.getPlayerTeam(playerId);
        return team.isPresent() && team.get().getId().equals(teamId);
    }

    @Override
    public boolean assignToTeam(UUID playerId, String teamId) {
        return teamManager.assignPlayerToTeam(playerId, teamId);
    }

    @Override
    public boolean removeFromCurrentTeam(UUID playerId) {
        return teamManager.removePlayerFromTeam(playerId);
    }
}
