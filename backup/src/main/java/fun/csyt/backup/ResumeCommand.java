/*
 * Made by IToncek
 *
 * Copyright (c) 2023.
 */

package fun.csyt.backup;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static fun.csyt.backup.Backup.pl;
import static fun.csyt.backup.Backup.servers;

public class ResumeCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.isOp() || args.length != 1) return true;

        Player[] playerList = Bukkit.getOnlinePlayers().toArray(new Player[0]);
        for (int i = 0; i < playerList.length; i++) {
            playerList[i].sendTitle(ChatColor.GREEN + "Event resumed, sending back!",
                    ChatColor.GREEN + "[" + (i + 0b1) + "/" + playerList.length + "]",
                    5,
                    100 + (5 * playerList.length),
                    5);
        }
        new BukkitRunnable() {
            int i = 0;

            @Override
            public void run() {
                if (i > (playerList.length - 1)) {
                    this.cancel();
                    return;
                }
                Player p = playerList[i];
                p.clearTitle();
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("Connect");
                out.writeUTF(args[0]);
                p.sendPluginMessage(pl, "BungeeCord", out.toByteArray());
                i++;
            }
        }.runTaskTimer(pl, 0L, 5L);

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.isOp()) return List.of();
        return servers.stream().toList();
    }


}