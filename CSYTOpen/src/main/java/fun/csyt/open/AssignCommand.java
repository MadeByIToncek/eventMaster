/*
 * Made by IToncek
 *
 * Copyright (c) 2023.
 */

package fun.csyt.open;

import com.gmail.val59000mc.exceptions.UhcPlayerDoesNotExistException;
import com.gmail.val59000mc.exceptions.UhcPlayerNotOnlineException;
import com.gmail.val59000mc.exceptions.UhcTeamException;
import com.gmail.val59000mc.players.PlayerState;
import com.gmail.val59000mc.players.UhcPlayer;
import com.gmail.val59000mc.players.UhcTeam;
import fun.csyt.open.cfg.CFGMGR;
import fun.csyt.open.meta.TeamMeta;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import static fun.csyt.open.CSYTOpen.gmmgr;
import static fun.csyt.open.CSYTOpen.pl;

@Deprecated(since = "03. 12. 2023", forRemoval = true)
public class AssignCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.isOp()) return true;

        if (gmmgr.getGameState() == null) {
            sender.sendMessage(ChatColor.DARK_RED + "GameManager is not yet registered!");
        }

        resetTeams();
        loadTeams(sender);

        return true;
    }

    private void loadTeams(CommandSender sender) {
        HashMap<TeamMeta, List<String>> map = CFGMGR.readTeams(pl.getDataFolder());

        if (map == null) {
            sender.sendMessage("Unable to parse config, please check again that it exists!");
        }

        map.forEach((meta, players) -> {//convert players to list of UhcPlayers
            List<UhcPlayer> uhcPlayers = new ArrayList<>();
            for (String player : players) {
                try {
                    uhcPlayers.add(gmmgr.getPlayerManager().getUhcPlayer(player));
                } catch (UhcPlayerDoesNotExistException e) {
                    Bukkit.getLogger().log(Level.SEVERE, e.getMessage(), e);
                }
            }

            if (meta.spectator()) {
                for (UhcPlayer p : uhcPlayers) {
                    try {
                        setPlayerSpectating(p.getPlayer(), p);
                    } catch (UhcPlayerNotOnlineException e) {
                        Bukkit.getLogger().log(Level.SEVERE, e.getMessage(), e);
                    }
                }
            } else {
                //Modify team values
                UhcTeam team = new UhcTeam(uhcPlayers.get(0));

                //Add other players
                for (UhcPlayer p : uhcPlayers) {
                    if (!team.contains(p)) {
                        try {
                            team.join(p);
                        } catch (UhcTeamException e) {
                            Bukkit.getLogger().log(Level.SEVERE, e.getMessage(), e);
                        }
                    }
                    gmmgr.getScoreboardManager().updatePlayerOnTab(p);
                }
            }
        });
    }

    private void setPlayerSpectating(Player player, UhcPlayer uhcPlayer) {
        uhcPlayer.setState(PlayerState.DEAD);

        // Clear lobby items
        player.getInventory().clear();

        if (!uhcPlayer.getTeam().isSolo()) {
            try {
                UhcTeam oldTeam = uhcPlayer.getTeam();
                oldTeam.leave(uhcPlayer);
                gmmgr.getScoreboardManager().updatePlayerOnTab(uhcPlayer);
                gmmgr.getScoreboardManager().updateTeamOnTab(oldTeam);
            } catch (UhcTeamException e) {
                Bukkit.getLogger().log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }

    private void resetTeams() {
        for (UhcTeam t : gmmgr.getTeamManager().getUhcTeams()) {
            t.getLeader();
            for (UhcPlayer m : t.getMembers()) {
                try {
                    if (t.getLeader() != m) t.leave(m);
                } catch (UhcTeamException e) {
                    Bukkit.getLogger().log(Level.SEVERE, e.getMessage(), e);
                }
            }
        }
    }
}
