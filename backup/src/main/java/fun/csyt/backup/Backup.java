package fun.csyt.backup;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import space.itoncek.csyt.DRMLib;

public final class Backup extends JavaPlugin {
    public static Backup pl;
    public static String title = "Server má problémy";
    public static String subtitle = "Snažíme se to opravit, vyčkejte";

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
        getServer().getPluginManager().registerEvents(new PlayerInteractionHandler(), this);
        getCommand("resume").setExecutor(new ResumeCommand());
        getCommand("sett").setExecutor(new SetTitleCommand());
        getCommand("sets").setExecutor(new SetSubtitleCommand());
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
