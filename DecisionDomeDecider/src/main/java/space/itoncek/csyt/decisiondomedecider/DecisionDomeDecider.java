package space.itoncek.csyt.decisiondomedecider;

import org.bukkit.plugin.java.JavaPlugin;

public final class DecisionDomeDecider extends JavaPlugin {
    public static DecisionDomeDecider ddd;
    public static DDDManager currentManager;

    @Override
    public void onEnable() {
        // Plugin startup logic
        ddd = this;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
