/*
 * Made by IToncek
 *
 * Copyright (c) 2023.
 */

package fun.csyt.csytools;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static fun.csyt.csytools.CSYTools.pl;

class CSYToolsCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.isOp()) return true;
        switch (args[0]) {
            case "launch" -> {
                //Player p = (Player) sender;
                Player p = Bukkit.getPlayer(args[1]);
                Arrow arrow = (Arrow) p.getWorld().spawnEntity(p.getLocation(), EntityType.ARROW);
                arrow.addPassenger(p);
                arrow.setVelocity(new Vector(Double.parseDouble(args[2]), Double.parseDouble(args[3]), Double.parseDouble(args[4])));

                arrow.setDamage(0f);
                arrow.setKnockbackStrength(0);
                arrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
                arrow.setSilent(true);
                new BukkitRunnable() {
                    int ttl = 200;
                    @Override
                    public void run() {
                        if (arrow.isInBlock() || ttl-- < 1) {
                            System.out.println(arrow.isInBlock() + "/" + (ttl < 1));
                            arrow.removePassenger(p);
                            arrow.remove();
                            this.cancel();
                        }
                    }
                }.runTaskTimer(pl, 10, 0);
            }
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender.isOp()) {
            return List.of("launch");
        } else return List.of();
    }
}
