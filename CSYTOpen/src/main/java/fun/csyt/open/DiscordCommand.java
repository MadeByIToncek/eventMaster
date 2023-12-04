/*
 * Made by IToncek
 *
 * Copyright (c) 2023.
 */

package fun.csyt.open;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.itoncek.csyt.exceptions.AlreadyRunningException;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;

import static fun.csyt.open.CSYTOpen.dbc;
import static fun.csyt.open.CSYTOpen.pl;

public class DiscordCommand implements CommandExecutor, TabCompleter {
    @SuppressWarnings("deprecation")
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.isOp()) return true;

        switch (args[0]) {
            case "startBot" -> {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        try {
                            dbc.connect();
                        } catch (InterruptedException e) {
                            Bukkit.getLogger().log(Level.SEVERE, e.getMessage());
                            sender.sendMessage(ChatColor.DARK_RED + "JDA unable to connect");
                        } catch (SQLException e) {
                            Bukkit.getLogger().log(Level.SEVERE, e.getMessage());
                            sender.sendMessage(ChatColor.DARK_RED + "MySQL unable to connect");
                        } catch (AlreadyRunningException e) {
                            Bukkit.getLogger().log(Level.INFO, e.getMessage());
                            sender.sendMessage(ChatColor.DARK_RED + "Bot Already running");
                        } finally {
                            sender.sendMessage(ChatColor.GREEN + "DBC Started Succesfully");
                            sender.sendMessage(" ");
                            sender.sendMessage(dbc.getBotStatus());
                            sender.sendMessage(" ");
                        }
                    }
                }.runTaskAsynchronously(pl);
            }
            case "stopBot" -> {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        try {
                            dbc.close();
                        } catch (SQLException e) {
                            Bukkit.getLogger().log(Level.SEVERE, e.getMessage());
                            sender.sendMessage(ChatColor.DARK_RED + "MySQL DB Access Error!");
                        } catch (InterruptedException e) {
                            sender.sendMessage(ChatColor.DARK_RED + "JDA shutdown interrupted!");
                        } finally {
                            sender.sendMessage(ChatColor.GREEN + "DBC Closed Succesfully");
                        }
                    }
                }.runTaskAsynchronously(pl);
            }
            case "createChannels" -> {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        try {
                            dbc.createChannels();
                        } catch (SQLException e) {
                            sender.sendMessage(ChatColor.DARK_RED + "MySQL DB Access Error!");
                        } catch (InterruptedException e) {
                            sender.sendMessage(ChatColor.DARK_RED + "JDA processing interrupted!");
                        } finally {
                            sender.sendMessage(ChatColor.GREEN + "Channels Created Succesfully");
                        }
                    }
                }.runTaskAsynchronously(pl);
            }
            case "destroyChannels" -> {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        try {
                            dbc.deleteChannels();
                        } catch (SQLException e) {
                            sender.sendMessage(ChatColor.DARK_RED + "MySQL DB Access Error!");
                        } catch (InterruptedException e) {
                            sender.sendMessage(ChatColor.DARK_RED + "JDA processing interrupted!");
                        } finally {
                            sender.sendMessage(ChatColor.GREEN + "Channels Deleted Succesfully");
                        }
                    }
                }.runTaskAsynchronously(pl);
            }
            case "cleanupChannels" -> {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        try {
                            dbc.cleanUp();
                        } catch (InterruptedException e) {
                            sender.sendMessage(ChatColor.DARK_RED + "JDA processing interrupted!");
                        } finally {
                            sender.sendMessage(ChatColor.GREEN + "Channels Cleaned up Succesfully");
                        }
                    }
                }.runTaskAsynchronously(pl);
            }
            case "moveAll" -> {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        try {
                            dbc.moveAll((s -> sender.sendMessage(ChatColor.DARK_RED + s)));
                        } catch (SQLException e) {
                            sender.sendMessage(ChatColor.DARK_RED + "MySQL DB Access Error!");
                        } catch (InterruptedException e) {
                            sender.sendMessage(ChatColor.DARK_RED + "JDA processing interrupted!");
                        } finally {
                            sender.sendMessage(ChatColor.GREEN + "Members moved succesfully");
                        }
                    }
                }.runTaskAsynchronously(pl);
            }
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.isOp()) return List.of();
        //noinspection SwitchStatementWithTooFewBranches
        return switch (args.length) {
            case 0 -> List.of("startBot", "stopBot", "createChannels", "destroyChannels", "cleanupChannels");
            default -> List.of();
        };
    }
}
