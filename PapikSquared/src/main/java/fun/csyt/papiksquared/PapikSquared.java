/*
 * Made by IToncek
 *
 * Copyright (c) 2023.
 */

package fun.csyt.papiksquared;

import me.neznamy.tab.api.TabAPI;
import me.neznamy.tab.api.TabPlayer;
import org.bukkit.plugin.java.JavaPlugin;
import space.itoncek.csyt.comm.CommLib;

import java.sql.SQLException;

public final class PapikSquared extends JavaPlugin {
    public static CommLib lib;

    @Override
    public void onEnable() {
        // Plugin startup logic
        TabAPI.getInstance().getPlaceholderManager().registerServerPlaceholder("%papik_minigame%", 1000, this::getMinigame);
        TabAPI.getInstance().getPlaceholderManager().registerPlayerPlaceholder("%papik_team_name%", 1000, this::getTeamName);
        TabAPI.getInstance().getPlaceholderManager().registerPlayerPlaceholder("%papik_better_team_name%", 1000, this::getBetterTeamName);
        TabAPI.getInstance().getPlaceholderManager().registerPlayerPlaceholder("%papik_worse_team_name%", 1000, this::getWorseTeamName);
        try {
            lib = new CommLib(getConfig().getString("db.url"), getConfig().getString("db.user"), getConfig().getString("db.password")) {
            };
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private String getWorseTeamName(TabPlayer tabPlayer) {
        return null;
    }

    private String getBetterTeamName(TabPlayer tabPlayer) {
        return null;
    }

    private String getTeamName(TabPlayer tabPlayer) {
        return null;
    }

    private String getMinigame() {
        return "";
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        try {
            lib.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

