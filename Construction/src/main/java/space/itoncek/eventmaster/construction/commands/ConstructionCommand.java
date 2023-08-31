package space.itoncek.eventmaster.construction.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import space.itoncek.eventmaster.construction.BuildPlace;
import space.itoncek.eventmaster.construction.utils.Orientation;
import space.itoncek.eventmaster.construction.utils.TeamColor;

import static space.itoncek.eventmaster.construction.Construction.buildPlaces;
import static space.itoncek.eventmaster.construction.config.ConfigManager.savePlaces;

public class ConstructionCommand implements CommandExecutor {
    @NotNull
    private static Orientation getOrientation(Player p) {
        Orientation ori;
        float yaw = Math.round(p.getLocation().getYaw()) % 360;
        if (yaw > -45 && yaw <= 45) {
            ori = Orientation.SOUTH;
        } else if (yaw > 45 && yaw <= 135) {
            ori = Orientation.WEST;
        } else if ((yaw > 135 && yaw <= 180) || (yaw > -180 && yaw <= -135)) {
            ori = Orientation.NORTH;
        } else {
            ori = Orientation.EAST;
        }
        return ori;
    }

    /**
     * Executes the given command, returning its success.
     * <br>
     * If false is returned, then the "usage" plugin.yml entry for this command
     * (if defined) will be sent to the player.
     *
     * @param sender  Source of the command
     * @param command Command which was executed
     * @param label   Alias of the command which was used
     * @param args    Passed command arguments
     * @return true if a valid command, otherwise false
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 2 && sender.isOp()) {
            switch (args[0]) {
                case "define" -> {
                    if (sender instanceof Player) {
                        Player p = ((Player) sender).getPlayer();
                        assert p != null;
                        Location l = p.getLocation();
                        Orientation ori = getOrientation(p);
                        TeamColor color = TeamColor.valueOf(args[1]);
                        buildPlaces.add(new BuildPlace(l,
                                ori,
                                color));
                        sender.sendMessage(ChatColor.GREEN + "Added team place of " + color.name() + " team.");
                        sender.sendMessage(ChatColor.GREEN + "Saved config in " + savePlaces(buildPlaces) + "ms");
                    }
                }
                default -> {
                    return true;
                }
            }
        }

        return true;

    }
}
