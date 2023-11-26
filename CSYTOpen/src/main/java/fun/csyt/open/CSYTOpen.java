package fun.csyt.open;

import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.utils.PluginForwardingHandler;
import fun.csyt.open.cfg.CFGMGR;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import space.itoncek.csyt.DRMLib;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class CSYTOpen extends JavaPlugin {
    public static GameManager gmmgr = null;
    public static Logger log;
    public static CSYTOpen pl;

    @Override
    public void onEnable() {
        // Plugin startup logic
        new DRMLib() {
            @Override
            public void callback() {
                Bukkit.shutdown();
            }
        };
        pl = this;
        log = PluginForwardingHandler.createForwardingLogger(this);

        //TODO:DEBUG
        log.setLevel(Level.FINE);

        CFGMGR mgr = new CFGMGR(getDataFolder());

        log.fine("Registering events!");
        getServer().getPluginManager().registerEvents(new InstanceObtainer(), this);
        log.fine("Registering commands!");
        getCommand("assign").setExecutor(new AssignCommand());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        log.fine("unloading everything!");
    }
}
