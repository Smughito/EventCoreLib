package dev.smughito.eventcorelib.storage;

import dev.smughito.eventcorelib.api.Team;
import dev.smughito.eventcorelib.core.TeamImpl;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class YamlStorageProvider implements StorageProvider {

    private final File dataFolder;
    private File teamsFile;
    private File assignmentsFile;
    private YamlConfiguration teamsConfig;
    private YamlConfiguration assignmentsConfig;

    public YamlStorageProvider(File dataFolder) {
        this.dataFolder = dataFolder;
    }

    @Override
    public void initialize() {
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        teamsFile = new File(dataFolder, "teams.yml");
        assignmentsFile = new File(dataFolder, "assignments.yml");

        if (!teamsFile.exists()) {
            try {
                teamsFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (!assignmentsFile.exists()) {
            try {
                assignmentsFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        teamsConfig = YamlConfiguration.loadConfiguration(teamsFile);
        assignmentsConfig = YamlConfiguration.loadConfiguration(assignmentsFile);
    }

    @Override
    public void saveTeam(Team team) {
        String path = "teams." + team.getId();
        teamsConfig.set(path + ".displayName", team.getDisplayName());
        teamsConfig.set(path + ".color", team.getColor().asHexString());
        teamsConfig.set(path + ".prefix", team.getPrefix());
        teamsConfig.set(path + ".suffix", team.getSuffix());

        List<String> memberStrings = new ArrayList<>();
        for (UUID uuid : team.getMembers()) {
            memberStrings.add(uuid.toString());
        }
        teamsConfig.set(path + ".members", memberStrings);

        try {
            teamsConfig.save(teamsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteTeam(String teamId) {
        teamsConfig.set("teams." + teamId, null);
        try {
            teamsConfig.save(teamsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Map<String, Team> loadTeams() {
        Map<String, Team> teams = new HashMap<>();

        ConfigurationSection teamsSection = teamsConfig.getConfigurationSection("teams");
        if (teamsSection == null) {
            return teams;
        }

        for (String teamId : teamsSection.getKeys(false)) {
            String path = "teams." + teamId;
            String displayName = teamsConfig.getString(path + ".displayName", teamId);
            String colorHex = teamsConfig.getString(path + ".color", "#FFFFFF");
            String prefix = teamsConfig.getString(path + ".prefix", "");
            String suffix = teamsConfig.getString(path + ".suffix", "");
            List<String> memberStrings = teamsConfig.getStringList(path + ".members");

            TextColor color = TextColor.fromHexString(colorHex);
            if (color == null) {
                color = TextColor.color(255, 255, 255);
            }

            TeamImpl team = new TeamImpl(teamId, displayName, color);
            team.setPrefix(prefix);
            team.setSuffix(suffix);

            for (String uuidString : memberStrings) {
                try {
                    UUID uuid = UUID.fromString(uuidString);
                    team.addMember(uuid);
                } catch (IllegalArgumentException e) {
                }
            }

            teams.put(teamId, team);
        }

        return teams;
    }

    @Override
    public void savePlayerTeamAssignment(UUID playerId, String teamId) {
        assignmentsConfig.set("assignments." + playerId.toString(), teamId);
        try {
            assignmentsConfig.save(assignmentsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removePlayerTeamAssignment(UUID playerId) {
        assignmentsConfig.set("assignments." + playerId.toString(), null);
        try {
            assignmentsConfig.save(assignmentsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Map<UUID, String> loadPlayerTeamAssignments() {
        Map<UUID, String> assignments = new HashMap<>();

        ConfigurationSection assignmentsSection = assignmentsConfig.getConfigurationSection("assignments");
        if (assignmentsSection == null) {
            return assignments;
        }

        for (String uuidString : assignmentsSection.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(uuidString);
                String teamId = assignmentsConfig.getString("assignments." + uuidString);
                if (teamId != null) {
                    assignments.put(uuid, teamId);
                }
            } catch (IllegalArgumentException e) {
            }
        }

        return assignments;
    }

    @Override
    public void shutdown() {
    }
}
