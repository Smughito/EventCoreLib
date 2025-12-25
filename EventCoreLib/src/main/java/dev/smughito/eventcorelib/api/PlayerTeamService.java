package dev.smughito.eventcorelib.api;

import java.util.Optional;
import java.util.UUID;

public interface PlayerTeamService {

    Optional<Team> getPlayerTeam(UUID playerId);

    boolean isInTeam(UUID playerId);

    boolean isInTeam(UUID playerId, String teamId);

    boolean assignToTeam(UUID playerId, String teamId);

    boolean removeFromCurrentTeam(UUID playerId);
}
