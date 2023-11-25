/*
 * Made by IToncek
 *
 * Copyright (c) 2023.
 */

package space.itoncek.eventmanager.capturepoint;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import space.itoncek.eventmanager.capturepoint.utils.TeamColor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static space.itoncek.eventmanager.capturepoint.CapturePoint.*;

public class CommandManager implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender.isOp() && args.length > 0) {
            switch (args[0]) {
                case "setup" -> {
                    if (args.length > 1) {
                        switch (args[1]) {
                            case "teams" -> {
                                teamMap.clear();
                                for (TeamColor value : TeamColor.values()) {
                                    Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), "minigame_laser_team " + value.name().toLowerCase());
                                }
                            }
                            case "teamloopback" -> {
                                TeamColor tc = TeamColor.valueOf(args[2].toUpperCase());
                                List<Player> players = new ArrayList<>(3);
                                for (int index = 3; index < args.length; index++) {
                                    if (!args[index].equals("<none>")) {
                                        players.add(playerify(args[index]));
                                    }
                                }
                                Team t = new Team(players, tc);
                                sender.sendMessage("[CP] Adding players " + Arrays.toString(Arrays.stream(players.toArray(new Player[0])).map(Player::getName).toArray()) + " to team " + tc.name().toLowerCase() + "!");
                                teamMap.put(tc, t);
                            }
                            case "create" -> {
                                if (args.length == 5) {
                                    CapturePointInstance instance = instances[Integer.parseInt(args[2])];
                                    managers.put(Integer.parseInt(args[2]), new CapturePointManager(instance,
                                            teamMap.get(TeamColor.valueOf(args[3].toUpperCase())) != null ? teamMap.get(TeamColor.valueOf(args[3].toUpperCase())) : new Team(List.of(), TeamColor.valueOf(args[3].toUpperCase())),
                                            teamMap.get(TeamColor.valueOf(args[4].toUpperCase())) != null ? teamMap.get(TeamColor.valueOf(args[4].toUpperCase())) : new Team(List.of(), TeamColor.valueOf(args[4].toUpperCase())),
                                            Integer.parseInt(args[2])));
                                }
                            }
                            case "destroy" -> {
                                if (args.length == 3) {
                                    Integer id = Integer.valueOf(args[2]);
                                    if (managers.get(id) != null) {
                                        managers.get(id).destroy();
                                        managers.remove(id);
                                    }
                                }
                            }
                            case "multi" -> {
                                if (args.length == 3) {
                                    multiplier = Float.parseFloat(args[2]);
                                }
                            }
                        }
                    }
                }
                case "start" -> {
                    for (CapturePointManager value : managers.values()) {
                        if (value != null) {
                            value.init();
                        }
                    }
                }

                case "stop" -> {
                    for (CapturePointManager value : managers.values()) {
                        if (value != null) {
                            value.destroy();
                        }
                    }
                    managers.clear();
                }

                case "disable" -> {
                    for (CapturePointManager value : managers.values()) {
                        if (value != null) {
                            value.destroy();
                        }
                    }
                    managers.clear();
                    Bukkit.getServer().getPluginManager().disablePlugin(pl);
                }
            }
        }
        return true;
    }

    private Player playerify(String arg) {
        return Bukkit.getPlayer(arg);
    }

    private Location locFromArgs(String[] args) {
        return new Location(Bukkit.getWorld(args[3]), Integer.parseInt(args[4]), Integer.parseInt(args[5]), Integer.parseInt(args[6]));
    }
}
