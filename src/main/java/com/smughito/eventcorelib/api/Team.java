package com.smughito.eventcorelib.api;

import net.kyori.adventure.text.format.NamedTextColor;
import java.util.Set;
import java.util.UUID;

/**
 * Represents a team in the event system.
 */
public interface Team {

    /**
     * Gets the unique identifier of this team.
     *
     * @return the team ID
     */
    String getId();

    /**
     * Gets the display name of this team.
     *
     * @return the display name
     */
    String getDisplayName();

    /**
     * Sets the display name of this team.
     *
     * @param displayName the new display name
     */
    void setDisplayName(String displayName);

    /**
     * Gets the color of this team.
     *
     * @return the team color
     */
    NamedTextColor getColor();

    /**
     * Sets the color of this team.
     *
     * @param color the new color
     */
    void setColor(NamedTextColor color);

    /**
     * Gets the prefix displayed before player names.
     *
     * @return the team prefix (MiniMessage format)
     */
    String getPrefix();

    /**
     * Sets the prefix displayed before player names.
     *
     * @param prefix the new prefix (MiniMessage format)
     */
    void setPrefix(String prefix);

    /**
     * Gets the suffix displayed after player names.
     *
     * @return the team suffix (MiniMessage format)
     */
    String getSuffix();

    /**
     * Sets the suffix displayed after player names.
     *
     * @param suffix the new suffix (MiniMessage format)
     */
    void setSuffix(String suffix);

    /**
     * Gets an immutable set of all member UUIDs in this team.
     *
     * @return the set of member UUIDs
     */
    Set<UUID> getMembers();

    /**
     * Checks if a player is a member of this team.
     *
     * @param playerUuid the player UUID
     * @return true if the player is a member
     */
    boolean isMember(UUID playerUuid);

    /**
     * Gets the number of members in this team.
     *
     * @return the member count
     */
    int getMemberCount();
}
