package space.itoncek.eventmanager.capturepoint;

import org.bukkit.entity.Player;
import space.itoncek.eventmanager.capturepoint.utils.TeamColor;

import java.util.ArrayList;
import java.util.List;

public class Team {
    List<Player> players = new ArrayList<>();
    TeamColor tc;

    public Team(Player p1, Player p2, Player p3, TeamColor tc) {
        players.add(p1);
        players.add(p2);
        players.add(p3);
        this.tc = tc;
    }
}
