package com.smughito.eventcorelib.integration;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.cacheddata.CachedMetaData;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

public class LuckPermsIntegration {

    private LuckPerms luckPerms;
    private boolean enabled;

    public LuckPermsIntegration() {
        this.enabled = false;
    }

    public void initialize() {
        try {
            this.luckPerms = LuckPermsProvider.get();
            this.enabled = true;
        } catch (IllegalStateException e) {
            this.enabled = false;
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getPrefix(Player player) {
        if (!enabled) {
            return "";
        }

        return getPrefix(player.getUniqueId()).orElse("");
    }

    public String getPrefix(UUID uuid) {
        if (!enabled) {
            return "";
        }

        return getMetaData(uuid)
                .map(CachedMetaData::getPrefix)
                .orElse("");
    }

    public String getSuffix(Player player) {
        if (!enabled) {
            return "";
        }

        return getSuffix(player.getUniqueId()).orElse("");
    }

    public String getSuffix(UUID uuid) {
        if (!enabled) {
            return "";
        }

        return getMetaData(uuid)
                .map(CachedMetaData::getSuffix)
                .orElse("");
    }

    public Optional<String> getPrefixOptional(UUID uuid) {
        if (!enabled) {
            return Optional.empty();
        }

        return getMetaData(uuid)
                .map(CachedMetaData::getPrefix);
    }

    public Optional<String> getSuffixOptional(UUID uuid) {
        if (!enabled) {
            return Optional.empty();
        }

        return getMetaData(uuid)
                .map(CachedMetaData::getSuffix);
    }

    private Optional<CachedMetaData> getMetaData(UUID uuid) {
        if (!enabled) {
            return Optional.empty();
        }

        User user = luckPerms.getUserManager().getUser(uuid);
        if (user == null) {
            return Optional.empty();
        }

        return Optional.of(user.getCachedData().getMetaData());
    }

    public String mergePrefix(String teamPrefix, String luckPermsPrefix) {
        if (teamPrefix == null) teamPrefix = "";
        if (luckPermsPrefix == null) luckPermsPrefix = "";

        if (teamPrefix.isEmpty()) {
            return luckPermsPrefix;
        }
        if (luckPermsPrefix.isEmpty()) {
            return teamPrefix;
        }

        return teamPrefix + " " + luckPermsPrefix;
    }

    public String mergeSuffix(String teamSuffix, String luckPermsSuffix) {
        if (teamSuffix == null) teamSuffix = "";
        if (luckPermsSuffix == null) luckPermsSuffix = "";

        if (teamSuffix.isEmpty()) {
            return luckPermsSuffix;
        }
        if (luckPermsSuffix.isEmpty()) {
            return teamSuffix;
        }

        return luckPermsSuffix + " " + teamSuffix;
    }
}
