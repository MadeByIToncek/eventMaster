package space.itoncek.eventmaster.construction.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import space.itoncek.eventmaster.construction.BuildPlace;
import space.itoncek.eventmaster.construction.SimpleLocation;

import static space.itoncek.eventmaster.construction.Construction.locationHash;

public class BlockPlaceListener implements Listener {
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        BuildPlace buildPlace = locationHash.get(SimpleLocation.createSimpleLocation(event.getBlock()));

        if (buildPlace == null) {
            event.setCancelled(false);
            return;
        }

        if (buildPlace.ticking) {
            if (buildPlace.matchPattern()) {
                for (Location location : buildPlace.getLocations()) {
                    location.getBlock().setType(Material.AIR);
                }
                buildPlace.ticking = false;
                buildPlace.reward();
            }
            ;
        }
    }
}
