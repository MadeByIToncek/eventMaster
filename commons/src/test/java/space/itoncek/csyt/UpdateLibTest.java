/*
 * Made by IToncek
 *
 * Copyright (c) 2023.
 */

package space.itoncek.csyt;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class UpdateLibTest {

    @Test
    void getCommitID() {
        System.out.println("Latest release: " + UpdateLib.getCommitID());
        assertTrue(true);
    }
}