/*
 * Made by IToncek
 *
 * Copyright (c) 2023.
 */

package space.itoncek.csyt.construction;

import org.bukkit.Location;
import org.bukkit.block.Block;

public record SimpleLocation(int x, int y, int z) {
    public static SimpleLocation createSimpleLocation(Location loc) {
        return createSimpleLocation(loc.getBlock());
    }

    public static SimpleLocation createSimpleLocation(Block b) {
        return new SimpleLocation(b.getX(), b.getY(), b.getZ());
    }

}
