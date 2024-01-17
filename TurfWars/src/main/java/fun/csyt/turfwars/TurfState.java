/*
 * Made by IToncek
 *
 * Copyright (c) 2023.
 */

package fun.csyt.turfwars;

/** Keeping track */
public enum TurfState {
    /** Game is asleep */
    INACTIVE,
    /** Game awaits setup */
    SETUP,
    /** Game awaits start */
    READY,
    /** Game is in PVP mode */
    PVP,
    /** Game is in BUILD mode */
    BUILD,
    /** Game is finished */
    FINISHED
}
