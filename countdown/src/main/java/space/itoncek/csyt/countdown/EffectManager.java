/*
 * Made by IToncek
 *
 * Copyright (c) 2023.
 */

package space.itoncek.csyt.countdown;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;

import static space.itoncek.csyt.countdown.Countdown.pl;

public class EffectManager {
    public EffectManager() {

    }

    public static Location launchLocLUT(int i) {
        return switch (i) {
            case 0 -> getLoc(37, 117, 280);
            case 1 -> getLoc(33, 117, 282);
            case 2 -> getLoc(29, 117, 284);
            case 3 -> getLoc(25, 118, 286);
            case 4 -> getLoc(14, 118, 286);
            case 5 -> getLoc(10, 117, 284);
            case 6 -> getLoc(6, 117, 282);
            case 7 -> getLoc(2, 117, 280);
            default -> null;
        };
    }

    private static Location getLoc(int x, int y, int z) {
        return new Location(Bukkit.getWorld("lobby"), x, y, z);
    }

    public void scheduleEvents() {
        //music
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    onlinePlayer.playSound(onlinePlayer, "minecraft:intromusic", 1f, 1f);
                }
            }
        }.runTask(pl);

        //daytime shift
        new BukkitRunnable() {
            @Override
            public void run() {
                if (Bukkit.getWorld("lobby").getTime() < 21800) {
                    Bukkit.getWorld("lobby").setTime(Bukkit.getWorld("lobby").getTime() + 100);
                } else {
                    this.cancel();
                }
            }
        }.runTaskTimer(pl, 0L, 0L);

        //final firework
        new BukkitRunnable() {
            int i = 0;

            @Override
            public void run() {
                Location llc = launchLocLUT(i);
                if (llc == null) {
                    this.cancel();
                } else {
                    Firework fw = (Firework) llc.getWorld().spawnEntity(llc, EntityType.FIREWORK);
                    FireworkMeta meta = fw.getFireworkMeta();

                    meta.setPower(3);

                    meta.addEffect(FireworkEffect.builder()
                            .with(FireworkEffect.Type.BALL_LARGE)
                            .withFlicker()
                            .withTrail()
                            .withColor(Color.RED)
                            .withFade(Color.YELLOW, Color.ORANGE)
                            .build());

                    fw.setFireworkMeta(meta);

                    fw.detonate();
                }
                i++;
            }
        }.runTaskTimer(pl, 1422, 9);
    }
}
