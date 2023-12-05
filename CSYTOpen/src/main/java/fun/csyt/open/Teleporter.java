/*
 * Made by IToncek
 *
 * Copyright (c) 2023.
 */

package fun.csyt.open;

import com.gmail.val59000mc.events.UhcTimeEvent;
import com.gmail.val59000mc.exceptions.UhcPlayerNotOnlineException;
import com.gmail.val59000mc.players.UhcPlayer;
import com.gmail.val59000mc.players.UhcTeam;
import com.gmail.val59000mc.utils.LocationUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.logging.Level;

public class Teleporter implements Listener {
    private boolean triggeredTP = false;
    private boolean triggeredAlert = false;

    @EventHandler(ignoreCancelled = true)
    public void onUhcTime(UhcTimeEvent event) {
        if (event.getTotalTime() > 1100 && !triggeredAlert) {
            triggeredAlert = true;
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.sendActionBar(Component.text(ChatColor.GOLD + "Teleport in 1 minute!"));
                p.playSound(p, Sound.ENTITY_WARDEN_SONIC_BOOM, 10f, 1f);
            }
        }
        if (event.getTotalTime() > 1200 && !triggeredTP) {
            triggeredTP = true;
            for (UhcTeam team : event.getGameManager().getTeamManager().getUhcTeams()) {
                try {
                    Location leaderLoc = team.getLeader().getPlayer().getLocation();
                    Location surface = LocationUtils.getSurfaceBlockAt(leaderLoc).getLocation();
                    for (UhcPlayer p : team.getMembers()) {
                        p.getPlayer().teleportAsync(LocationUtils.withSameDirection(surface, p.getPlayer()));
                        p.getPlayer().sendTitle(ChatColor.GOLD + "Teleported to leader", ChatColor.GOLD + "PVP will be enabled in 1 minute!");
                        p.getPlayer().playSound(p.getPlayer(), Sound.ENTITY_WARDEN_SONIC_BOOM, 10f, 1f);
                    }
                } catch (UhcPlayerNotOnlineException e) {
                    Bukkit.getLogger().log(Level.SEVERE, e.getMessage(), e);
                }
            }
        }
    }
}
