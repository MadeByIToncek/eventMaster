package space.itoncek.eventmaster.construction;

import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONArray;
import space.itoncek.eventmaster.construction.commands.DevelopmentCommand;
import space.itoncek.eventmaster.construction.commands.GameCommand;
import space.itoncek.eventmaster.construction.commands.autofill.ConstructionAutofill;
import space.itoncek.eventmaster.construction.debug.ParticleRunnable;
import space.itoncek.eventmaster.construction.listeners.BlockActionListener;
import space.itoncek.eventmaster.construction.utils.TeamColor;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static space.itoncek.eventmaster.construction.config.ConfigManager.*;

public final class Construction extends JavaPlugin {

    public static List<BuildPlace> buildPlaces;
    public static HashMap<SimpleLocation, BuildPlace> locationHash = new HashMap<>();
    public static ParticleRunnable particles = new ParticleRunnable();
    public static List<Pattern> patterns = new ArrayList<>();
    public static HashMap<TeamColor, TeamAssets> teams = new HashMap<>();
    public static Construction pl;
    public static JSONArray output = new JSONArray();
    public static boolean active = false;
    public static boolean blocking = false;
    public static float mutliplier = 1.0F;

    @Override
    public void onEnable() {
        pl = this;
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
        try (FileWriter fw = new FileWriter("./balance-" + LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) + ".json")) {
            fw.write(output.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        for (BuildPlace buildPlace : buildPlaces) {
            buildPlace.clr();
        }
        savePlaces(buildPlaces);
    }
}
