package space.itoncek.eventmaster.construction.debug;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.scheduler.BukkitRunnable;
import space.itoncek.eventmaster.construction.BuildPlace;

import static space.itoncek.eventmaster.construction.Construction.buildPlaces;

public class ParticleRunnable extends BukkitRunnable {
    @Override
    public void run() {
        for (BuildPlace buildPlace : buildPlaces) {
            buildPlace.markerLocation.getWorld().spawnParticle(Particle.BLOCK_MARKER,
                    buildPlace.getLocations().clone().add(0, 1, 0), 30, Material.BARRIER.createBlockData());
        }
    }
}
