package space.itoncek.csyt.decisiondomedecider;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import space.itoncek.csyt.DRMLib;
import space.itoncek.csyt.UpdateLib;

import java.util.ArrayList;
import java.util.List;

public final class DecisionDomeDecider extends JavaPlugin {
    public static DecisionDomeDecider ddd;
    public static DDDManager currentManager;
    public static List<BukkitTask> taskList = new ArrayList<>();

    @Override
    public void onEnable() {
        // Plugin startup logic
        new DRMLib() {
            @Override
            public void callback() {
                Bukkit.shutdown();
            }
        };
        UpdateLib.downloadCommitID(this.getDataFolder(), "./config/.ghcreds");
        ddd = this;
        DDDCommand dddc = new DDDCommand();
        getCommand("ddd").setTabCompleter(dddc);
        getCommand("ddd").setExecutor(dddc);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        UpdateLib.checkForUpdates(this.getDataFolder(), "DecisionDomeDecider", this.getFile(), "./config/.ghcreds");
    }
}
