/*
 * Made by IToncek
 *
 * Copyright (c) 2023.
 */

package space.itoncek.eventmanager.chunklockprototype;

import me.lucko.spark.api.Spark;
import me.lucko.spark.api.statistic.StatisticWindow;
import me.lucko.spark.api.statistic.types.DoubleStatistic;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import space.itoncek.csyt.DRMLib;
import space.itoncek.eventmanager.chunklockprototype.commands.ResetCommand;
import space.itoncek.eventmanager.chunklockprototype.commands.SetupCommand;
import space.itoncek.eventmanager.chunklockprototype.listeners.BlockClickListener;
import space.itoncek.eventmanager.chunklockprototype.listeners.BlockStuffListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class ChunkLockPrototype extends JavaPlugin {

    public static final BukkitRunnable runnable = new BukkitRunnable() {
        @Override
        public void run() {
            RegisteredServiceProvider<Spark> provider = Bukkit.getServicesManager().getRegistration(Spark.class);
            if (provider != null) {
                Spark spark = provider.getProvider();
                // Get the TPS statistic (will be null on platforms that don't have ticks!)
                DoubleStatistic<StatisticWindow.TicksPerSecond> tps = spark.tps();
                DoubleStatistic<StatisticWindow.CpuUsage> cpuUsage = spark.cpuSystem();

                double cpu = cpuUsage.poll(StatisticWindow.CpuUsage.SECONDS_10);
                double tps5s = tps.poll(StatisticWindow.TicksPerSecond.SECONDS_5);

                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    onlinePlayer.sendActionBar(Component.text("TPS: " + tps5s + "   CPU: " + cpu));
                }
            }
        }
    };
    public static ChunkLockPrototype pl;
    public static List<Chunk> unlockedChunks = new ArrayList<>();

    @Override
    public void onEnable() {
        new DRMLib() {
            @Override
            public void callback() {
                Bukkit.shutdown();
            }
        };
        // Plugin startup logic
        File cache = new File("./chunklock-cache/");
        if (cache.exists()) cache.delete();
        pl = this;
        getCommand("setupcl").setExecutor(new SetupCommand());
        getCommand("resetcl").setExecutor(new ResetCommand());
        getServer().getPluginManager().registerEvents(new BlockStuffListener(), this);
        getServer().getPluginManager().registerEvents(new BlockClickListener(), this);

        runnable.runTaskTimer(this, 20L, 20L);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        File cache = new File("./chunklock-cache/");
        if (cache.exists()) cache.delete();
    }
}
