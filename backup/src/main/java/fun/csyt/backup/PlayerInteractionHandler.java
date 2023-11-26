/*
 * Made by IToncek
 *
 * Copyright (c) 2023.
 */

package fun.csyt.backup;

import com.destroystokyo.paper.Title;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import static fun.csyt.backup.Backup.subtitle;
import static fun.csyt.backup.Backup.title;

public class PlayerInteractionHandler implements Listener {
    public static void reloadTitle() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.clearTitle();
            p.sendTitle(Title.builder()
                    .title(ChatColor.DARK_RED + title)
                    .subtitle(ChatColor.DARK_RED + subtitle)
                    .fadeIn(20)
                    .stay(3600 * 20)
                    .fadeOut(0)
                    .build());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        p.teleportAsync(new Location(Bukkit.getWorld("world_the_end"), 250, 1000, 250));
        p.setGameMode(GameMode.SPECTATOR);
        p.clearTitle();
        p.getInventory().clear();
        p.sendTitle(Title.builder()
                .title(ChatColor.DARK_RED + title)
                .subtitle(ChatColor.DARK_RED + subtitle)
                .fadeIn(20)
                .stay(3600 * 20)
                .fadeOut(0)
                .build());
        event.joinMessage(Component.empty());
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.getPlayer().clearTitle();
        event.quitMessage(Component.empty());
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        event.setCancelled(true);
    }
}
