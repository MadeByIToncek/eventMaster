/*
 * Made by IToncek
 *
 * Copyright (c) 2023.
 */

package fun.csyt.open;

import com.gmail.val59000mc.exceptions.UhcPlayerDoesNotExistException;
import com.gmail.val59000mc.exceptions.UhcTeamException;
import com.gmail.val59000mc.players.UhcPlayer;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.*;
import java.util.Objects;
import java.util.logging.Level;

import static fun.csyt.open.CSYTOpen.gmmgr;
import static fun.csyt.open.CSYTOpen.pl;

public class AutoAssigner implements Listener, AutoCloseable {
    private final Connection conn;

    public AutoAssigner(String url) {
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            handle(e);
            throw new RuntimeException("AutoAssignerError");
        }
    }

    private void handle(Exception e) {
        Bukkit.getLogger().log(Level.SEVERE, e.getMessage(), e);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    UhcPlayer p = gmmgr.getPlayerManager().getUhcPlayer(event.getPlayer().getName());
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery("SELECT * FROM Players WHERE name = '%s'".formatted(p.getName()));
                    rs.next();
                    ResultSet fellows = stmt.executeQuery("SELECT * FROM Players WHERE team = '%d'".formatted(rs.getInt("team")));
                    while (fellows.next()) {
                        if (!Objects.equals(fellows.getString("name"), p.getName()) && gmmgr.getPlayerManager().getUhcPlayer(fellows.getString("name")).isOnline()) {
                            gmmgr.getPlayerManager().getUhcPlayer(fellows.getString("name")).getTeam().join(p);
                        }
                    }
                    stmt.close();
                    gmmgr.getTeamManager().squash();
                    for (UhcPlayer uhcPlayer : gmmgr.getPlayerManager().getPlayersList()) {
                        gmmgr.getScoreboardManager().updatePlayerOnTab(uhcPlayer);
                    }
                } catch (UhcPlayerDoesNotExistException | SQLException | UhcTeamException e) {
                    Bukkit.getLogger().log(Level.INFO, e.getMessage());
                }
            }
        }.runTaskLater(pl, 20L);
    }

    @Override
    public void close() throws Exception {
        conn.close();
    }
}
