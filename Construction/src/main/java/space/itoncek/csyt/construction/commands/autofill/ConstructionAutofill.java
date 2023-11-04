/*
 * Made by IToncek
 *
 * Copyright (c) 2023.
 */

package space.itoncek.csyt.construction.commands.autofill;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.itoncek.csyt.construction.utils.TeamColor;

import java.util.ArrayList;
import java.util.List;

public class ConstructionAutofill implements TabCompleter {
    /**
     * Requests a list of possible completions for a command argument.
     *
     * @param sender  Source of the command.  For players tab-completing a
     *                command inside a command block, this will be the player, not
     *                the command block.
     * @param command Command which was executed
     * @param label   Alias of the command which was used
     * @param args    The arguments passed to the command, including final
     *                partial argument to be completed
     * @return A List of possible completions for the final argument, or null
     * to default to the command executor
     */
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return switch (args.length) {
            case 1 -> List.of("define", "debug");
            case 2 -> switch (args[0].toLowerCase()) {
                case "define" -> {
                    List<String> teams = new ArrayList<>();
                    for (TeamColor value : TeamColor.values()) {
                        teams.add(value.name());
                    }
                    yield teams;
                }
                case "debug" -> List.of("replaceAll", "replaceDisplay", "replaceBuild", "clr", "toggleParticles");
                default -> List.of();
            };
            case 3 -> switch (args[0].toLowerCase()) {
                case "define" -> List.of("true", "false");
                case "debug" -> {
                    List<String> ret = new ArrayList<>();
                    for (Material value : Material.values()) {
                        ret.add(value.name());
                    }
                    yield ret;
                }
                default -> List.of();
            };

            default -> List.of();
        };
    }
}
