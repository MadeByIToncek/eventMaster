package space.itoncek.eventmaster.construction.debug;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.scheduler.BukkitRunnable;
import space.itoncek.eventmaster.construction.BuildPlace;

import static space.itoncek.eventmaster.construction.Construction.buildPlaces;

public class ParticleRunnable extends BukkitRunnable {
    @Override
    public void run() {
        for (BuildPlace buildPlace : buildPlaces) {
            for (Location location : buildPlace.getLocations()) {
                location.getWorld().spawnParticle(Particle.BLOCK_MARKER,
                        location.clone().add(0, 1, 0).toBlockLocation(),
                        1,
                        buildPlace.color.material.createBlockData());
            }
        }
    }
}
