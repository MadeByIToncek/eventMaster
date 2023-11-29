package space.itoncek.csyt;

import org.jetbrains.annotations.NotNull;

import java.sql.*;

public abstract class CommLib implements AutoCloseable {
    private final Connection conn;

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
    public boolean GetPlayer(String username) throws SQLException {
        PreparedStatement stmt = (PreparedStatement) conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT *");
        stmt.close();

        return true;
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