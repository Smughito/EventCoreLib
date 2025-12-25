package com.smughito.eventcorelib.storage;

import com.smughito.eventcorelib.core.TeamImpl;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.sql.*;
import java.util.*;

public class SqliteStorageProvider implements StorageProvider {

    private final Plugin plugin;
    private final String databaseName;
    private Connection connection;

    public SqliteStorageProvider(Plugin plugin, String databaseName) {
        this.plugin = plugin;
        this.databaseName = databaseName;
    }

    @Override
    public void initialize() {
        try {
            File dataFolder = plugin.getDataFolder();
            if (!dataFolder.exists()) {
                dataFolder.mkdirs();
            }

            File databaseFile = new File(dataFolder, databaseName);
            String url = "jdbc:sqlite:" + databaseFile.getAbsolutePath();

            connection = DriverManager.getConnection(url);
            createTables();

            plugin.getLogger().info("SQLite database initialized successfully");
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to initialize SQLite database: " + e.getMessage());
        }
    }

    @Override
    public void shutdown() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to close SQLite connection: " + e.getMessage());
        }
    }

    private void createTables() throws SQLException {
        String createTeamsTable = """
            CREATE TABLE IF NOT EXISTS teams (
                id TEXT PRIMARY KEY,
                display_name TEXT NOT NULL,
                color TEXT NOT NULL,
                prefix TEXT NOT NULL,
                suffix TEXT NOT NULL
            )
        """;

        String createMembersTable = """
            CREATE TABLE IF NOT EXISTS team_members (
                player_uuid TEXT PRIMARY KEY,
                team_id TEXT NOT NULL,
                FOREIGN KEY (team_id) REFERENCES teams(id) ON DELETE CASCADE
            )
        """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createTeamsTable);
            stmt.execute(createMembersTable);
        }
    }

    @Override
    public Map<String, TeamImpl> loadTeams() {
        Map<String, TeamImpl> teams = new HashMap<>();

        String query = "SELECT id, display_name, color, prefix, suffix FROM teams";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String id = rs.getString("id");
                String displayName = rs.getString("display_name");
                String colorName = rs.getString("color");
                String prefix = rs.getString("prefix");
                String suffix = rs.getString("suffix");

                NamedTextColor color = parseColor(colorName);
                Set<UUID> members = loadTeamMembers(id);

                TeamImpl team = new TeamImpl(id, displayName, color, prefix, suffix, members);
                teams.put(id, team);
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to load teams from database: " + e.getMessage());
        }

        return teams;
    }

    private Set<UUID> loadTeamMembers(String teamId) {
        Set<UUID> members = new HashSet<>();
        String query = "SELECT player_uuid FROM team_members WHERE team_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, teamId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    try {
                        UUID uuid = UUID.fromString(rs.getString("player_uuid"));
                        members.add(uuid);
                    } catch (IllegalArgumentException e) {
                        plugin.getLogger().warning("Invalid UUID in database for team " + teamId);
                    }
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to load team members: " + e.getMessage());
        }

        return members;
    }

    @Override
    public void saveTeam(TeamImpl team) {
        String query = """
            INSERT OR REPLACE INTO teams (id, display_name, color, prefix, suffix)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, team.getId());
            stmt.setString(2, team.getDisplayName());
            stmt.setString(3, team.getColor().toString());
            stmt.setString(4, team.getPrefix());
            stmt.setString(5, team.getSuffix());
            stmt.executeUpdate();

            saveTeamMembers(team);
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to save team: " + e.getMessage());
        }
    }

    private void saveTeamMembers(TeamImpl team) throws SQLException {
        String deleteQuery = "DELETE FROM team_members WHERE team_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(deleteQuery)) {
            stmt.setString(1, team.getId());
            stmt.executeUpdate();
        }

        if (!team.getMembers().isEmpty()) {
            String insertQuery = "INSERT INTO team_members (player_uuid, team_id) VALUES (?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(insertQuery)) {
                for (UUID member : team.getMembers()) {
                    stmt.setString(1, member.toString());
                    stmt.setString(2, team.getId());
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }
        }
    }

    @Override
    public void deleteTeam(String teamId) {
        String query = "DELETE FROM teams WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, teamId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to delete team: " + e.getMessage());
        }
    }

    @Override
    public Map<UUID, String> loadPlayerAssignments() {
        Map<UUID, String> assignments = new HashMap<>();
        String query = "SELECT player_uuid, team_id FROM team_members";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                try {
                    UUID playerUuid = UUID.fromString(rs.getString("player_uuid"));
                    String teamId = rs.getString("team_id");
                    assignments.put(playerUuid, teamId);
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Invalid UUID in database");
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to load player assignments: " + e.getMessage());
        }

        return assignments;
    }

    @Override
    public void savePlayerAssignment(UUID playerUuid, String teamId) {
        String query = "INSERT OR REPLACE INTO team_members (player_uuid, team_id) VALUES (?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, playerUuid.toString());
            stmt.setString(2, teamId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to save player assignment: " + e.getMessage());
        }
    }

    @Override
    public void removePlayerAssignment(UUID playerUuid) {
        String query = "DELETE FROM team_members WHERE player_uuid = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, playerUuid.toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to remove player assignment: " + e.getMessage());
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
