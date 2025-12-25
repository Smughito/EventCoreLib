package dev.smughito.eventcorelib.api;

import net.kyori.adventure.text.format.TextColor;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface TeamManager {

    Team createTeam(String id, String displayName, TextColor color);

    boolean deleteTeam(String id);

    Optional<Team> getTeam(String id);

    Collection<Team> getAllTeams();

    Optional<Team> getPlayerTeam(UUID playerId);

    boolean assignPlayerToTeam(UUID playerId, String teamId);

    boolean removePlayerFromTeam(UUID playerId);

    void saveAll();

    void loadAll();
}
