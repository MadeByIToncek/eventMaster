/*
 * Made by IToncek
 *
 * Copyright (c) 2023.
 */

package space.itoncek.csyt.countdown;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ACDHelper implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return switch (args.length) {
            case 1 -> List.of("settime", "auto", "config");
            case 2 -> switch (args[0]) {
                case "settime", "auto" -> List.of("<seconds>");
                case "config" -> List.of("loc1", "loc2", "loc3", "loc4", "world");
                default -> List.of();
            };
            default -> List.of();
        };
    }
}
