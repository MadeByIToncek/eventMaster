package space.itoncek.csyt.graphicscreator;

import org.bukkit.plugin.java.JavaPlugin;

public final class GraphicsCreator extends JavaPlugin {
    public static GraphicsCreator gcc;

    @Override
    public void onEnable() {

        gcc = this;
        // Plugin startup logic
        GCCHandler gcc = new GCCHandler();
        getCommand("gcc").setExecutor(gcc);
        getCommand("gcc").setTabCompleter(gcc);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }


}
