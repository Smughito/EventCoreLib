package dev.smughito.eventcorelib.util;

import dev.smughito.eventcorelib.api.Team;
import dev.smughito.eventcorelib.integration.LuckPermsIntegration;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

import java.util.Optional;

public class TabListFormatter {

    private final LuckPermsIntegration luckPerms;
    private final MiniMessage miniMessage;
    private String tabListFormat;
    private boolean enabled;

    public TabListFormatter(LuckPermsIntegration luckPerms) {
        this.luckPerms = luckPerms;
        this.miniMessage = MiniMessage.miniMessage();
        this.tabListFormat = "<team_prefix><luckperms_prefix><player_name><luckperms_suffix>";
        this.enabled = true;
    }

    public void setTabListFormat(String format) {
        this.tabListFormat = format;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Component formatPlayerName(Player player, Optional<Team> team) {
        if (!enabled) {
            return Component.text(player.getName());
        }

        String teamPrefix = team.map(Team::getPrefix).orElse("");
        String luckPermsPrefix = luckPerms.isEnabled() ? luckPerms.getPrefix(player.getUniqueId()) : "";
        String luckPermsSuffix = luckPerms.isEnabled() ? luckPerms.getSuffix(player.getUniqueId()) : "";

        String formatted = tabListFormat
            .replace("<team_prefix>", teamPrefix)
            .replace("<luckperms_prefix>", luckPermsPrefix)
            .replace("<luckperms_suffix>", luckPermsSuffix)
            .replace("<player_name>", player.getName());

        return miniMessage.deserialize(formatted);
    }
}
