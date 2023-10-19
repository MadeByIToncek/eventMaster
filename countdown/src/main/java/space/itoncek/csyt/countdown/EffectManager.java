/*
 * Made by IToncek
 *
 * Copyright (c) 2023.
 */

package space.itoncek.csyt.countdown;

import org.bukkit.scheduler.BukkitRunnable;

import static space.itoncek.csyt.countdown.Countdown.pl;

public class EffectManager {
    public EffectManager() {

    }

    public void runTaskNumber(int i) {
        new BukkitRunnable() {
            int i = 20;

            @Override
            public void run() {
                i--;
            }
        }.runTaskTimerAsynchronously(pl, 0L, 0L);
    }
}
