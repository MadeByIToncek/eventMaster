package space.itoncek.eventmaster.construction.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import space.itoncek.eventmaster.construction.BuildPlace;
import space.itoncek.eventmaster.construction.SimpleLocation;

import static space.itoncek.eventmaster.construction.Construction.*;

public class BlockActionListener implements Listener {
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockPlace(@NotNull BlockPlaceEvent event) {
        new BukkitRunnable() {
            @Override
            public void run() {
                BuildPlace buildPlace = locationHash.get(SimpleLocation.createSimpleLocation(event.getBlock()));

                if (buildPlace == null) {
                    event.setCancelled(true);
                    return;
                }

                common(buildPlace, event.getPlayer(), event.getBlock().getLocation());
            }
        }.runTaskLater(pl, 5L);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(@NotNull BlockBreakEvent event) {
        new BukkitRunnable() {
            @Override
            public void run() {
                BuildPlace buildPlace = locationHash.get(SimpleLocation.createSimpleLocation(event.getBlock()));
                if (buildPlace == null) {
                    event.setCancelled(true);
                    return;
                }
                common(buildPlace, event.getPlayer(), event.getBlock().getLocation());
            }
        }.runTaskLater(pl, 5L);
    }

    public void common(@NotNull BuildPlace buildPlace, Player player, Location loc) {
        if (buildPlace.active) {
            if (!buildPlace.display) {
                buildPlace.addPlayerBlockPoints(player, loc);
                if (buildPlace.matchPattern()) {
                    for (Location location : buildPlace.getLocations()) {
                        location.getBlock().setType(Material.AIR);
                    }
                    buildPlace.active = false;
                    buildPlace.reward();
                    logOutput.add("BF;" + buildPlace.color.name() + ";" + buildPlace.patternID);

                    boolean finish = true;
                    for (BuildPlace place : teams.get(buildPlace.color).buildPlaces()) {
                        if (place.active) {
                            finish = false;
                            break;
                        }
                    }

                    if (finish) {
                        for (Player p : player.getLocation().getNearbyPlayers(20)) {
                            p.playSound(teams.get(buildPlace.color).display().getRelLoc(2, 2).clone().add(0, 1, 0), Sound.ENTITY_PLAYER_LEVELUP, 10f, 1f);
                        }
                        teams.get(buildPlace.color).recycle();
                    }
                }
            }
        }
    }
}
