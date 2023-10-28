/*
 * Made by IToncek
 *
 * Copyright (c) 2023.
 */

package space.itoncek.csyt;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

class DRMLibTest {

    @Test
    void checkDRM() {
        assertFalse(DRMLib.checkDRM());
    }
}