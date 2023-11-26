/*
 * Made by IToncek
 *
 * Copyright (c) 2023.
 */

package fun.csyt.backup;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.StringJoiner;

import static fun.csyt.backup.Backup.subtitle;

public class SetSubtitleCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender.isOp()) {
            subtitle = join(args);
            PlayerInteractionHandler.reloadTitle();
        }
        return true;
    }

    private String join(String[] args) {
        StringJoiner js = new StringJoiner(" ");
        for (String arg : args) {
            js.add(arg);
        }
        return js.toString();
    }
}
