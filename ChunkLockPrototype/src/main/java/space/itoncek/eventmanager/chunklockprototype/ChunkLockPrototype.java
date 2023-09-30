package space.itoncek.eventmanager.chunklockprototype;

import org.bukkit.plugin.java.JavaPlugin;

public final class ChunkLockPrototype extends JavaPlugin {

    public static ChunkLockPrototype pl;

    @Override
    public void onEnable() {
        // Plugin startup logic
        pl = this;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
