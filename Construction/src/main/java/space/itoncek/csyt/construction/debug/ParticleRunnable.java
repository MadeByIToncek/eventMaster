package space.itoncek.csyt.construction.debug;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.scheduler.BukkitRunnable;
import space.itoncek.csyt.construction.BuildPlace;
import space.itoncek.csyt.construction.Construction;

public class ParticleRunnable extends BukkitRunnable {
    public boolean enabled = false;
    @Override
    public void run() {
        if (!enabled) return;
        for (BuildPlace buildPlace : Construction.buildPlaces) {
            for (Location location : buildPlace.getLocations()) {
                location.getWorld().spawnParticle(Particle.BLOCK_MARKER,
                        location.clone().add(0, 1, 0).toBlockLocation(),
                        1,
                        buildPlace.color.material.createBlockData());
            }
        }
    }
}
