/*
 * Made by IToncek
 *
 * Copyright (c) 2023.
 */

package fun.csyt.backup;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import space.itoncek.csyt.DRMLib;

import java.util.Arrays;
import java.util.HashSet;

public final class Backup extends JavaPlugin implements @NotNull PluginMessageListener {
    public static Backup pl;
    public static String title = "Server má problémy";
    public static String subtitle = "Snažíme se to opravit, vyčkejte";
    public static HashSet<String> servers = new HashSet<>();
    public static BukkitRunnable serversManager = new BukkitRunnable() {
        @Override
        public void run() {
            if (!Bukkit.getOnlinePlayers().isEmpty()) {
                parseServers();
            }
        }
    };

    private static void parseServers() {
        Player player = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("GetServers");
        player.sendPluginMessage(pl, "Bungeecord", out.toByteArray());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

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
        serversManager.runTaskTimer(this, 20 * 10, 20 * 10);
    }

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, @NotNull byte[] message) {
        if (!channel.equals("BungeeCord")) {
            return;
        }
        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subchannel = in.readUTF();
        if (subchannel.equals("GetServers")) {
            String[] serverList = in.readUTF().split(", ");
            servers.clear();
            servers.addAll(Arrays.asList(serverList));
        }
    }
}
