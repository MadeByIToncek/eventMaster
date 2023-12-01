/*
 * Made by IToncek
 *
 * Copyright (c) 2023.
 */

package fun.csyt.csytools;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static fun.csyt.csytools.CSYTools.pl;

class CSYToolsCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.isOp()) return true;
        switch (args[0]) {
            case "resetSkript" -> {
                pl.getServer().getPluginManager().disablePlugin(Bukkit.getPluginManager().getPlugin("Skript"));
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        pl.getServer().getPluginManager().enablePlugin(Bukkit.getPluginManager().getPlugin("Skript"));
                    }
                }.runTaskLater(pl, 20L);
            }
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender.isOp()) {
            return List.of("resetSkript");
        } else return List.of();
    }
}
