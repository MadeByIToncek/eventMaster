/*
 * Made by IToncek
 *
 * Copyright (c) 2023.
 */

package space.itoncek.eventmanager.capturepoint;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.itoncek.eventmanager.capturepoint.utils.TeamColor;

import java.util.ArrayList;
import java.util.List;

import static space.itoncek.eventmanager.capturepoint.CapturePoint.instances;
import static space.itoncek.eventmanager.capturepoint.CapturePoint.managers;

public class CommandHelper implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return switch (args.length) {
            case 1 -> List.of("setup", "start", "stop");
            case 2 -> switch (args[0]) {
                case "setup" ->
                        List.of("setArenaCenter", "setArenaRegion1", "setArenaRegion2", "teams", "create", "destroy");
                case "start", "stop" -> List.of("");
                default -> List.of();
            };
            default -> {
                if (args[0].equals("setup")) {
                    yield switch (args[1]) {
                        case "setArenaCenter", "setArenaRegion1", "setArenaRegion2" -> switch (args.length) {
                            case 3 -> List.of("<arena id>");
                            case 4 -> List.of("<world>");
                            case 5 -> List.of("<x>");
                            case 6 -> List.of("<y>");
                            case 7 -> List.of("<z>");
                            default -> List.of();
                        };
                        case "create" -> switch (args.length) {
                            case 3 -> {
                                List<String> out = new ArrayList<>();
                                for (Integer i : instances.keySet()) {
                                    out.add(String.valueOf(i));
                                }
                                yield out;
                            }
                            case 4, 5 -> {
                                List<String> strings = new ArrayList<>();
                                for (TeamColor value : TeamColor.values()) {
                                    strings.add(value.name().toLowerCase());
                                }
                                yield strings;
                            }
                            default -> List.of();
                        };
                        case "destroy" -> {
                            List<String> out = new ArrayList<>();
                            for (Integer i : managers.keySet()) {
                                out.add(String.valueOf(i));
                            }
                            yield out;
                        }
                        default -> List.of();
                    };
                } else {
                    yield List.of();
                }
            }
        };
    }
}
