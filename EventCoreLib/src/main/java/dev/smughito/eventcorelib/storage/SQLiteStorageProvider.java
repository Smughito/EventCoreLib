package dev.smughito.eventcorelib.storage;

import dev.smughito.eventcorelib.api.Team;
import dev.smughito.eventcorelib.core.TeamImpl;
import net.kyori.adventure.text.format.TextColor;

import java.io.File;
import java.sql.*;
import java.util.*;

public class SQLiteStorageProvider implements StorageProvider {

    private final File dataFolder;
    private final String filename;
    private Connection connection;

    public SQLiteStorageProvider(File dataFolder, String filename) {
        this.dataFolder = dataFolder;
        this.filename = filename;
    }

    @Override
    public void initialize() {
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        File dbFile = new File(dataFolder, filename);

        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());

            createTables();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void createTables() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS teams (" +
                "id TEXT PRIMARY KEY, " +
                "display_name TEXT NOT NULL, " +
                "color TEXT NOT NULL, " +
                "prefix TEXT, " +
                "suffix TEXT" +
                ")"
            );

            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS team_members (" +
                "player_uuid TEXT PRIMARY KEY, " +
                "team_id TEXT NOT NULL, " +
                "FOREIGN KEY(team_id) REFERENCES teams(id) ON DELETE CASCADE" +
                ")"
            );
        }
    }

    @Override
    public void saveTeam(Team team) {
        String sql = "INSERT OR REPLACE INTO teams (id, display_name, color, prefix, suffix) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, team.getId());
            pstmt.setString(2, team.getDisplayName());
            pstmt.setString(3, team.getColor().asHexString());
            pstmt.setString(4, team.getPrefix());
            pstmt.setString(5, team.getSuffix());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try (PreparedStatement pstmt = connection.prepareStatement(
            "DELETE FROM team_members WHERE team_id = ?")) {
            pstmt.setString(1, team.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for (UUID member : team.getMembers()) {
            savePlayerTeamAssignment(member, team.getId());
        }
    }

    @Override
    public void deleteTeam(String teamId) {
        String sql = "DELETE FROM teams WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, teamId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Map<String, Team> loadTeams() {
        Map<String, Team> teams = new HashMap<>();
        String sql = "SELECT * FROM teams";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String id = rs.getString("id");
                String displayName = rs.getString("display_name");
                String colorHex = rs.getString("color");
                String prefix = rs.getString("prefix");
                String suffix = rs.getString("suffix");

                TextColor color = TextColor.fromHexString(colorHex);
                if (color == null) {
                    color = TextColor.color(255, 255, 255);
                }

                TeamImpl team = new TeamImpl(id, displayName, color);
                team.setPrefix(prefix != null ? prefix : "");
                team.setSuffix(suffix != null ? suffix : "");

                teams.put(id, team);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Map<UUID, String> assignments = loadPlayerTeamAssignments();
        for (Map.Entry<UUID, String> entry : assignments.entrySet()) {
            Team team = teams.get(entry.getValue());
            if (team != null) {
                team.addMember(entry.getKey());
            }
        }

        return teams;
    }

    @Override
    public void savePlayerTeamAssignment(UUID playerId, String teamId) {
        String sql = "INSERT OR REPLACE INTO team_members (player_uuid, team_id) VALUES (?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, playerId.toString());
            pstmt.setString(2, teamId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removePlayerTeamAssignment(UUID playerId) {
        String sql = "DELETE FROM team_members WHERE player_uuid = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, playerId.toString());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Map<UUID, String> loadPlayerTeamAssignments() {
        Map<UUID, String> assignments = new HashMap<>();
        String sql = "SELECT * FROM team_members";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String uuidString = rs.getString("player_uuid");
                String teamId = rs.getString("team_id");

                try {
                    UUID uuid = UUID.fromString(uuidString);
                    assignments.put(uuid, teamId);
                } catch (IllegalArgumentException e) {
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return assignments;
    }

    @Override
    public void shutdown() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
