package com.smughito.eventcorelib.core;

import com.smughito.eventcorelib.api.Team;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.*;

public class TeamImpl implements Team {

    private final String id;
    private String displayName;
    private NamedTextColor color;
    private String prefix;
    private String suffix;
    private final Set<UUID> members;

    public TeamImpl(String id, String displayName) {
        this.id = id;
        this.displayName = displayName;
        this.color = NamedTextColor.WHITE;
        this.prefix = "";
        this.suffix = "";
        this.members = new HashSet<>();
    }

    public TeamImpl(String id, String displayName, NamedTextColor color, String prefix, String suffix, Set<UUID> members) {
        this.id = id;
        this.displayName = displayName;
        this.color = color;
        this.prefix = prefix;
        this.suffix = suffix;
        this.members = new HashSet<>(members);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public NamedTextColor getColor() {
        return color;
    }

    @Override
    public void setColor(NamedTextColor color) {
        this.color = color;
    }

    @Override
    public String getPrefix() {
        return prefix;
    }

    @Override
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public String getSuffix() {
        return suffix;
    }

    @Override
    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    @Override
    public Set<UUID> getMembers() {
        return Collections.unmodifiableSet(members);
    }

    @Override
    public boolean isMember(UUID playerUuid) {
        return members.contains(playerUuid);
    }

    @Override
    public int getMemberCount() {
        return members.size();
    }

    public void addMember(UUID playerUuid) {
        members.add(playerUuid);
    }

    public void removeMember(UUID playerUuid) {
        members.remove(playerUuid);
    }

    public void clearMembers() {
        members.clear();
    }
}
