package space.itoncek.csyt.comm;

import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CommLibTest {

    @Test
    void getPlayer() throws SQLException {
        CommLib lib = new CommLib(System.getenv("DB_URL"), System.getenv("DB_USER"), System.getenv("DB_PWD")) {
        };

        CSYTPlayer player = lib.getPlayer("IToncek");
        lib.close();

        System.out.println(player.toString());
        assertEquals(player.name(), "IToncek");
        assertEquals(player.points(), 0);
        assertEquals(player.team(), Team.spectator);
    }
}