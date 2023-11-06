/*
 * Made by IToncek
 *
 * Copyright (c) 2023.
 */

package space.itoncek.csyt.graphicscreator;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static space.itoncek.csyt.graphicscreator.GraphicsCreator.gcc;

public class GCCHandler implements CommandExecutor, TabCompleter {
    private Location pos1;
    private Location pos2;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length != 1 || !sender.isOp() || !(sender instanceof Player)) return true;

        switch (args[0]) {
            case "pos1" -> this.pos1 = ((Player) sender).getLocation();
            case "pos2" -> this.pos2 = ((Player) sender).getLocation();
            case "export" -> new BukkitRunnable() {
                @Override
                public void run() {
                    new GCCExporter() {
                        @Override
                        public void success(String info) {
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    sender.sendMessage(ChatColor.GREEN + "[GCC Success] " + info);
                                }
                            }.runTask(gcc);
                        }

                        @Override
                        public void info(String info) {
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    sender.sendMessage("[GCC Info] " + info);
                                }
                            }.runTask(gcc);
                        }

                        @Override
                        public void debug(String info) {
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    sender.sendMessage(ChatColor.GRAY + "[GCC Debug] " + info);
                                }
                            }.runTask(gcc);
                        }

                        @Override
                        public void error(String info) {
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    sender.sendMessage(ChatColor.DARK_RED + "[GCC Error] " + info);
                                }
                            }.runTask(gcc);
                        }
                    }.process(pos1, pos2);
                }
            }.runTaskAsynchronously(gcc);
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return List.of("pos1", "pos2", "export");
    }
}
