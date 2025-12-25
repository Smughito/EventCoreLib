package dev.smughito.eventcorelib.util;

import dev.smughito.eventcorelib.api.Team;
import dev.smughito.eventcorelib.integration.LuckPermsIntegration;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

import java.util.Optional;

public class ChatFormatter {

    private final LuckPermsIntegration luckPerms;
    private final MiniMessage miniMessage;
    private String chatFormat;
    private boolean enabled;

    public ChatFormatter(LuckPermsIntegration luckPerms) {
        this.luckPerms = luckPerms;
        this.miniMessage = MiniMessage.miniMessage();
        this.chatFormat = "<team_prefix><luckperms_prefix><gray><player_name></gray><luckperms_suffix><team_suffix><white>: <message>";
        this.enabled = true;
    }

    public void setChatFormat(String format) {
        this.chatFormat = format;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Component formatChatMessage(Player player, String message, Optional<Team> team) {
        if (!enabled) {
            return Component.text(player.getName() + ": " + message);
        }

        String teamPrefix = team.map(Team::getPrefix).orElse("");
        String teamSuffix = team.map(Team::getSuffix).orElse("");
        String luckPermsPrefix = luckPerms.isEnabled() ? luckPerms.getPrefix(player.getUniqueId()) : "";
        String luckPermsSuffix = luckPerms.isEnabled() ? luckPerms.getSuffix(player.getUniqueId()) : "";

        String formatted = chatFormat
            .replace("<team_prefix>", teamPrefix)
            .replace("<team_suffix>", teamSuffix)
            .replace("<luckperms_prefix>", luckPermsPrefix)
            .replace("<luckperms_suffix>", luckPermsSuffix)
            .replace("<player_name>", player.getName())
            .replace("<message>", message);

        return miniMessage.deserialize(formatted);
    }
}
