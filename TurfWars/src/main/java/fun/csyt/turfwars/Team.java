/*
 * Made by IToncek
 *
 * Copyright (c) 2023.
 */

package fun.csyt.turfwars;

import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;

import static org.bukkit.Material.*;

public enum Team {
	coal(BLACK_CONCRETE, COAL_BLOCK, TextColor.color(60, 60, 60)),
	copper(ORANGE_CONCRETE, COPPER_BLOCK, TextColor.color(245, 141, 37)),
	iron(LIGHT_GRAY_CONCRETE, IRON_BLOCK, TextColor.color(170, 170, 170)),
	redstone(RED_CONCRETE, REDSTONE_BLOCK, TextColor.color(170, 0, 0)),
	lapis(BLUE_CONCRETE, LAPIS_BLOCK, TextColor.color(3, 77, 170)),
	diamond(LIGHT_BLUE_CONCRETE, DIAMOND_BLOCK, TextColor.color(17, 238, 235)),
	emerald(LIME_CONCRETE, EMERALD_BLOCK, TextColor.color(45, 182, 48)),
	gold(YELLOW_CONCRETE, GOLD_BLOCK, TextColor.color(255, 225, 0)),
	netherite(PURPLE_CONCRETE, NETHERITE_BLOCK, TextColor.color(160, 31, 163)),
	quartz(PINK_CONCRETE, QUARTZ_BLOCK, TextColor.color(255, 115, 220));

	public final Material ground;
	public final Material build;
	public final TextColor color;

	Team(Material ground, Material build, TextColor color) {
		this.ground = ground;
		this.build = build;
		this.color = color;
	}
}
