/*
 * Made by IToncek
 *
 * Copyright (c) 2023.
 */

package space.itoncek.csyt.comm;

import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public abstract class CommLib implements AutoCloseable {

    private final Connection conn;

    /* Players SQL QUERY
    CREATE TABLE `Players` (
	    `name` VARCHAR(256) NOT NULL DEFAULT '' COLLATE 'latin2_czech_cs',
	    `points` INT(10) UNSIGNED ZEROFILL NOT NULL DEFAULT '0000000000',
	    `team` ENUM('coal','copper','iron','gold','redstone','lapis','emerald','diamond','netherite','quartz','spectator') NOT NULL COLLATE 'latin2_czech_cs',
	    PRIMARY KEY (`name`) USING BTREE
    );

    Storage SQL QUERY
    CREATE TABLE `Storage` (
    	`key` VARCHAR(256) NOT NULL DEFAULT '0' COLLATE 'latin2_czech_cs',
    	`value` VARCHAR(256) NULL DEFAULT NULL COLLATE 'latin2_czech_cs',
    	PRIMARY KEY (`key`) USING BTREE
    )
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
     * @return CSYTPlayer object
     * @throws SQLException most likely DB access error
     */
    public CSYTPlayer getPlayer(String username) throws SQLException {
        // LGTM
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM Players WHERE name = '%s';".formatted(username));
        rs.next();
        System.out.printf("[DEBUG] getting points from %s%n", username);
        CSYTPlayer csytPlayer = new CSYTPlayer(rs.getString("name"), rs.getInt("points"), Team.valueOf(rs.getString("team")));
        stmt.close();
        return csytPlayer;
    }

    /**
     * Request player object from database
     *
     * @param color color of team you want to query
     * @return CSYTPlayer object
     */
    public CSYTTeam getTeam(Team color) throws SQLException {
        List<CSYTPlayer> teams = new ArrayList<>();

        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM Players WHERE team='%s';".formatted(color));

        while (rs.next()) {
            CSYTPlayer csytPlayer = new CSYTPlayer(rs.getString("name"), rs.getInt("points"), Team.valueOf(rs.getString("team")));
            teams.add(csytPlayer);
        }

        return new CSYTTeam(color, teams);
    }

    /**
     * Adds points to user
     *
     * @param username Username of this player
     * @param points   points to add to this player
     * @throws SQLException DB Access Error!
     */
    public void addPlayerScore(String username, int points) throws SQLException{
        Statement st = conn.createStatement();
        ResultSet r = st.executeQuery(String.format("SELECT * FROM Players WHERE name = '%s';", username));
        r.next();
        int tempscore = r.getInt("points");

        Statement stmt = conn.createStatement();
        int rs = stmt.executeUpdate("UPDATE Players SET points=%d + %d WHERE name = '%s';".formatted(tempscore, points, username));

        System.out.printf("[DEBUG] adding score to player %s, edited %d row%n", username, rs);
    }

    /**
     * Sets user's points
     * @param username Username of this player
     * @param points points to set to this player
     * @throws SQLException DB Access Error!
     */
    public void setPlayerScore(String username, int points) throws SQLException {
        // LGTM 👍
        Statement stmt = conn.createStatement();

        int rs = stmt.executeUpdate("UPDATE Players SET points='%d' WHERE name='%s';".formatted(points, username));
        stmt.close();

        System.out.printf("Setting %s to %d, edited %d rows%n", username, points, rs);
    }

    /**
     * Fetches value associated with key
     * @param key key
     * @return String associated with key
     * @throws SQLException DB Access Error!
     */
    public String getValue(String key) throws SQLException {
        // LGTM 👍
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM Storage WHERE key=`%s`;".formatted(key));
        return rs.getString("value");
    }

    /**
     * Edits selected keypair
     * @param key key
     * @param value value to associate with key
     * @throws SQLException DB Access Error!
     */
    public void setValue(String key, String value) throws SQLException {
        Statement stmt = conn.createStatement();
        //TODO: Poradí si to s tím, že daná hodnota neexistuje?
        ResultSet rs = stmt.executeQuery("UPDATE Storage SET key=`%s` AND value=`%s`;".formatted(key, value));

        System.out.printf("[DEBUG] setting value to key %s%n", key);
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
     * Creates backup of the whole database, exported as some object (JSONObject/SQL string/etc...)
     *
     * @param checkpointID ID of this checkpoint
     * @return some Object
     * @implNote 🔼 tohle implementovat můžeš ale nemusíš, např. pokud to budeš dělat jako JSONObject tak tam jen přidej hodnotu jak to identifikovat
     */
    public Object createCheckpoint(String checkpointID) {
        //TODO: Dopsat!
        return null;
    }

    /**
     * Resets (!) database from backup, created using createCheckpoint()
     *
     * @param data data from createCheckpoint()
     * @implNote reloadCheckpoint(createCheckpoint ( " test ")) should do nothing!
     */
    public void reloadCheckpoint(Object data){}

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