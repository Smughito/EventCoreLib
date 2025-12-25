package com.smughito.eventcorelib.storage;

import com.smughito.eventcorelib.core.TeamImpl;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class YamlStorageProvider implements StorageProvider {

    private final Plugin plugin;
    private File teamsFile;
    private YamlConfiguration teamsConfig;
    private File assignmentsFile;
    private YamlConfiguration assignmentsConfig;

    public YamlStorageProvider(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void initialize() {
        teamsFile = new File(plugin.getDataFolder(), "teams.yml");
        assignmentsFile = new File(plugin.getDataFolder(), "assignments.yml");

        if (!teamsFile.exists()) {
            try {
                teamsFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Failed to create teams.yml: " + e.getMessage());
            }
        }

        if (!assignmentsFile.exists()) {
            try {
                assignmentsFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Failed to create assignments.yml: " + e.getMessage());
            }
        }

        teamsConfig = YamlConfiguration.loadConfiguration(teamsFile);
        assignmentsConfig = YamlConfiguration.loadConfiguration(assignmentsFile);
    }

    @Override
    public void shutdown() {
        saveConfigs();
    }

    @Override
    public Map<String, TeamImpl> loadTeams() {
        Map<String, TeamImpl> teams = new HashMap<>();

        ConfigurationSection teamsSection = teamsConfig.getConfigurationSection("teams");
        if (teamsSection == null) {
            return teams;
        }

        for (String teamId : teamsSection.getKeys(false)) {
            ConfigurationSection teamSection = teamsSection.getConfigurationSection(teamId);
            if (teamSection == null) {
                continue;
            }

            String displayName = teamSection.getString("display-name", teamId);
            String colorName = teamSection.getString("color", "WHITE");
            NamedTextColor color = parseColor(colorName);
            String prefix = teamSection.getString("prefix", "");
            String suffix = teamSection.getString("suffix", "");

            List<String> memberStrings = teamSection.getStringList("members");
            Set<UUID> members = new HashSet<>();
            for (String memberString : memberStrings) {
                try {
                    members.add(UUID.fromString(memberString));
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Invalid UUID in team " + teamId + ": " + memberString);
                }
            }

            TeamImpl team = new TeamImpl(teamId, displayName, color, prefix, suffix, members);
            teams.put(teamId, team);
        }

        return teams;
    }

    @Override
    public void saveTeam(TeamImpl team) {
        String path = "teams." + team.getId();
        teamsConfig.set(path + ".display-name", team.getDisplayName());
        teamsConfig.set(path + ".color", team.getColor().toString());
        teamsConfig.set(path + ".prefix", team.getPrefix());
        teamsConfig.set(path + ".suffix", team.getSuffix());

        List<String> memberStrings = new ArrayList<>();
        for (UUID member : team.getMembers()) {
            memberStrings.add(member.toString());
        }
        teamsConfig.set(path + ".members", memberStrings);

        saveConfigs();
    }

    @Override
    public void deleteTeam(String teamId) {
        teamsConfig.set("teams." + teamId, null);
        saveConfigs();
    }

    @Override
    public Map<UUID, String> loadPlayerAssignments() {
        Map<UUID, String> assignments = new HashMap<>();

        ConfigurationSection assignmentsSection = assignmentsConfig.getConfigurationSection("assignments");
        if (assignmentsSection == null) {
            return assignments;
        }

        for (String uuidString : assignmentsSection.getKeys(false)) {
            try {
                UUID playerUuid = UUID.fromString(uuidString);
                String teamId = assignmentsSection.getString(uuidString);
                if (teamId != null) {
                    assignments.put(playerUuid, teamId);
                }
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Invalid UUID in assignments: " + uuidString);
            }
        }

        return assignments;
    }

    @Override
    public void savePlayerAssignment(UUID playerUuid, String teamId) {
        assignmentsConfig.set("assignments." + playerUuid.toString(), teamId);
        saveConfigs();
    }

    @Override
    public void removePlayerAssignment(UUID playerUuid) {
        assignmentsConfig.set("assignments." + playerUuid.toString(), null);
        saveConfigs();
    }

    private void saveConfigs() {
        try {
            teamsConfig.save(teamsFile);
            assignmentsConfig.save(assignmentsFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save configuration files: " + e.getMessage());
        }
    }

    private NamedTextColor parseColor(String colorName) {
        try {
            return NamedTextColor.NAMES.value(colorName.toLowerCase());
        } catch (Exception e) {
            return NamedTextColor.WHITE;
        }
    }
}
