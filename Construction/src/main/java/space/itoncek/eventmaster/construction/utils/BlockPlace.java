package space.itoncek.eventmaster.construction.utils;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public record BlockPlace(Player p, Location loc) implements Comparable<BlockPlace> {
    @Override
    public int compareTo(@NotNull BlockPlace o) {
        if (loc.getX() > o.loc.getX()) return 1;
        else if (loc.getX() > o.loc.getX()) return -1;
        if (loc.getZ() > o.loc.getZ()) return 1;
        else if (loc.getZ() > o.loc.getZ()) return -1;
        if (loc.getZ() > o.loc.getZ()) return 1;
        return loc.getZ() > o.loc.getZ() ? -1 : 0;
    }
}
