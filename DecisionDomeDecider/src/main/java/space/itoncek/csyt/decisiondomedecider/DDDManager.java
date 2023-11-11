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
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static space.itoncek.csyt.decisiondomedecider.DecisionDomeDecider.ddd;

public class DDDManager {

    public final boolean auto;
    public Minigame chosenMinigame;
    private BukkitRunnable cmdrunnable;
    private BukkitRunnable finishRunnable;
    private BukkitRunnable fillRunnable;
    public DDDManager(boolean auto) {
        this.auto = auto;
    }
    public void start() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getGameMode() != GameMode.CREATIVE && p.getGameMode() != GameMode.SPECTATOR) {
                p.setGameMode(GameMode.ADVENTURE);
                p.teleportAsync(new Location(Bukkit.getWorld("lobby"), 19, 127, 345), PlayerTeleportEvent.TeleportCause.COMMAND);
                for (PotionEffect activePotionEffect : p.getActivePotionEffects()) {
                    p.removePotionEffect(activePotionEffect.getType());
                }
                p.setFlying(false);
                p.setHealth(p.getMaxHealth());
                p.setSaturation(200);
            } else {
                p.setFlying(true);
                p.teleportAsync(new Location(Bukkit.getWorld("lobby"), 19, 130, 345), PlayerTeleportEvent.TeleportCause.COMMAND);
            }
        }
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
        chosenMinigame = Minigame.GridBuilders;
        finishRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                fill(chosenMinigame);
            }
        };

        finishRunnable.runTaskLater(ddd, 20L);
    }

    public void fill(Minigame minigame) {
        fillRunnable = new BukkitRunnable() {
            int i = 3;

            @Override
            public void run() {
                if (i > 25) {
                    startMinigame(minigame);
                    this.cancel();
                }
                for (Location location : circle(new Location(Bukkit.getWorld("lobby"), 19, 130, 345), i)) {
                    minigame.replaceBlock(location.getBlock());
                }
                i += 2;
            }
        };
        fillRunnable.runTaskTimer(ddd, 20L, 7L);
    }

    public void startMinigame(Minigame minigame) {
        cmdrunnable = new BukkitRunnable() {
            @Override
            public void run() {
                //BukkitCommand.broadcastCommandMessage(Bukkit.getConsoleSender(), minigame.cmd);
                Bukkit.broadcast(Component.text(minigame.cmd));
            }
        };
        cmdrunnable.runTaskLater(ddd, 20L);
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
        if (!cmdrunnable.isCancelled()) {
            cmdrunnable.cancel();
        }
        if (!fillRunnable.isCancelled()) {
            fillRunnable.cancel();
        }
        if (!finishRunnable.isCancelled()) {
            finishRunnable.cancel();
        }
    }
}
