/*
 * Made by IToncek
 *
 * Copyright (c) 2023.
 */

package space.itoncek.csyt.decisiondomedecider;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.security.SecureRandom;
import java.util.*;

import static space.itoncek.csyt.decisiondomedecider.DecisionDomeDecider.ddd;
import static space.itoncek.csyt.decisiondomedecider.DecisionDomeDecider.taskList;

public class DDDManager {

    public final boolean auto;
    public Minigame chosenMinigame;
    public DDDManager(boolean auto) {
        this.auto = auto;
    }
    public void start() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getGameMode() != GameMode.CREATIVE && p.getGameMode() != GameMode.SPECTATOR) {
                p.setGameMode(GameMode.ADVENTURE);
                p.teleportAsync(new Location(Bukkit.getWorld("lobby"), 19, 130, 345), PlayerTeleportEvent.TeleportCause.COMMAND);
                for (PotionEffect activePotionEffect : p.getActivePotionEffects()) {
                    p.removePotionEffect(activePotionEffect.getType());
                }
                p.setFlying(false);
                p.setHealth(p.getMaxHealth());
                p.setSaturation(200);
            } else {
                p.setFlying(true);
                p.teleportAsync(new Location(Bukkit.getWorld("lobby"), 19, 133, 345), PlayerTeleportEvent.TeleportCause.COMMAND);
            }
        }
    }

    public static <K, V extends Comparable<V>> Map.Entry<K, V> maxMap(Map<K, V> map) {
        // To store the result
        Map.Entry<K, V> entryWithMaxValue = null;
        // Iterate in the map to find the required entry
        for (Map.Entry<K, V> currentEntry : map.entrySet()) {
            if (entryWithMaxValue == null || currentEntry.getValue().compareTo(entryWithMaxValue.getValue()) > 0) {
                entryWithMaxValue = currentEntry;
            }
        }

        return entryWithMaxValue;
    }

    public static List<Map.Entry<Minigame, Integer>> uniqueMaxMap(Map<Minigame, Integer> map) {
        // To store the result
        List<Map.Entry<Minigame, Integer>> entryWithMaxValue = new ArrayList<>();
        int compare = Integer.MIN_VALUE;
        // Iterate in the map to find the required entry
        for (Map.Entry<Minigame, Integer> currentEntry : map.entrySet()) {
            if (compare == Integer.MIN_VALUE || compare < currentEntry.getValue()) {
                entryWithMaxValue.clear();
                compare = currentEntry.getValue();
                entryWithMaxValue.add(currentEntry);
            } else if (compare == currentEntry.getValue()) {
                entryWithMaxValue.add(currentEntry);
            }
        }

        return entryWithMaxValue;
    }

    public void end() {
        HashMap<Minigame, Integer> results = new HashMap<>();
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getGameMode() != GameMode.CREATIVE && p.getGameMode() != GameMode.SPECTATOR) {
                Block block = p.getLocation().subtract(0, 1, 0).getBlock();
                Minigame minigame = null;
                for (Minigame value : Minigame.values()) {
                    if (value.isBlockOfMinigame(block.getType())) {
                        minigame = value;
                        break;
                    }
                }
                if (minigame != null) {
                    results.put(minigame, results.getOrDefault(minigame, 0) + 1);
                }
            }
        }
        System.out.println(results);
        Minigame resultat = process(results);
        Bukkit.broadcast(Component.text(resultat.getGraphic()));
        chosenMinigame = resultat;
        if (auto) {
            taskList.add(new BukkitRunnable() {
                @Override
                public void run() {
                    fill();
                }
            }.runTaskLater(ddd, 20L));
        }
    }

    private Minigame process(HashMap<Minigame, Integer> results) {
        if (results.values().stream().distinct().count() != results.size()) {
            SecureRandom rnd = new SecureRandom();
            List<Map.Entry<Minigame, Integer>> entries = uniqueMaxMap(results);
//            System.out.println(entries);
//            System.out.println(entries.size());
//            System.out.println(rnd.nextInt(entries.size()));
//            System.out.println(entries.get(rnd.nextInt(entries.size())).getKey());
            return entries.get(rnd.nextInt(entries.size())).getKey();
        } else {
            return maxMap(results).getKey();
        }
    }


    public void fill() {
        taskList.add(new BukkitRunnable() {
            int i = 3;

            @Override
            public void run() {
                if (i > 25) {
                    if (auto) startMinigame();
                    this.cancel();
                }
                //Bukkit.broadcast(Component.text("Filling radius " + i));
                for (Location location : circle(new Location(Bukkit.getWorld("lobby"), 19, 127, 345), i)) {
                    chosenMinigame.replaceBlock(location.getBlock());
                }
                i += 2;
            }
        }.runTaskTimer(ddd, 20L, 5L));
    }


    public void startMinigame() {
        taskList.add(new BukkitRunnable() {
            @Override
            public void run() {
                for (String s : chosenMinigame.cmd) {
                    BukkitCommand.broadcastCommandMessage(Bukkit.getConsoleSender(), s);
                    //System.out.println(s);
                }
            }
        }.runTask(ddd));
    }

    public Set<Location> circle(Location location, int radius) {
        Set<Location> blocks = new HashSet<>();
        World world = location.getWorld();
        int X = location.getBlockX();
        int Y = location.getBlockY();
        int Z = location.getBlockZ();
        int radiusSquared = radius * radius;

        for (int x = X - radius; x <= X + radius; x++) {
            for (int z = Z - radius; z <= Z + radius; z++) {
                if ((X - x) * (X - x) + (Z - z) * (Z - z) <= radiusSquared) {
                    Location block = new Location(world, x, Y, z);
                    blocks.add(block);
                }
            }
        }
        return blocks;

    }

    public void destroy() {
        taskList.forEach(BukkitTask::cancel);
    }
}
