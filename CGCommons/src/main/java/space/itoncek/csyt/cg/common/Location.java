/*
 * Made by IToncek
 *
 * Copyright (c) 2023.
 */

package space.itoncek.csyt.cg.common;

public record Location(float x, float z) {

    public static Location parseLocation(String l) {
        return new Location(Float.parseFloat(l.split("\\r?x")[0]), Float.parseFloat(l.split("\\r?x")[1]));
    }

    @Override
    public String toString() {
        return x + "x" + z;
    }
}
