package space.itoncek.eventmaster.construction.runnables;

import org.bukkit.scheduler.BukkitRunnable;

public class GameRunnable extends BukkitRunnable {
    long remaining = 2 * 60 * 10L;

    @Override
    public void run() {
        remaining--;
    }
}
