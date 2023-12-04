/*
 * Made by IToncek
 *
 * Copyright (c) 2023.
 */

package fun.csyt.open;

import com.gmail.val59000mc.game.GameManager;
import fun.csyt.open.cfg.CFGMGR;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import space.itoncek.csyt.DRMLib;
import space.itoncek.csyt.UpdateLib;

import java.util.logging.Level;

public final class CSYTOpen extends JavaPlugin {
    public static GameManager gmmgr = null;
    public static CSYTOpen pl;
    public static DiscordBotController dbc;
    public AutoAssigner assigner = new AutoAssigner(CFGMGR.getConfig(getDataFolder()).getString("dburl"));

    @Override
    public void onEnable() {
        // Plugin startup logic
        new DRMLib() {
            @Override
            public void callback() {
                Bukkit.shutdown();
            }
        };
        UpdateLib.downloadCommitID(getDataFolder());
        pl = this;
        dbc = new DiscordBotController(CFGMGR.getConfig(getDataFolder()));
        getServer().getPluginManager().registerEvents(new InstanceObtainer(), this);
        getServer().getPluginManager().registerEvents(assigner, this);
        //getCommand("assign").setExecutor(new AssignCommand());
        getCommand("discord").setExecutor(new DiscordCommand());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        try {
            assigner.close();
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, e.getMessage(), e);
        }
        UpdateLib.checkForUpdates(this.getDataFolder(), "open", this.getFile());
    }
}
