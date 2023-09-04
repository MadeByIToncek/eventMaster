package space.itoncek.eventmaster.construction;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import space.itoncek.eventmaster.construction.commands.DevelopmentCommand;
import space.itoncek.eventmaster.construction.commands.GameCommand;
import space.itoncek.eventmaster.construction.commands.autofill.ConstructionAutofill;
import space.itoncek.eventmaster.construction.debug.ParticleRunnable;
import space.itoncek.eventmaster.construction.listeners.BlockActionListener;
import space.itoncek.eventmaster.construction.utils.TeamColor;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static space.itoncek.eventmaster.construction.config.ConfigManager.*;

public final class Construction extends JavaPlugin {

    public static List<BuildPlace> buildPlaces;
    public static HashMap<SimpleLocation, BuildPlace> locationHash = new HashMap<>();
    public static ParticleRunnable particles = new ParticleRunnable();
    public static List<Pattern> patterns = new ArrayList<>();
    public static HashMap<TeamColor, TeamAssets> teams = new HashMap<>();
    public static StringJoiner logOutput = new StringJoiner("\n");
    public static StringJoiner ptsOutput = new StringJoiner("\n");
    @Override
    public void onEnable() {
        // Plugin startup logic
        buildPlaces = loadPlaces();
        getServer().getPluginManager().registerEvents(new BlockActionListener(), this);
        //TODO: DEBUG STUFF, REMOVE BEFORE RELEASE!
        Objects.requireNonNull(getCommand("constGame")).setExecutor(new GameCommand());
        //getServer().getPluginManager().registerEvents(new MoveListener(), this);
        Objects.requireNonNull(getCommand("development")).setExecutor(new DevelopmentCommand());
        Objects.requireNonNull(getCommand("development")).setTabCompleter(new ConstructionAutofill());
        particles.runTaskTimer(this, 5L, 5L);

        for (BuildPlace place : buildPlaces) {
            for (Location location : place.getLocations()) {
                locationHash.put(SimpleLocation.createSimpleLocation(location), place);
            }
        }

        for (BuildPlace buildPlace : buildPlaces) {
            if (!teams.containsKey(buildPlace.color))
                teams.put(buildPlace.color, new TeamAssets(null, new ArrayList<>()));
            if (buildPlace.display) teams.get(buildPlace.color).setDisplay(buildPlace);
            else teams.get(buildPlace.color).addPlace(buildPlace);
        }
        patterns = loadPatterns();
    }


    @Override
    public void onDisable() {
        // Plugin shutdown logic
        savePlaces(buildPlaces);
        long start = System.currentTimeMillis();
        try (FileWriter fw = new FileWriter("./log.data");
             FileWriter fwa = new FileWriter("./pts.data")) {
            fw.write(logOutput.toString());
            fwa.write(ptsOutput.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Bukkit.getLogger().info(ChatColor.GREEN + "Log saved in " + (System.currentTimeMillis() - start) + "ms");
        }
    }
}
