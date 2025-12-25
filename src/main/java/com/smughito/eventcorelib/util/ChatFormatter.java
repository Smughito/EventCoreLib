package com.smughito.eventcorelib.util;

import com.smughito.eventcorelib.api.Team;
import com.smughito.eventcorelib.integration.LuckPermsIntegration;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class ChatFormatter {

    private final FileConfiguration config;
    private final LuckPermsIntegration luckPerms;
    private final MiniMessage miniMessage;

    private boolean chatEnabled;
    private String chatFormat;
    private boolean mergePrefix;

    public ChatFormatter(FileConfiguration config, LuckPermsIntegration luckPerms) {
        this.config = config;
        this.luckPerms = luckPerms;
        this.miniMessage = MiniMessage.miniMessage();
        reload();
    }

    public void reload() {
        this.chatEnabled = config.getBoolean("chat.enabled", true);
        this.chatFormat = config.getString("chat.format",
                "{team_prefix}{luckperms_prefix}{player}{luckperms_suffix} <dark_gray>»</dark_gray> {message}");
        this.mergePrefix = config.getBoolean("luckperms.merge-prefix", true);
    }

    public boolean isChatEnabled() {
        return chatEnabled;
    }

    public Component formatChatMessage(Player player, Team team, String message) {
        String formatted = chatFormat;

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
        formatted = formatted.replace("{message}", message);

        return miniMessage.deserialize(formatted);
    }

    public Component formatChatMessage(Player player, String message) {
        return formatChatMessage(player, null, message);
    }

    public Component parse(String text) {
        return miniMessage.deserialize(text);
    }

    public String serialize(Component component) {
        return miniMessage.serialize(component);
    }
}
