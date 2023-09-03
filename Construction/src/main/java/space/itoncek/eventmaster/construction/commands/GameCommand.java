package space.itoncek.eventmaster.construction.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import space.itoncek.eventmaster.construction.BuildPlace;

import static space.itoncek.eventmaster.construction.Construction.buildPlaces;

public class GameCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0 || !sender.isOp()) return true;
        switch (args[0]) {
            case "start" -> {
                for (BuildPlace place : buildPlaces) {
                    place.setPattern(0);
                }
            }
        }
        return true;
    }
}
