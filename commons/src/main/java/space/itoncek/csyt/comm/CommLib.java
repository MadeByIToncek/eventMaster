/*
 * Made by IToncek
 *
 * Copyright (c) 2023.
 */

package space.itoncek.csyt.comm;

import org.jetbrains.annotations.NotNull;

import java.sql.*;

public abstract class CommLib implements AutoCloseable {
    private final Connection conn;

    /* Table SQL QUERY
    CREATE TABLE Players (
    name TINYTEXT,
    points int,
    team ENUM('coal', 'copper', 'iron', 'gold', 'redstone', 'lapis', 'emerald', 'diamond', 'netherite', 'quartz')
    );
    */

    /**
     * Creates new DB connection
     *
     * @param url  URL address of the database instance, cannot be null
     * @param user MySQL User
     * @param pass MySQL User's password
     * @throws SQLException DB access error / url == null
     */
    public CommLib(@NotNull String url, String user, String pass) throws SQLException {
        conn = DriverManager.getConnection(url, user, pass);
    }

    /**
     * Request player object from database
     *
     * @param username username of player you want to query
     * @return TODO
     * @throws SQLException most likely DB access error
     */
    public boolean getPlayer(String username) throws SQLException {
        PreparedStatement stmt = (PreparedStatement) conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM players WHERE name = '%s';".formatted(username));
        stmt.close();

        System.out.printf("[DEBUG] getting points from %s%n", username);

        //return rs.getInt("points");
        //TODO: Tato funkce by měla vrátit objekt CSYTPlayer
        return false;
    }

    public boolean getTeam(String color) {
        //TODO: Tato funkce by měla vrátit objekt CSYTTeam
        return false;
    }

    public void addPlayerScore(String username, int points) {
        //TODO: Tato funkce by měla vrátit void
    }

    public void setPlayerScore(String username, int points) {
        //TODO: Tato funkce by měla vrátit void
    }

    public String getValue(String key) {
        //TODO: Tato funkce by měla vrátit String
        return "";
    }

    public void setValue(String key, String value) {
        //TODO: Tato funkce by měla vrátit void
    }

    /**
     * Adds amount to value associated with key
     *
     * @param key    Identifier in keypair table
     * @param amount Amount to add to the value
     * @implNote Fails silently if value of key is not an integer
     */
    public void addValue(String key, int amount) {
        try {
            int value = Integer.parseInt(getValue(key));
            setValue(key, String.valueOf(value + amount));
        } catch (NumberFormatException ignored) {
        }
    }

    /**
     * Closes this connection
     *
     * @throws SQLException DB access error
     */
    @Override
    public void close() throws SQLException {
        conn.close();
    }
}