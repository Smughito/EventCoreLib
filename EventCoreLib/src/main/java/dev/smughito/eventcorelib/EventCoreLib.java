package dev.smughito.eventcorelib;

import dev.smughito.eventcorelib.api.PlayerTeamService;
import dev.smughito.eventcorelib.api.TeamManager;
import dev.smughito.eventcorelib.core.PlayerTeamServiceImpl;
import dev.smughito.eventcorelib.core.TeamManagerImpl;
import dev.smughito.eventcorelib.integration.LuckPermsIntegration;
import dev.smughito.eventcorelib.storage.SQLiteStorageProvider;
import dev.smughito.eventcorelib.storage.StorageProvider;
import dev.smughito.eventcorelib.storage.YamlStorageProvider;
import dev.smughito.eventcorelib.util.ChatFormatter;
import dev.smughito.eventcorelib.util.TabListFormatter;
import org.bukkit.plugin.java.JavaPlugin;

public class EventCoreLib extends JavaPlugin {

    private TeamManager teamManager;
    private PlayerTeamService playerTeamService;
    private StorageProvider storageProvider;
    private LuckPermsIntegration luckPermsIntegration;
    private ChatFormatter chatFormatter;
    private TabListFormatter tabListFormatter;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        initializeStorage();

        teamManager = new TeamManagerImpl(storageProvider);
        teamManager.loadAll();

        playerTeamService = new PlayerTeamServiceImpl(teamManager);

        luckPermsIntegration = new LuckPermsIntegration();
        luckPermsIntegration.initialize();

        chatFormatter = new ChatFormatter(luckPermsIntegration);
        chatFormatter.setEnabled(getConfig().getBoolean("chat.enabled", true));
        chatFormatter.setChatFormat(getConfig().getString("chat.format",
            "<team_prefix><luckperms_prefix><gray><player_name></gray><luckperms_suffix><team_suffix><white>: <message>"));

        tabListFormatter = new TabListFormatter(luckPermsIntegration);
        tabListFormatter.setEnabled(getConfig().getBoolean("tablist.enabled", true));
        tabListFormatter.setTabListFormat(getConfig().getString("tablist.format",
            "<team_prefix><luckperms_prefix><player_name><luckperms_suffix>"));

        getLogger().info("EventCoreLib enabled successfully!");
        getLogger().info("Storage type: " + getConfig().getString("storage.type", "YAML"));
        getLogger().info("LuckPerms integration: " + (luckPermsIntegration.isEnabled() ? "enabled" : "disabled"));
    }

    @Override
    public void onDisable() {
        if (teamManager != null) {
            teamManager.saveAll();
        }

        if (storageProvider != null) {
            storageProvider.shutdown();
        }

        getLogger().info("EventCoreLib disabled successfully!");
    }

    private void initializeStorage() {
        String storageType = getConfig().getString("storage.type", "YAML").toUpperCase();

        switch (storageType) {
            case "SQLITE":
                String filename = getConfig().getString("storage.sqlite-filename", "eventcorelib.db");
                storageProvider = new SQLiteStorageProvider(getDataFolder(), filename);
                break;
            case "YAML":
            default:
                storageProvider = new YamlStorageProvider(getDataFolder());
                break;
        }

        storageProvider.initialize();
    }

    public TeamManager getTeamManager() {
        return teamManager;
    }

    public PlayerTeamService getPlayerTeamService() {
        return playerTeamService;
    }

    public LuckPermsIntegration getLuckPermsIntegration() {
        return luckPermsIntegration;
    }

    public ChatFormatter getChatFormatter() {
        return chatFormatter;
    }

    public TabListFormatter getTabListFormatter() {
        return tabListFormatter;
    }

    public static EventCoreLib getInstance() {
        return getPlugin(EventCoreLib.class);
    }
}
