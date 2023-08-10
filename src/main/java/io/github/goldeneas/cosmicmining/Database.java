package io.github.goldeneas.cosmicmining;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.*;
import java.sql.*;
import java.util.OptionalInt;
import java.util.logging.Level;

public class Database {
    private static String databaseName;
    private static CosmicMining plugin;

    public Database(CosmicMining _plugin) {
        plugin = _plugin;
        databaseName = plugin.getDataFolder() + "/database.db";

        initializeDatabase();
        testDatabaseConnection();
    }

    public boolean addExperience(Player player, int expPoints) {
        return add("player_levels", "experience", player, expPoints);
    }

    public boolean removeExperience(Player player, int expPoints) {
        return addExperience(player, -expPoints);
    }


    public boolean addLevels(Player player, int levels) {
        return add("player_levels", "levels", player, levels);
    }

    public boolean removeLevels(Player player, int levels) {
        return addLevels(player, -levels);
    }

    public boolean setLevel(Player player, int level) {
        return set("player_levels", "levels", player, level);
    }

    public OptionalInt getLevel(Player player) {
        return getInt("player_levels", "levels", player);
    }

    public OptionalInt getExperience(Player player) {
        return getInt("player_levels", "experience", player);
    }

    private OptionalInt getInt(String table, String column, Player player) {
        String query = String.format("SELECT %s FROM %s WHERE uuid = ?;", column, table);

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + databaseName); PreparedStatement stmt = conn.prepareStatement(
                query
        )) {
            stmt.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                return OptionalInt.of(resultSet.getInt(column));
            }
            return OptionalInt.empty();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not retrieve player's " + column + "!", e);
            return OptionalInt.empty();
        }
    }

    private boolean set(String table, String column, Player player, Object value) {
        String query = String.format("INSERT INTO %s(uuid, %s) VALUES(?, ?);", table, column);

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + databaseName); PreparedStatement stmt = conn.prepareStatement(
                query
        )) {
            stmt.setString(1, player.getUniqueId().toString());
            stmt.setObject(2, value);
            stmt.execute();
            return true;
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not set player's " + column + "!", e);
        }
        return false;
    }

    private boolean add(String table, String column, Player player, Object value) {
        String query = String.format("UPDATE %s SET %s = %s + ? WHERE uuid = ?", table, column, column);

        try(Connection conn = DriverManager.getConnection("jdbc:sqlite:" + databaseName); PreparedStatement stmt = conn.prepareStatement(
                query
        )) {
            stmt.setObject(1, value);
            stmt.setString(2, player.getUniqueId().toString());
            stmt.execute();
            return true;
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not add player's" + column + "!", e);
        }

        return false;
    }

    private void initializeDatabase() {
        String setupStatements;

        try (InputStream in = plugin.getResource("setup.db")) {
            setupStatements = new String(in.readAllBytes());
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not read database setup file!", e);
            return;
        }

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + databaseName);
             PreparedStatement stmt = conn.prepareStatement(setupStatements)) {
            stmt.execute();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not process a database setup statement!", e);
        }
    }

    private void testDatabaseConnection() {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + databaseName)) {
            if (!conn.isValid(1)) {
                Bukkit.getLogger().severe("Could not connect to database.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
