package dev.smughito.eventcorelib.integration;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.MetaNode;
import org.bukkit.Bukkit;

import java.util.UUID;

public class LuckPermsIntegration {

    private LuckPerms luckPerms;
    private boolean enabled;

    public LuckPermsIntegration() {
        this.enabled = false;
    }

    public void initialize() {
        if (Bukkit.getPluginManager().getPlugin("LuckPerms") != null) {
            try {
                luckPerms = LuckPermsProvider.get();
                enabled = true;
            } catch (IllegalStateException e) {
                enabled = false;
            }
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getPrefix(UUID playerId) {
        if (!enabled) {
            return "";
        }

        User user = luckPerms.getUserManager().getUser(playerId);
        if (user == null) {
            return "";
        }

        String prefix = user.getCachedData().getMetaData().getPrefix();
        return prefix != null ? prefix : "";
    }

    public String getSuffix(UUID playerId) {
        if (!enabled) {
            return "";
        }

        User user = luckPerms.getUserManager().getUser(playerId);
        if (user == null) {
            return "";
        }

        String suffix = user.getCachedData().getMetaData().getSuffix();
        return suffix != null ? suffix : "";
    }

    public String getMetaValue(UUID playerId, String key) {
        if (!enabled) {
            return null;
        }

        User user = luckPerms.getUserManager().getUser(playerId);
        if (user == null) {
            return null;
        }

        return user.getCachedData().getMetaData().getMetaValue(key);
    }
}
