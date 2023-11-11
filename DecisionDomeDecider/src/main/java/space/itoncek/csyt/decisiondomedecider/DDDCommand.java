/*
 * Made by IToncek
 *
 * Copyright (c) 2023.
 */

package space.itoncek.csyt.decisiondomedecider;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static space.itoncek.csyt.decisiondomedecider.DecisionDomeDecider.currentManager;

public class DDDCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender.isOp()) {
            switch (args[0]) {
                case "startAuto" -> {
                }
                case "abortAuto" -> {
                }
                case "startManual" -> {
                    if (currentManager != null) currentManager.destroy();
                    currentManager = new DDDManager(false);
                }
                case "endManual" -> {
                    currentManager.end();
                }
                case "sendManual" -> {
                    currentManager.startMinigame(currentManager.chosenMinigame);
                }
            }
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender.isOp()) return List.of("startAuto", "abortAuto", "startManual", "endManual", "fillManual");
        return List.of();
    }
}
