/*
 * Made by IToncek
 *
 * Copyright (c) 2023.
 */

package space.itoncek.eventmanager.capturepoint;

import org.bukkit.entity.Player;
import space.itoncek.eventmanager.capturepoint.utils.TeamColor;

import java.util.List;

public class Team {
    final List<Player> players;
    final TeamColor tc;

    public Team(List<Player> players, TeamColor tc) {
        this.players = players;
        this.tc = tc;
    }
}
