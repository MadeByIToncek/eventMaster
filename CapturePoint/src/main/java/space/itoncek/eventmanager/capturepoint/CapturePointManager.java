/*
 * Made by IToncek
 *
 * Copyright (c) 2023.
 */

package space.itoncek.eventmanager.capturepoint;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import space.itoncek.eventmanager.capturepoint.utils.BlockState;

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

    //TODO
    /**
     * Run to initialize surround checks and prepare point for game
     */
    public void init() {
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


            if (absState == 8) {
                runnable.cancel();
                win(state > 0);
            }
        }
    }

    private void setBlock(int x, int y, int z, Material mat) {
        if (mat != null) {
            new Location(instance.center().getWorld(), x, y, z).getBlock().setType(mat);
        }
    }

    //TODO
    /**
     * Ran internally to process win
     * @param red if true, assuming red won this game
     */
    private void win(boolean red) {
        for (Player player : this.red.players) {
            if (red) {
                Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), "ptsadd " + player + " 40");
            }
            player.setGameMode(GameMode.SPECTATOR);
        }
        for (Player player : blue.players) {
            if (!red) {
                Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), "ptsadd " + player + " 40");
            }
            player.setGameMode(GameMode.SPECTATOR);
        }
    }

    //TODO
    /**
     * Run at the end of every round to destroy this instance
     */
    public void destroy() {
        runnable.cancel();
        System.out.println(managers.remove(ident, this));
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
