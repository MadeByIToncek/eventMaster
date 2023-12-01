package space.itoncek.csyt.comm;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CommLibTest {

    CommLib lib;

    @BeforeEach
    void setUp() throws SQLException {
        lib = new CommLib(System.getenv("DB_URL"), System.getenv("DB_USER"), System.getenv("DB_PWD")) {
        };
    }

    @Test
    void getPlayer() throws SQLException {
        CSYTPlayer player = lib.getPlayer("IToncek");

        System.out.println(player.toString());
        assertEquals(player.name(), "IToncek");
        assertEquals(player.points(), 0);
        assertEquals(player.team(), Team.spectator);
    }

    @Test
    void getTeam() {
        CSYTTeam player = lib.getTeam(Team.spectator.toString());

        System.out.println(player.toString());
        assertTrue(player.hasPlayer("IToncek"));
        assertTrue(player.hasPlayer("NeXuSoveVidea"));
        assertTrue(player.hasPlayer("mrkwi"));
        assertTrue(player.hasPlayer("JellyCZ"));
        assertEquals(player.points(), 0);
        assertEquals(player.color(), Team.spectator);
    }

    @AfterEach
    void tearDown() throws SQLException {
        lib.close();
    }
}