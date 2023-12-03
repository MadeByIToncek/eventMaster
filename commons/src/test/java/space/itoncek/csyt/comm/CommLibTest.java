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
import java.util.Random;

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
        System.out.println("getPlayer() took " + duration + "ms");

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
        System.out.println("getTeam() took " + duration + "ms");

        System.out.println(player.toString());
        assertTrue(player.hasPlayer("IToncek"));
        assertTrue(player.hasPlayer("NeXuSoveVidea"));
        assertTrue(player.hasPlayer("mrkwi"));
        assertTrue(player.hasPlayer("JellyCZ"));
        assertEquals(player.points(), 0);
        assertEquals(player.color(), Team.spectator);
    }

    @Test
    void addPlayerScore() throws SQLException {
        CSYTPlayer player = lib.getPlayer("IToncek");
        int scoreBefore = player.points();

        long startTime = System.nanoTime();

        Random rnd = new Random();
        int addedScore = rnd.nextInt(200);
        lib.addPlayerScore("IToncek", addedScore);
        int afterAdd = lib.getPlayer("IToncek").points();
        assertEquals(scoreBefore, afterAdd - addedScore);

        long endTime = System.nanoTime();
        double duration = (endTime - startTime) / 1000000d;  //divide by 1000000 to get milliseconds.
        System.out.println("addPlayerScore(+) took " + duration + "ms");

        startTime = System.nanoTime();
        int removedScore = -addedScore;
        lib.addPlayerScore("IToncek", removedScore);
        int afterSubtract = lib.getPlayer("IToncek").points();
        assertEquals(afterSubtract, afterAdd + removedScore);

        endTime = System.nanoTime();
        duration = (endTime - startTime) / 1000000d;  //divide by 1000000 to get milliseconds.
        System.out.println("addPlayerScore(-) took " + duration + "ms");
    }

    @Test
    void setPlayerScore() throws SQLException {
        CSYTPlayer player = lib.getPlayer("IToncek");
        int scoreBefore = player.points();

        Random rnd = new Random();
        int chosenScore = rnd.nextInt(200);

        lib.setPlayerScore("IToncek", chosenScore);
        assertEquals(lib.getPlayer("IToncek").points(), chosenScore);

        lib.setPlayerScore("IToncek", scoreBefore);
    }

    @Test
    void getValue() {

    }

    @Test
    void setValue() {

    }

    @Test
    void addValue() {

    }

    @AfterEach
    void tearDown() throws SQLException {
        lib.close();
    }
}