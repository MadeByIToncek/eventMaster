package space.itoncek.eventmanager.capturepoint.utils;

import org.bukkit.Material;

public enum TeamColor {
    COAL(Material.LIGHT_GRAY_STAINED_GLASS),
    COPPER(Material.ORANGE_STAINED_GLASS),
    IRON(Material.WHITE_STAINED_GLASS),
    GOLD(Material.YELLOW_STAINED_GLASS),
    REDSTONE(Material.RED_STAINED_GLASS),
    LAPIS(Material.BLUE_STAINED_GLASS),
    EMERALD(Material.LIME_STAINED_GLASS),
    DIAMOND(Material.LIGHT_BLUE_STAINED_GLASS),
    NETHERITE(Material.PURPLE_STAINED_GLASS),
    QUARTZ(Material.PINK_STAINED_GLASS);
    public final Material material;

    TeamColor(Material material) {
        this.material = material;
    }
}
