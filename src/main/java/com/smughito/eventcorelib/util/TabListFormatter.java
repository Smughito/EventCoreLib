package com.smughito.eventcorelib.util;

import com.smughito.eventcorelib.api.Team;
import com.smughito.eventcorelib.integration.LuckPermsIntegration;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class TabListFormatter {

    private final FileConfiguration config;
    private final LuckPermsIntegration luckPerms;
    private final MiniMessage miniMessage;

    private boolean tabListEnabled;
    private String tabListFormat;
    private boolean mergePrefix;

    public TabListFormatter(FileConfiguration config, LuckPermsIntegration luckPerms) {
        this.config = config;
        this.luckPerms = luckPerms;
        this.miniMessage = MiniMessage.miniMessage();
        reload();
    }

    public void reload() {
        this.tabListEnabled = config.getBoolean("tablist.enabled", true);
        this.tabListFormat = config.getString("tablist.format",
                "{team_prefix}{luckperms_prefix}{player}{luckperms_suffix}");
        this.mergePrefix = config.getBoolean("luckperms.merge-prefix", true);
    }

    public boolean isTabListEnabled() {
        return tabListEnabled;
    }

    public Component formatTabListName(Player player, Team team) {
        String formatted = tabListFormat;

        String teamPrefix = team != null ? team.getPrefix() : "";
        String teamSuffix = team != null ? team.getSuffix() : "";

        String luckPermsPrefix = "";
        String luckPermsSuffix = "";

        if (luckPerms.isEnabled()) {
            luckPermsPrefix = luckPerms.getPrefix(player);
            luckPermsSuffix = luckPerms.getSuffix(player);

            if (luckPermsPrefix == null) luckPermsPrefix = "";
            if (luckPermsSuffix == null) luckPermsSuffix = "";
        }

        if (mergePrefix && !teamPrefix.isEmpty() && !luckPermsPrefix.isEmpty()) {
            teamPrefix = teamPrefix + " ";
        }

        formatted = formatted.replace("{team_prefix}", teamPrefix);
        formatted = formatted.replace("{team_suffix}", teamSuffix);
        formatted = formatted.replace("{luckperms_prefix}", luckPermsPrefix);
        formatted = formatted.replace("{luckperms_suffix}", luckPermsSuffix);
        formatted = formatted.replace("{player}", player.getName());

        return miniMessage.deserialize(formatted);
    }

    public Component formatTabListName(Player player) {
        return formatTabListName(player, null);
    }

    public Component parse(String text) {
        return miniMessage.deserialize(text);
    }

    public String serialize(Component component) {
        return miniMessage.serialize(component);
    }
}
