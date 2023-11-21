/*
 * Made by IToncek
 *
 * Copyright (c) 2023.
 */

package space.itoncek.eventmanager.capturepoint;

import org.bukkit.Location;

import static space.itoncek.eventmanager.capturepoint.CapturePoint.log;

public record CapturePointInstance(Location center, Location capt1, Location capt2) {
    public boolean isInside(Location loc) {
        Location min = new Location(capt1.getWorld(), Math.min(capt1.x(), capt2.x()), Math.min(capt1.y(), capt2.y()), Math.min(capt1.z(), capt2.z()));
        Location max = new Location(capt1.getWorld(), Math.max(capt1.x(), capt2.x()), Math.max(capt1.y(), capt2.y()), Math.max(capt1.z(), capt2.z()));
        boolean b = loc.x() >= min.x() && loc.x() <= max.x() && loc.y() >= min.y() && loc.y() <= max.y() && loc.z() >= min.z() && loc.z() <= max.z();
        log("IsLocationInRegion?" + b);
        return b;
    }
}
