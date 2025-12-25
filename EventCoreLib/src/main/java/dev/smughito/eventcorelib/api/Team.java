package dev.smughito.eventcorelib.api;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

import java.util.List;
import java.util.UUID;

public interface Team {

    String getId();

    String getDisplayName();

    void setDisplayName(String displayName);

    TextColor getColor();

    void setColor(TextColor color);

    String getPrefix();

    void setPrefix(String prefix);

    String getSuffix();

    void setSuffix(String suffix);

    List<UUID> getMembers();

    boolean isMember(UUID playerId);

    void addMember(UUID playerId);

    void removeMember(UUID playerId);

    int getMemberCount();
}
