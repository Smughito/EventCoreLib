package com.smughito.eventcorelib;

import com.smughito.eventcorelib.api.EventCoreAPI;
import com.smughito.eventcorelib.api.TeamManager;
import com.smughito.eventcorelib.core.TeamManagerImpl;
import com.smughito.eventcorelib.integration.LuckPermsIntegration;
import com.smughito.eventcorelib.storage.SqliteStorageProvider;
import com.smughito.eventcorelib.storage.StorageProvider;
import com.smughito.eventcorelib.storage.YamlStorageProvider;
import com.smughito.eventcorelib.util.ChatFormatter;
import com.smughito.eventcorelib.util.TabListFormatter;
import org.bukkit.plugin.java.JavaPlugin;

public class EventCoreLib extends JavaPlugin implements EventCoreAPI {

    private TeamManagerImpl teamManager;
    private StorageProvider storageProvider;
    private LuckPermsIntegration luckPermsIntegration;
    private ChatFormatter chatFormatter;
    private TabListFormatter tabListFormatter;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        initializeStorage();
        initializeLuckPerms();
        initializeTeamManager();
        initializeFormatters();

        getLogger().info("EventCoreLib has been enabled successfully!");
        getLogger().info("Storage type: " + getConfig().getString("storage-type", "YAML"));
        getLogger().info("LuckPerms integration: " + (luckPermsIntegration.isEnabled() ? "enabled" : "disabled"));
    }

    @Override
    public void onDisable() {
        if (teamManager != null) {
            teamManager.save();
        }

        if (storageProvider != null) {
            storageProvider.shutdown();
        }

        getLogger().info("EventCoreLib has been disabled.");
    }

    private void initializeStorage() {
        String storageType = getConfig().getString("storage-type", "YAML").toUpperCase();

        switch (storageType) {
            case "SQLITE":
                String databaseName = getConfig().getString("sqlite-database", "eventcorelib.db");
                storageProvider = new SqliteStorageProvider(this, databaseName);
                break;
            case "YAML":
            default:
                storageProvider = new YamlStorageProvider(this);
                break;
        }

        storageProvider.initialize();
    }

    private void initializeLuckPerms() {
        luckPermsIntegration = new LuckPermsIntegration();

        if (getConfig().getBoolean("luckperms.enabled", true)) {
            luckPermsIntegration.initialize();
        }
    }

    private void initializeTeamManager() {
        teamManager = new TeamManagerImpl(storageProvider);
        teamManager.initialize();
    }

    private void initializeFormatters() {
        chatFormatter = new ChatFormatter(getConfig(), luckPermsIntegration);
        tabListFormatter = new TabListFormatter(getConfig(), luckPermsIntegration);
    }

    @Override
    public TeamManager getTeamManager() {
        return teamManager;
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

    public void reloadPlugin() {
        reloadConfig();

        if (chatFormatter != null) {
            chatFormatter.reload();
        }

        if (tabListFormatter != null) {
            tabListFormatter.reload();
        }

        if (teamManager != null) {
            teamManager.reload();
        }

        getLogger().info("EventCoreLib configuration reloaded.");
    }
}
