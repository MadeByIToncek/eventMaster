package space.itoncek.csyt.graphicsprocessor;

import org.bukkit.plugin.java.JavaPlugin;
import space.itoncek.csyt.cg.CGProcessor;
import space.itoncek.csyt.graphicsprocessor.commands.StartCGCommand;
import space.itoncek.csyt.graphicsprocessor.commands.StartCGHelper;

public final class GraphicsProcessor extends JavaPlugin {
    private CGProcessor processor = new CGProcessor("");

    @Override
    public void onEnable() {
        // Plugin startup logic
        getCommand("startcg").setExecutor(new StartCGCommand());
        getCommand("startcg").setTabCompleter(new StartCGHelper());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
