package space.itoncek.eventmaster.construction.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import space.itoncek.eventmaster.construction.BuildPlace;
import space.itoncek.eventmaster.construction.SimpleLocation;

import static space.itoncek.eventmaster.construction.Construction.*;

public class BlockActionListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(@NotNull BlockPlaceEvent event) {
        if (event.getBlock().getWorld() == Bukkit.getWorld("construction")) {
            BuildPlace buildPlace = locationHash.get(SimpleLocation.createSimpleLocation(event.getBlock()));

            if (!blocking && (buildPlace == null || buildPlace.display)) {
                event.setCancelled(true);
                return;
            }
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (buildPlace.active) {
                        if (!buildPlace.display) {
                            buildPlace.addPlayerBlockPoints(event.getPlayer(), event.getBlock().getLocation());
                            if (buildPlace.matchPattern()) {
                                for (Location location : buildPlace.getLocations()) {
                                    location.getBlock().setType(Material.AIR);
                                }
                                buildPlace.active = false;
                                buildPlace.reward();

                                boolean finish = true;
                                for (BuildPlace place : teams.get(buildPlace.color).buildPlaces()) {
                                    if (place.active) {
                                        finish = false;
                                        break;
                                    }
                                }

                                if (finish) {
                                    for (Player p : event.getPlayer().getLocation().getNearbyPlayers(20)) {
                                        p.playSound(teams.get(buildPlace.color).display().getRelLoc(2, 2).clone().add(0, 1, 0), Sound.ENTITY_PLAYER_LEVELUP, 10f, 1f);
                                    }
                                    teams.get(buildPlace.color).recycle();
                                }
                            }
                        }
                    }
                }
            }.runTaskLater(pl, 1L);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(@NotNull BlockBreakEvent event) {
        if (event.getBlock().getWorld() == Bukkit.getWorld("construction")) {
            BuildPlace buildPlace = locationHash.get(SimpleLocation.createSimpleLocation(event.getBlock()));

            if (!blocking && (buildPlace == null || buildPlace.display)) {
                event.setCancelled(true);
                return;
            }
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (buildPlace.active) {
                        if (!buildPlace.display) {
                            buildPlace.addPlayerBlockPoints(event.getPlayer(), event.getBlock().getLocation());
                            if (buildPlace.matchPattern()) {
                                for (Location location : buildPlace.getLocations()) {
                                    location.getBlock().setType(Material.AIR);
                                }
                                buildPlace.active = false;
                                buildPlace.reward();

                                boolean finish = true;
                                for (BuildPlace place : teams.get(buildPlace.color).buildPlaces()) {
                                    if (place.active) {
                                        finish = false;
                                        break;
                                    }
                                }

                                if (finish) {
                                    for (Player p : event.getPlayer().getLocation().getNearbyPlayers(20)) {
                                        p.playSound(teams.get(buildPlace.color).display().getRelLoc(2, 2).clone().add(0, 1, 0), Sound.ENTITY_PLAYER_LEVELUP, 10f, 1f);
                                    }
                                    teams.get(buildPlace.color).recycle();
                                } else {
                                    if (event.isDropItems()) {
                                        event.getPlayer().getInventory().addItem(event.getBlock().getDrops().toArray(new ItemStack[0]));
                                        event.setDropItems(false);
                                    }
                                }
                            }
                        }
                    }
                }
            }.runTaskLater(pl, 1L);
        }
    }
}