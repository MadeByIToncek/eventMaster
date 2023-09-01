package space.itoncek.eventmaster.construction.utils;

import org.bukkit.Material;

public enum TeamColor {
    COAL(Material.COAL_BLOCK),
    COPPER(Material.WAXED_WEATHERED_CUT_COPPER),
    IRON(Material.IRON_BLOCK),
    GOLD(Material.GOLD_BLOCK),
    REDSTONE(Material.REDSTONE_BLOCK),
    LAPIS(Material.LAPIS_BLOCK),
    EMERALD(Material.EMERALD_BLOCK),
    DIAMOND(Material.DIAMOND_BLOCK),
    NETHERITE(Material.NETHERITE_BLOCK),
    QUARTZ(Material.QUARTZ_BLOCK);
    public final Material material;

    TeamColor(Material material) {
        this.material = material;
    }
}
