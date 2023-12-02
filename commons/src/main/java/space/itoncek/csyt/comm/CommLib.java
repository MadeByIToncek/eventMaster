/*
 * Made by IToncek
 *
 * Copyright (c) 2023.
 */

package space.itoncek.csyt.comm;

import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.List;

public abstract class CommLib implements AutoCloseable {

    private final Connection conn;

    /* Table SQL QUERY
    CREATE TABLE `Players` (
	    `name` VARCHAR(256) NOT NULL DEFAULT '' COLLATE 'latin1_swedish_ci',
	    `points` INT(10) UNSIGNED ZEROFILL NOT NULL DEFAULT '0000000000',
	    `team` ENUM('coal','copper','iron','gold','redstone','lapis','emerald','diamond','netherite','quartz','spectator') NOT NULL COLLATE 'latin1_swedish_ci',
	    PRIMARY KEY (`name`) USING BTREE
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
    public CSYTPlayer getPlayer(String username) throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM Players WHERE name = '%s';".formatted(username));
        rs.next();
        System.out.printf("[DEBUG] getting points from %s%n", username);
        //TODO: Vyplnit tyto hodnoty
        CSYTPlayer csytPlayer = new CSYTPlayer(rs.getString("name"), rs.getInt("points"), Team.valueOf(rs.getString("team")));
        stmt.close();
        return csytPlayer;
    }

    public CSYTTeam getTeam(String color) {
        //TODO: Vyplnit tyto hodnoty
        return new CSYTTeam(Team.spectator, List.of());
    }

    public void addPlayerScore(String username, int points) throws SQLException{
        PreparedStatement stmt = (PreparedStatement) conn.createStatement();
        ResultSet rs = stmt.executeQuery("INSERT INTO Customers (name, points) VALUES (%s, %d);".formatted(username, points));

        System.out.println("[DEBUG] adding score to player %s".formatted(username));
    }

    public void setPlayerScore(String username, int points) throws SQLException {
        PreparedStatement stmt = (PreparedStatement) conn.createStatement();
        ResultSet rs = stmt.executeQuery("UPDATE Players SET points=`%d` WHERE name=`%s`;".formatted(points, username));
        stmt.close();

        System.out.println("Setting %s to %d".formatted(username, points));
    }

    public String getValue(String key) throws SQLException {
        //TODO: Tato funkce by měla vrátit String
        PreparedStatement stmt = (PreparedStatement) conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM Storage WHERE key=`%s`;".formatted(key));
        return rs.getString("value");
    }

    public void setValue(String key, String value) throws SQLException {
        PreparedStatement stmt = (PreparedStatement) conn.createStatement();
        ResultSet rs = stmt.executeQuery("UPDATE Storage SET key=`%s` AND value=`%s`;".formatted(key, value));

        System.out.println("[DEBUG] setting value to key %s".formatted(key));
    }

    /**
     * Adds amount to value associated with key
     *
     * @param key    Identifier in keypair table
     * @param amount Amount to add to the value
     * @implNote Fails silently if value of key is not an integer
     */
    public void addValue(String key, int amount) throws SQLException {
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