/*
 * Made by IToncek
 *
 * Copyright (c) 2023.
 */

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
        long startTime = System.nanoTime();
        lib = new CommLib(System.getenv("DB_URL"), System.getenv("DB_USER"), System.getenv("DB_PWD")) {
        };

        long endTime = System.nanoTime();
        double duration = (endTime - startTime) / 1000000d;  //divide by 1000000 to get milliseconds.
        System.out.println("setUp() took " + duration + "ms");
    }

    @Test
    void getPlayer() throws SQLException {
        long startTime = System.nanoTime();
        CSYTPlayer player = lib.getPlayer("IToncek");

        long endTime = System.nanoTime();
        double duration = (endTime - startTime) / 1000000d;  //divide by 1000000 to get milliseconds.
        System.out.println("setUp() took " + duration + "ms");

        assertEquals(player.name(), "IToncek");
        assertEquals(player.points(), 0);
        assertEquals(player.team(), Team.spectator);
    }

    @Test
    void getTeam() throws SQLException {
        long startTime = System.nanoTime();
        CSYTTeam player = lib.getTeam(Team.spectator);

        long endTime = System.nanoTime();
        double duration = (endTime - startTime) / 1000000d;  //divide by 1000000 to get milliseconds.
        System.out.println("setUp() took " + duration + "ms");

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