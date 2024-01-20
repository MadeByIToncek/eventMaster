/*
 * Made by IToncek
 *
 * Copyright (c) 2023.
 */

package fun.csyt.turfwars;

import org.bukkit.Bukkit;
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
        getCommand("turfmanager").setExecutor(new TurfManagerCommand(this));
        getCommand("turfmanager").setTabCompleter(new TurfManagerCommand(this));

    }

    /** Keeping track */
    @Override
    public void onDisable() {
        // Plugin shutdown logic
        UpdateLib.checkForUpdates(this.getDataFolder(), "TurfWars", this.getFile());
    }
}
