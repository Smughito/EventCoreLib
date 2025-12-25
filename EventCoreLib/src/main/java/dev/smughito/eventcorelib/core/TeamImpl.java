package dev.smughito.eventcorelib.core;

import dev.smughito.eventcorelib.api.Team;
import net.kyori.adventure.text.format.TextColor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TeamImpl implements Team {

    private final String id;
    private String displayName;
    private TextColor color;
    private String prefix;
    private String suffix;
    private final List<UUID> members;

    public TeamImpl(String id, String displayName, TextColor color) {
        this.id = id;
        this.displayName = displayName;
        this.color = color;
        this.prefix = "";
        this.suffix = "";
        this.members = new ArrayList<>();
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
    public TextColor getColor() {
        return color;
    }

    @Override
    public void setColor(TextColor color) {
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
    public List<UUID> getMembers() {
        return new ArrayList<>(members);
    }

    @Override
    public boolean isMember(UUID playerId) {
        return members.contains(playerId);
    }

    @Override
    public void addMember(UUID playerId) {
        if (!members.contains(playerId)) {
            members.add(playerId);
        }
    }

    @Override
    public void removeMember(UUID playerId) {
        members.remove(playerId);
    }

    @Override
    public int getMemberCount() {
        return members.size();
    }
}
