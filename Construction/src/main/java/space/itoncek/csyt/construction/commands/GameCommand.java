package space.itoncek.csyt.construction.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import space.itoncek.csyt.construction.BuildPlace;
import space.itoncek.csyt.construction.TeamAssets;
import space.itoncek.csyt.construction.utils.TeamColor;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import static space.itoncek.csyt.construction.Construction.*;

public class GameCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0 || !sender.isOp()) return true;
        switch (args[0].toLowerCase()) {
            case "start" -> {
                if (!active) {
                    for (Map.Entry<TeamColor, TeamAssets> e : teams.entrySet()) {
                        e.getValue().players.clear();
                        sendCmd("minigame_construction_fetchteam " + e.getKey().name().toLowerCase());
                    }
                    for (Map.Entry<TeamColor, TeamAssets> entry : teams.entrySet()) {
                        entry.getValue().reset();
                    }
                    active = true;
                }
            }
            case "stop" -> {
                String filename = "./balance-" + System.currentTimeMillis() + ".json";
                if (active) {
                    for (BuildPlace place : buildPlaces) {
                        place.end();
                        active = false;
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            p.getInventory().clear();
                        }
                    }
                }
                try (FileWriter fw = new FileWriter(filename)) {
                    fw.write(output.toString(4));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } finally {
                    output.clear();
                }
            }
            case "fetchteam" -> {
                TeamAssets team = teams.get(TeamColor.valueOf(args[1].toUpperCase()));
                for (int i = 2; i < args.length; i++) {
                    if (!args[i].equals("<none>") && Bukkit.getPlayer(args[i]) != null) {
                        team.players.add(Bukkit.getPlayer(args[i]));
                    }
                }
                try {
                    Bukkit.getLogger().info("Synced team " + TeamColor.valueOf(args[1].toUpperCase()) + " with players " + Arrays.toString(Arrays.stream(team.players.toArray(new Player[0])).map(CommandSender::name).toArray()));
                } catch (Exception ignored) {

                }
            }
            case "reloadteams" -> {
                for (Map.Entry<TeamColor, TeamAssets> e : teams.entrySet()) {
                    e.getValue().players.clear();
                    sendCmd("minigame_construction_fetchteam " + e.getKey().name().toLowerCase());
                }
            }
            case "setmutliplier" -> {
                sender.sendMessage("Multiplier set to " + Float.parseFloat(args[1]));
                mutliplier = Float.parseFloat(args[1]);
            }
        }
        return true;
    }

    private void sendCmd(String cmd) {
        //Bukkit.getLogger().info(cmd);
        Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), cmd);
    }
}
