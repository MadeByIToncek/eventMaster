/*
 * Made by IToncek
 *
 * Copyright (c) 2023.
 */

package space.itoncek.csyt.construction.listeners;

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
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import space.itoncek.csyt.construction.BuildPlace;
import space.itoncek.csyt.construction.Construction;
import space.itoncek.csyt.construction.SimpleLocation;

public class BlockActionListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(@NotNull BlockPlaceEvent event) {
        if (event.getBlock().getWorld() == Bukkit.getWorld("construction")) {
            BuildPlace buildPlace = Construction.locationHash.get(SimpleLocation.createSimpleLocation(event.getBlock()));

            if (!Construction.blocking && (buildPlace == null || buildPlace.display)) {
                event.setCancelled(true);
                return;
            }

            if (buildPlace.active) {
                if (!buildPlace.display) {
                    if (buildPlace.matchPattern()) {
                        for (Location location : buildPlace.getLocations()) {
                            location.getBlock().setType(Material.AIR);
                        }
                        buildPlace.deactivate();
                        buildPlace.reward(event.getPlayer().getName());

                        boolean finish = true;
                        for (BuildPlace place : Construction.teams.get(buildPlace.color).buildPlaces()) {
                            if (place.active) {
                                finish = false;
                                break;
                            }
                        }

                        if (finish) {
                            for (Player p : event.getPlayer().getLocation().getNearbyPlayers(20)) {
                                p.playSound(Construction.teams.get(buildPlace.color).display().getRelLoc(2, 2).clone().add(0, 1, 0), Sound.ENTITY_PLAYER_LEVELUP, 10f, 1f);
                            }
                            Construction.teams.get(buildPlace.color).recycle();
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(@NotNull BlockBreakEvent event) {
        if (event.getBlock().getWorld() == Bukkit.getWorld("construction")) {
            BuildPlace buildPlace = Construction.locationHash.get(SimpleLocation.createSimpleLocation(event.getBlock()));

            if (!Construction.blocking && (buildPlace == null || buildPlace.display)) {
                event.setCancelled(true);
                return;
            }
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (buildPlace.active) {
                        if (!buildPlace.display) {
                            if (buildPlace.matchPattern()) {
                                for (Location location : buildPlace.getLocations()) {
                                    location.getBlock().setType(Material.AIR);
                                }
                                buildPlace.deactivate();
                                buildPlace.reward(event.getPlayer().getName());

                                boolean finish = true;
                                for (BuildPlace place : Construction.teams.get(buildPlace.color).buildPlaces()) {
                                    if (place.active) {
                                        finish = false;
                                        break;
                                    }
                                }

                                if (finish) {
                                    for (Player p : event.getPlayer().getLocation().getNearbyPlayers(20)) {
                                        p.playSound(Construction.teams.get(buildPlace.color).display().getRelLoc(2, 2).clone().add(0, 1, 0), Sound.ENTITY_PLAYER_LEVELUP, 10f, 1f);
                                    }
                                    Construction.teams.get(buildPlace.color).recycle();
                                }
                            }
                        }
                    }
                }
            }.runTaskLater(Construction.pl, 1L);
        }
    }
}