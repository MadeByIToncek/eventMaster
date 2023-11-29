package space.itoncek.csyt;
import java.sql.*;

public abstract class CommLib {
    //public String DB_URL = "mysql://CHANGE_THIS";
    //public String DB_USER = "CHANGE_THIS";
    //public String DB_PASS = "CHANGE_THIS";

    public boolean GetPlayer(String DB_URL, String DB_USER, String DB_PASS) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            PreparedStatement stmt = (PreparedStatement) conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT *");
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}