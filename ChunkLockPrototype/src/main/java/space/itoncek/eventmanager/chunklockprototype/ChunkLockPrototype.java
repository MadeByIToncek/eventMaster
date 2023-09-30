package space.itoncek.eventmanager.chunklockprototype;

import org.bukkit.Chunk;
import org.bukkit.plugin.java.JavaPlugin;
import space.itoncek.eventmanager.chunklockprototype.commands.SetupCommand;
import space.itoncek.eventmanager.chunklockprototype.listeners.BlockClickListener;
import space.itoncek.eventmanager.chunklockprototype.listeners.BlockStuffListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class ChunkLockPrototype extends JavaPlugin {

    public static ChunkLockPrototype pl;
    public static List<Chunk> unlockedChunks = new ArrayList<>();
    @Override
    public void onEnable() {
        // Plugin startup logic
        File cache = new File("./chunklock-cache/");
        if (cache.exists()) cache.delete();
        pl = this;
        getCommand("setupcl").setExecutor(new SetupCommand());
        getServer().getPluginManager().registerEvents(new BlockStuffListener(), this);
        getServer().getPluginManager().registerEvents(new BlockClickListener(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        File cache = new File("./chunklock-cache/");
        if (cache.exists()) cache.delete();
    }
}
