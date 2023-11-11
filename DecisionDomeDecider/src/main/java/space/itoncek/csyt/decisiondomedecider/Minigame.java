/*
 * Made by IToncek
 *
 * Copyright (c) 2023.
 */

package space.itoncek.csyt.decisiondomedecider;

import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.List;

import static org.bukkit.Material.*;

public enum Minigame {
    LaserGame(PURPLE_CONCRETE, PURPLE_WOOL, PURPLE_CONCRETE_POWDER, List.of("minigame_laser_start"), "\n\n\n\n\n\n\n\n\n䩄"),
    BlitzTag(RED_CONCRETE, RED_WOOL, RED_CONCRETE_POWDER, List.of("minigame_tag_start"), "\n\n\n\n\n\n\n\n䩀"),
    ReachTheEnd(ORANGE_CONCRETE, ORANGE_WOOL, ORANGE_CONCRETE_POWDER, List.of("minigame_rte_kolo1"), "\n\n\n\n\n\n\n䩅"),
    ParkourMaster(YELLOW_CONCRETE, YELLOW_WOOL, YELLOW_CONCRETE_POWDER, List.of("minigame_parkour_reset", "minigame_parkour_start"), "\n\n\n\n\n\n\n䩃"),
    SkyLands(GREEN_CONCRETE, GREEN_WOOL, GREEN_CONCRETE_POWDER, List.of("minigame_sl_start"), "\n\n\n\n\n\n\n\n䩂"),
    GridBuilders(LIGHT_BLUE_CONCRETE, LIGHT_BLUE_WOOL, LIGHT_BLUE_CONCRETE_POWDER, List.of("minigame_construction_start"), "\n\n\n\n\n䩁");
    public final List<String> cmd;
    private final String display;
    private final Material concrete;
    private final Material wool;
    private final Material powder;

    Minigame(Material concrete, Material wool, Material powder, List<String> cmd, String display) {
        this.concrete = concrete;
        this.wool = wool;
        this.powder = powder;
        this.cmd = cmd;
        this.display = display;
    }

    public boolean isBlockOfMinigame(Material type) {
        if (type.equals(concrete)) return true;
        if (type.equals(wool)) return true;
        return type.equals(powder);
    }

    ;

    public void replaceBlock(Block block) {
        if (block.getType().equals(concrete)) block.setType(BLACK_CONCRETE);
        if (block.getType().equals(wool)) block.setType(GRAY_WOOL);
        if (block.getType().equals(powder)) block.setType(GRAY_CONCRETE_POWDER);
    }

    public String getGraphic() {
        return this.display;
    }
}
