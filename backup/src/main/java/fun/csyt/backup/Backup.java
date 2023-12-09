/*
 * Made by IToncek
 *
 * Copyright (c) 2023.
 */

package fun.csyt.backup;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;
import space.itoncek.csyt.DRMLib;

import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.CountDownLatch;

public final class Backup extends JavaPlugin implements @NotNull PluginMessageListener {
    public static Backup pl;
    public static String title = "Server má problémy";
    public static String subtitle = "Snažíme se to opravit, vyčkejte";
    public static CountDownLatch latch;
    public static HashSet<String> servers = new HashSet<>();
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
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, @NotNull byte[] message) {
        if (!channel.equals("BungeeCord")) {
            return;
        }
        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subchannel = in.readUTF();
        if (subchannel.equals("SomeSubChannel")) {
            String[] serverList = in.readUTF().split(", ");
            servers.addAll(Arrays.asList(serverList));
            if (latch != null && latch.getCount() > 0) latch.countDown();
        }
    }
}
