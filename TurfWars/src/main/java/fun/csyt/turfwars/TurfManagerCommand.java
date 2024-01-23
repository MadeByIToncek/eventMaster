/*
 * Made by IToncek
 *
 * Copyright (c) 2023.
 */

package fun.csyt.turfwars;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static fun.csyt.turfwars.TurfWars.turfWars;

public class TurfManagerCommand implements CommandExecutor, TabCompleter {
    private final TurfWars pl;
    public TurfManagerCommand(TurfWars pl) {
        this.pl = pl;
    }
    /** Keeping track */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender.isOp()) {
            switch (args[0]) {
                case "enable" -> {
                    if (turfWars != null) {
                        turfWars.close();
                        turfWars = null;
                    }
                    turfWars = new TurfWarsRuntime(new Location(Bukkit.getWorld(pl.getConfig().getString("world")), -34, 67, -18), new Location(Bukkit.getWorld(pl.getConfig().getString("world")), 12, 67, 12), pl);
                }
                case "disable" -> {
                    if (turfWars != null) {
                        turfWars.close();
                        turfWars = null;
                    }
                }
                case "loadPreset" -> {
                    if (turfWars != null) {
                        turfWars.loadTeam(args[1].equals("red"), args[2], sender);
                    }
                }
            }
        }

        return true;
    }

    /** Keeping track */
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender.isOp()) {
            return switch (args.length) {
                case 1 -> List.of("enable", "disable", "loadPreset");
                case 2 -> {
                    if (args[1].equals("loadPreset")) {
                        yield List.of("red", "blue");
                    } else yield List.of();
                }
                case 3 -> {
                    if (args[1].equals("loadPreset")) {
                        yield List.of("coal", "copper", "iron", "redstone", "lapis", "diamond", "emerald", "gold", "netherite", "quartz");
                    } else yield List.of();
                }
                default -> List.of();
            };
        }
        return List.of();
    }
}
