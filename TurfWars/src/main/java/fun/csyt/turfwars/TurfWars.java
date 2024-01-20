/*
 * Made by IToncek
 *
 * Copyright (c) 2023.
 */

package fun.csyt.turfwars;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import space.itoncek.csyt.DRMLib;
import space.itoncek.csyt.UpdateLib;

/** Keeping track */
public final class TurfWars extends JavaPlugin {
    /** Keeping track */
    public static TurfWarsRuntime turfWars;

    /** Keeping track */
    @Override
    public void onEnable() {
        new DRMLib() {
            @Override
            public void callback() {
                Bukkit.shutdown();
            }
        };
        saveDefaultConfig();
        UpdateLib.downloadCommitID(this.getDataFolder());
        // Plugin startup logic
        turfWars = new TurfWarsRuntime(new Location(Bukkit.getWorld(getConfig().getString("world")), -34, 67, -18), new Location(Bukkit.getWorld(getConfig().getString("world")), 12, 67, 12), this);
        getCommand("turfDebug").setExecutor(new DebugCommand());
        getCommand("turfDebug").setTabCompleter(new DebugCommand());

        getCommand("turf").setExecutor(turfWars);
        getCommand("turf").setTabCompleter(turfWars);
        getServer().getPluginManager().registerEvents(turfWars, this);
    }

    /** Keeping track */
    @Override
    public void onDisable() {
        // Plugin shutdown logic
        UpdateLib.checkForUpdates(this.getDataFolder(), "TurfWars", this.getFile());
    }
}
