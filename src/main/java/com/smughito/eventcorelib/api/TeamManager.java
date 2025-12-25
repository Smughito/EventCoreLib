package com.smughito.eventcorelib.api;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

/**
 * Manages teams and player assignments.
 */
public interface TeamManager {

    /**
     * Creates a new team with the specified ID and display name.
     *
     * @param id the unique team ID
     * @param displayName the display name
     * @return the created team
     * @throws IllegalArgumentException if a team with this ID already exists
     */
    Team createTeam(String id, String displayName);

    /**
     * Deletes a team by its ID.
     *
     * @param id the team ID
     * @return true if the team was deleted, false if it didn't exist
     */
    boolean deleteTeam(String id);

    /**
     * Gets a team by its ID.
     *
     * @param id the team ID
     * @return an Optional containing the team, or empty if not found
     */
    Optional<Team> getTeam(String id);

    /**
     * Gets all teams.
     *
     * @return an immutable collection of all teams
     */
    Collection<Team> getAllTeams();

    /**
     * Gets the team that a player is currently assigned to.
     *
     * @param playerUuid the player UUID
     * @return an Optional containing the player's team, or empty if not assigned
     */
    Optional<Team> getPlayerTeam(UUID playerUuid);

    /**
     * Assigns a player to a team.
     * If the player is already in another team, they will be removed from it first.
     *
     * @param playerUuid the player UUID
     * @param teamId the team ID
     * @return true if the assignment was successful
     * @throws IllegalArgumentException if the team doesn't exist
     */
    boolean assignPlayer(UUID playerUuid, String teamId);

    /**
     * Removes a player from their current team.
     *
     * @param playerUuid the player UUID
     * @return true if the player was removed from a team, false if they weren't in any team
     */
    boolean removePlayer(UUID playerUuid);

    /**
     * Checks if a team with the specified ID exists.
     *
     * @param id the team ID
     * @return true if the team exists
     */
    boolean teamExists(String id);

    /**
     * Reloads teams and assignments from storage.
     */
    void reload();

    /**
     * Saves all teams and assignments to storage.
     */
    void save();
}
