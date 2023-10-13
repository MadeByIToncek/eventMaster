package space.itoncek.eventmanager.capturepoint;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class CapturePointManager {
    private final CapturePointInstance instance;
    private final BukkitRunnable runnable;
    private final Team red;
    private final Team blue;
    private int state = 0;

    public CapturePointManager(CapturePointInstance instance, Team red, Team blue) {
        this.instance = instance;
        runnable = new BukkitRunnable() {
            @Override
            public void run() {
                tick();
            }
        };
        this.red = red;
        this.blue = blue;
    }

    //TODO
    /**
     * Run to initialize surround checks and prepare point for game
     */
    public void init() {

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

        Location center = instance.center();
        int x = center.getBlockX(), y = center.getBlockY(), z = center.getBlockZ();
        int absState = Math.abs(state);
        Material base = Material.MAGENTA_STAINED_GLASS;
        Material fill;
        if (sign > 0) fill = red.tc.material;
        if (sign <= 0) fill = blue.tc.material;

    }
    //TODO
    /**
     * Runned internally to process win
     */
    private void win() {

    }

    //TODO
    /**
     * Run at the end of every round to destroy this instance
     */
    public void destroy() {

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
