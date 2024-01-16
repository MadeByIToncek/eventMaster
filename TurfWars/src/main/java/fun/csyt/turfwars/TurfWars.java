/*
 * Made by IToncek
 *
 * Copyright (c) 2023.
 */

package fun.csyt.turfwars;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

public final class TurfWars extends JavaPlugin {
    public static TurfWarsRuntime turfWars;

    @Override
    public void onEnable() {
        // Plugin startup logic
        turfWars = new TurfWarsRuntime(new Location(Bukkit.getWorld("world"), -34, 67, -18), new Location(Bukkit.getWorld("world"), 12, 67, 12), this);
        getCommand("turfDebug").setExecutor(new DebugCommand());
        getCommand("turfDebug").setTabCompleter(new DebugCommand());

        getCommand("turf").setExecutor(turfWars);
        getCommand("turf").setTabCompleter(turfWars);
        getServer().getPluginManager().registerEvents(turfWars, this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
