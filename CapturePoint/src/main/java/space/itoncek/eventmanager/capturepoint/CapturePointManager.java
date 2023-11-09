/*
 * Made by IToncek
 *
 * Copyright (c) 2023.
 */

package space.itoncek.eventmanager.capturepoint;

import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import space.itoncek.eventmanager.capturepoint.utils.BlockState;

import java.util.List;

import static space.itoncek.eventmanager.capturepoint.CapturePoint.*;

public class CapturePointManager {
    private final CapturePointInstance instance;
    private final BukkitRunnable runnable;
    private final Team red;
    private final Team blue;
    private int state = 0;
    private final int ident;

    public CapturePointManager(CapturePointInstance instance, Team red, Team blue, int ident) {
        this.instance = instance;
        runnable = new BukkitRunnable() {
            @Override
            public void run() {
                tick();
            }
        };
        this.red = red;
        this.blue = blue;
        this.ident = ident;
    }
    /**
     * Run to initialize surround checks and prepare point for game
     */
    public void init() {
        System.out.println("Starting capture point " + ident);
        runnable.runTaskTimer(pl, 0, 20);
    }

    /**
     * Run every second to process new game changes since last "tick"
     */
    public void tick() {
        boolean redSneaking = false, blueSneaking = false;
        for (Player player : red.players) {
            if (instance.isInside(player.getLocation())) {
                if (player.isSneaking()) {
                    redSneaking = true;
                }
            }
        }

        for (Player player : blue.players) {
            if (instance.isInside(player.getLocation())) {
                if (player.isSneaking()) {
                    blueSneaking = true;
                }
            }
        }

        int sign = integerify(redSneaking, blueSneaking);
        state += sign;
        int signstate = Math.round(Math.signum(state));
        int absState = Math.abs(state);

        if (absState < 9) {
            Location center = instance.center();
            int bx = center.getBlockX(), by = center.getBlockY(), bz = center.getBlockZ();
            Material base = Material.MAGENTA_STAINED_GLASS;
            Material fill = null;
            if (signstate > 0) fill = red.tc.material;
            if (signstate <= 0) fill = blue.tc.material;
            BlockState[][] pattern = blockStates[absState];
            for (int x = 0; x < 5; x++) {
                for (int z = 0; z < 5; z++) {
                    int xo = (x - 2) + bx, zo = (z - 2) + bz;
                    BlockState state = pattern[x][z];
                    setBlock(xo, by, zo, switch (state) {
                        case KEEP -> null;
                        case BASE -> base;
                        case ACCENT -> fill;
                    });
                }
            }

            for (Integer off : List.of(-2, -4, -6)) {
                setBlock(center.getBlockX(), center.getBlockY() + off, center.getBlockZ(), pattern[2][2] == BlockState.ACCENT ? fill : Material.PURPLE_STAINED_GLASS);
            }

            if (sign != 0) {
                for (Player player : ArrayUtils.addAll(red.players.toArray(new Player[0]), blue.players.toArray(new Player[0]))) {
                    player.playSound(instance.center(), Sound.BLOCK_NOTE_BLOCK_PLING, 1000f, pitchify(absState));
                }
            }

            if (absState == 8) {
                runnable.cancel();
                win(state > 0);
            }
        }
    }

    private float pitchify(int absState) {
        return absState / 4f;
    }

    private void setBlock(int x, int y, int z, Material mat) {
        if (mat != null) {
            new Location(instance.center().getWorld(), x, y, z).getBlock().setType(mat);
        }
    }

    /**
     * Ran internally to process win
     * @param red if true, assuming red won this game
     */
    private void win(boolean red) {
        for (Player player : this.red.players) {
            if (red) {
                System.out.println("Adding 45 points to" + player.getName());
                Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), "ptsadd " + player.getName() + " 40");
            }
            player.setGameMode(GameMode.SPECTATOR);
        }
        for (Player player : blue.players) {
            if (!red) {
                System.out.println("Adding 45 points to" + player.getName());
                Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), "ptsadd " + player.getName() + " 40");
            }
            player.setGameMode(GameMode.SPECTATOR);
        }
        destroy();
    }
    /**
     * Run at the end of every round to destroy this instance
     */
    public void destroy() {
        runnable.cancel();
        System.out.println("Removed manager " + this.ident + "?" + managers.remove(ident, this));
    }
    /**
     * Compare two booleans, basically XOR with more steps
     *
     * @param redSneaking  a
     * @param blueSneaking b
     * @return 1 if only a is true, -1 if only b is true and 0 is both or neither are true
     */
    private int integerify(boolean redSneaking, boolean blueSneaking) {
        int out = 0;
        if (redSneaking) out++;
        if (blueSneaking) out--;
        return out;
    }
}
