/*
 * Made by IToncek
 *
 * Copyright (c) 2023.
 */

package space.itoncek.csyt.decisiondomedecider;

import org.bukkit.Material;
import org.bukkit.block.Block;

import static org.bukkit.Material.*;

public enum Minigame {
    LaserGame(PURPLE_CONCRETE, PURPLE_WOOL, PURPLE_CONCRETE_POWDER, "minigame_laser_start"),
    BlitzTag(RED_CONCRETE, RED_WOOL, RED_CONCRETE_POWDER, "minigame_x_start"),
    ReachTheEnd(ORANGE_CONCRETE, ORANGE_WOOL, ORANGE_CONCRETE_POWDER, "minigame_x_start"),
    ParkourMaster(YELLOW_CONCRETE, YELLOW_WOOL, YELLOW_CONCRETE_POWDER, "minigame_x_start"),
    SkyLands(GREEN_CONCRETE, GREEN_WOOL, GREEN_CONCRETE_POWDER, "minigame_x_start"),
    GridBuilders(LIGHT_BLUE_CONCRETE, LIGHT_BLUE_WOOL, LIGHT_BLUE_CONCRETE_POWDER, "minigame_x_start");
    public final String cmd;
    private final Material concrete;
    private final Material wool;
    private final Material powder;

    Minigame(Material concrete, Material wool, Material powder, String cmd) {
        this.concrete = concrete;
        this.wool = wool;
        this.powder = powder;
        this.cmd = cmd;
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

}
