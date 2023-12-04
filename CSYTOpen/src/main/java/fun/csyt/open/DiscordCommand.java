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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.itoncek.csyt.exceptions.AlreadyRunningException;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;

import static fun.csyt.open.CSYTOpen.dbc;

public class DiscordCommand implements CommandExecutor, TabCompleter {
    @SuppressWarnings("deprecation")
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.isOp()) return true;

        switch (args[0]) {
            case "startBot" -> {
                try {
                    dbc.connect();
                } catch (InterruptedException e) {
                    Bukkit.getLogger().log(Level.SEVERE, e.getMessage());
                    sender.sendMessage(ChatColor.DARK_RED + "JDA unable to connect");
                    return true;
                } catch (SQLException e) {
                    Bukkit.getLogger().log(Level.SEVERE, e.getMessage());
                    sender.sendMessage(ChatColor.DARK_RED + "MySQL unable to connect");
                    return true;
                } catch (AlreadyRunningException e) {
                    Bukkit.getLogger().log(Level.INFO, e.getMessage());
                    sender.sendMessage(ChatColor.DARK_RED + "Bot Already running");
                    return true;
                } finally {
                    sender.sendMessage(ChatColor.GREEN + "DBC Started Succesfully");
                    sender.sendMessage(" ");
                    sender.sendMessage(dbc.getBotStatus());
                    sender.sendMessage(" ");
                }
            }
            case "stopBot" -> {
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
            case "createChannels" -> {
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
            case "destroyChannels" -> {
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
            case "cleanupChannels" -> {
                try {
                    dbc.cleanUp();
                } catch (InterruptedException e) {
                    sender.sendMessage(ChatColor.DARK_RED + "JDA processing interrupted!");
                } finally {
                    sender.sendMessage(ChatColor.GREEN + "Channels Cleaned up Succesfully");
                }
            }
            case "moveAll" -> {
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
