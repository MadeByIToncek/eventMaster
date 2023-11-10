/*
 * Made by IToncek
 *
 * Copyright (c) 2023.
 */

package space.itoncek.csyt.cg.common;

public enum Minigame {
    BLITZ_TAG(0b1),
    REACH_THE_END(0b10),
    PARKOUR_MASTERS(0b100),
    SKY_LANDS(0b1000),
    GRID_BUILDERS(0b10000),
    LASER_GAME(0b100000);

    public final int mask;

    Minigame(int mask) {

        this.mask = mask;
    }
}
