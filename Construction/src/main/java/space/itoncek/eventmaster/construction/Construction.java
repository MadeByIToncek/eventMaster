package space.itoncek.eventmaster.construction;

import org.bukkit.plugin.java.JavaPlugin;
import space.itoncek.eventmaster.construction.commands.ConstructionCommand;
import space.itoncek.eventmaster.construction.commands.autofill.ConstructionAutofill;
import space.itoncek.eventmaster.construction.debug.ParticleRunnable;

import java.util.List;

import static space.itoncek.eventmaster.construction.config.ConfigManager.loadPlaces;
import static space.itoncek.eventmaster.construction.config.ConfigManager.savePlaces;

public final class Construction extends JavaPlugin {

    public static List<BuildPlace> buildPlaces;

    @Override
    public void onEnable() {
        // Plugin startup logic
        buildPlaces = loadPlaces();
        //TODO: DEBUG STUFF, REMOVE BEFORE RELEASE!
        //getServer().getPluginManager().registerEvents(new MoveListener(), this);
        getCommand("construction").setExecutor(new ConstructionCommand());
        getCommand("construction").setTabCompleter(new ConstructionAutofill());
        new ParticleRunnable().runTaskTimer(this, 5L, 5L);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        savePlaces(buildPlaces);
    }
}
