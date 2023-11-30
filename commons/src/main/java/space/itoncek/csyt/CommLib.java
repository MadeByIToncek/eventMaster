package space.itoncek.csyt;

import org.jetbrains.annotations.NotNull;

import java.sql.*;

public abstract class CommLib implements AutoCloseable {
    private final Connection conn;

    /* Table SQL QUERY
    CREATE TABLE Players (
    name TINYTEXT,
    points int,
    team TINYTEXT
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
    public int GetPoints(String username) throws SQLException {
        PreparedStatement stmt = (PreparedStatement) conn.createStatement();
        ResultSet rs = stmt.executeQuery(String.format("SELECT %s FROM players".formatted(username)));
        stmt.close();

        System.out.println("[DEBUG] getting points from %s".formatted(username));

        return rs.getInt("NAME");
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