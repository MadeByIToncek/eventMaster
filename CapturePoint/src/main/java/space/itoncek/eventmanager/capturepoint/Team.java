package space.itoncek.eventmanager.capturepoint;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Team {
    List<Player> players = new ArrayList<>();

    public Team(Player p1, Player p2, Player p3) {
        players.add(p1);
        players.add(p2);
        players.add(p3);
    }
}
