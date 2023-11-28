/*
 * Made by IToncek
 *
 * Copyright (c) 2023.
 */

package space.itoncek.csyt;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;

class UpdateLibTest {

//    @Test
//    void getCommitID() {
//        System.out.println("Latest release: " + UpdateLib.getCommitID("./config/.ghcreds"));
//        assertTrue(true);
//    }

    @Test
    void update() throws IOException {
        UpdateLib.update("construction", new File("./plugin.jar"));
        assertTrue(true);
    }
}