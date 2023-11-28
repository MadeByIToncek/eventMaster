/*
 * Made by IToncek
 *
 * Copyright (c) 2023.
 */

package space.itoncek.csyt.construction;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONArray;
import space.itoncek.csyt.DRMLib;
import space.itoncek.csyt.UpdateLib;
import space.itoncek.csyt.construction.commands.GameCommand;
import space.itoncek.csyt.construction.listeners.BlockActionListener;
import space.itoncek.csyt.construction.utils.TeamColor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static space.itoncek.csyt.construction.config.ConfigManager.*;

public final class Construction extends JavaPlugin {

    public static List<BuildPlace> buildPlaces;
    public static final HashMap<SimpleLocation, BuildPlace> locationHash = new HashMap<>();
    public static List<Pattern> patterns = new ArrayList<>();
    public static final HashMap<TeamColor, TeamAssets> teams = new HashMap<>();
    public static Construction pl;
    public static final JSONArray output = new JSONArray();
    public static boolean active = false;
    public static boolean blocking = false;
    public static float multiplier = 1.0F;

    @Override
    public void onEnable() {
        new DRMLib() {
            @Override
            public void callback() {
                Bukkit.shutdown();
            }
        };
        UpdateLib.downloadCommitID(this.getDataFolder(), "./config/.ghcreds");
        pl = this;
        // Plugin startup logic
        buildPlaces = loadPlaces();
        getServer().getPluginManager().registerEvents(new BlockActionListener(), this);
        Objects.requireNonNull(getCommand("constgame")).setExecutor(new GameCommand());

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
        for (BuildPlace buildPlace : buildPlaces) {
            buildPlace.clr();
        }
        savePlaces(buildPlaces);
        UpdateLib.checkForUpdates(this.getDataFolder(), "Construction", this.getFile(), "./config/.ghcreds");
    }
}
