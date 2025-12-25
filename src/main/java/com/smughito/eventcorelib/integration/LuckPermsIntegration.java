package com.smughito.eventcorelib.integration;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.model.user.User;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

public class LuckPermsIntegration {

    private LuckPerms luckPerms;
    private boolean enabled = false;

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

    /* ================= PREFIX ================= */

    public String getPrefix(Player player) {
        return enabled ? getPrefix(player.getUniqueId()) : "";
    }

    public String getPrefix(UUID uuid) {
        return getMetaData(uuid)
                .map(CachedMetaData::getPrefix)
                .orElse("");
    }

    public Optional<String> getPrefixOptional(UUID uuid) {
        return enabled
                ? getMetaData(uuid).map(CachedMetaData::getPrefix)
                : Optional.empty();
    }

    /* ================= SUFFIX ================= */

    public String getSuffix(Player player) {
        return enabled ? getSuffix(player.getUniqueId()) : "";
    }

    public String getSuffix(UUID uuid) {
        return getMetaData(uuid)
                .map(CachedMetaData::getSuffix)
                .orElse("");
    }

    public Optional<String> getSuffixOptional(UUID uuid) {
        return enabled
                ? getMetaData(uuid).map(CachedMetaData::getSuffix)
                : Optional.empty();
    }

    /* ================= METADATA ================= */

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

    /* ================= MERGE ================= */

    public String mergePrefix(String teamPrefix, String luckPermsPrefix) {
        teamPrefix = teamPrefix == null ? "" : teamPrefix;
        luckPermsPrefix = luckPermsPrefix == null ? "" : luckPermsPrefix;

        if (teamPrefix.isEmpty()) return luckPermsPrefix;
        if (luckPermsPrefix.isEmpty()) return teamPrefix;

        return teamPrefix + " " + luckPermsPrefix;
    }

    public String mergeSuffix(String teamSuffix, String luckPermsSuffix) {
        teamSuffix = teamSuffix == null ? "" : teamSuffix;
        luckPermsSuffix = luckPermsSuffix == null ? "" : luckPermsSuffix;

        if (teamSuffix.isEmpty()) return luckPermsSuffix;
        if (luckPermsSuffix.isEmpty()) return teamSuffix;

        return luckPermsSuffix + " " + teamSuffix;
    }
}
