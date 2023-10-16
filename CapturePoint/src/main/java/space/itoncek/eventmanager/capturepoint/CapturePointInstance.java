/*
 * Made by IToncek
 *
 * Copyright (c) 2023.
 */

package space.itoncek.eventmanager.capturepoint;

import org.bukkit.Location;

public record CapturePointInstance(Location center, Location reg1, Location reg2) {
    public boolean isInside(Location loc) {
        Location min = new Location(reg1.getWorld(), Math.min(reg1.x(), reg2.x()), Math.min(reg1.y(), reg2.y()), Math.min(reg1.z(), reg2.z()));
        Location max = new Location(reg1.getWorld(), Math.max(reg1.x(), reg2.x()), Math.max(reg1.y(), reg2.y()), Math.max(reg1.z(), reg2.z()));
        return loc.x() >= min.x() && loc.x() <= max.x() && loc.y() >= min.y() && loc.y() <= max.y() && loc.z() >= min.z() && loc.z() <= max.z();
    }
}
