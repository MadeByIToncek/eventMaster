package space.itoncek.csyt.decisiondomedecider;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import space.itoncek.csyt.DRMLib;
import space.itoncek.csyt.UpdateLib;

public final class DecisionDomeDecider extends JavaPlugin {
    public static DecisionDomeDecider ddd;
    public static DDDManager currentManager;

    @Override
    public void onEnable() {
        // Plugin startup logic
        new DRMLib() {
            @Override
            public void callback() {
                Bukkit.shutdown();
            }
        };
        UpdateLib.downloadCommitID(this.getDataFolder());
        ddd = this;
        DDDCommand dddc = new DDDCommand();
        getCommand("ddd").setTabCompleter(dddc);
        getCommand("ddd").setExecutor(dddc);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        UpdateLib.checkForUpdates(this.getDataFolder(), "DecisionDomeDecider", this.getFile());
    }
}
