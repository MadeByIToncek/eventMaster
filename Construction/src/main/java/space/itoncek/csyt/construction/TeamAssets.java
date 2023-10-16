/*
 * Made by IToncek
 *
 * Copyright (c) 2023.
 */

package space.itoncek.csyt.construction;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class TeamAssets {
    private BuildPlace display;
    public List<Player> players = new ArrayList<>();
    private final List<BuildPlace> buildPlaces;
    private int currentID = -1;
    public TeamAssets(BuildPlace display, List<BuildPlace> buildPlaces) {
        this.display = display;
        this.buildPlaces = buildPlaces;
    }

    public BuildPlace display() {
        return display;
    }

    public List<BuildPlace> buildPlaces() {
        return buildPlaces;
    }

    public void setDisplay(BuildPlace display) {
        this.display = display;
    }

    public void addPlace(BuildPlace place) {
        buildPlaces.add(place);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (TeamAssets) obj;
        return Objects.equals(this.display, that.display) &&
                Objects.equals(this.buildPlaces, that.buildPlaces);
    }

    @Override
    public int hashCode() {
        return Objects.hash(display, buildPlaces);
    }

    @Override
    public String toString() {
        return "TeamAssets[" +
                "display=" + display + ", " +
                "buildPlaces=" + buildPlaces + ']';
    }

    public static ItemStack enchant(Material mat) {
        ItemStack itemStack = new ItemStack(mat);
        itemStack.addEnchantment(Enchantment.SILK_TOUCH, 1);
        itemStack.addUnsafeEnchantment(Enchantment.DIG_SPEED, 100);
        itemStack.addUnsafeEnchantment(Enchantment.DURABILITY, 100);
        return itemStack;
    }

    public void reset() {
        currentID = -1;
        recycle();
    }
    //todo
    public void recycle() {
        int nextID = currentID + 1;

        if (nextID > Construction.patterns.size()) {
            finish();
            return;
        }

        display.setPattern(nextID);
        for (BuildPlace buildPlace : buildPlaces) {
            buildPlace.setPattern(nextID);
        }

        for (Player player : players) {
            player.getInventory().clear();
            player.getInventory().addItem(enchant(Material.DIAMOND_PICKAXE));
            player.getInventory().addItem(enchant(Material.DIAMOND_AXE));
            player.getInventory().addItem(enchant(Material.DIAMOND_SHOVEL));
        }

        for (Material material : Construction.patterns.get(nextID).materials()) {
            for (Player player : players) {
                player.getInventory().addItem(new ItemStack(material, 64));
            }
        }

        for (Player p : display.getRelLoc(2, 2).getNearbyPlayers(20)) {
            p.playSound(display.getRelLoc(2, 2).clone().add(0, 1, 0), Sound.ENTITY_PLAYER_LEVELUP, 10f, 1f);
        }
        currentID = nextID;
    }

    public void finish() {

    }
}
