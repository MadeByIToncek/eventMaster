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
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import static fun.csyt.backup.Backup.pl;

public class ResumeCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.isOp()) return true;

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
                out.writeUTF("event");
                p.sendPluginMessage(pl, "BungeeCord", out.toByteArray());
                i++;
            }
        }.runTaskTimer(pl, 2 * 20L, 20L);

        return true;
    }
}