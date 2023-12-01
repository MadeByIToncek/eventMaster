package fun.csyt.csytools;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import space.itoncek.csyt.DRMLib;
import space.itoncek.csyt.UpdateLib;

public final class CSYTools extends JavaPlugin {
    public static CSYTools pl;

    @Override
    public void onEnable() {
        pl = this;
        new DRMLib() {
            @Override
            public void callback() {
                Bukkit.shutdown();
            }
        };
        UpdateLib.downloadCommitID(this.getDataFolder());
        // Plugin startup logic
        getCommand("csytools").setExecutor(new CSYTools());
        getCommand("csytools").setTabCompleter(new CSYTools());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        UpdateLib.checkForUpdates(this.getDataFolder(), "CSYTools", this.getFile());
    }
}

