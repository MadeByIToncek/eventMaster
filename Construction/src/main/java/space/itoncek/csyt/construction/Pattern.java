/*
 * Made by IToncek
 *
 * Copyright (c) 2023.
 */

package space.itoncek.csyt.construction;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static space.itoncek.csyt.construction.Construction.multiplier;

public final class Pattern implements Comparable<Pattern> {
    private final int id;
    private final Material[][] pattern;
    private final List<Material> materials;
    private int remainingPoints = 30;

    public Pattern(int id, Material[][] pattern, List<Material> materials) {
        this.id = id;
        this.pattern = pattern;
        this.materials = materials;
    }

    public String award(String player) {
        String cmd = "ptsadd " + player + " " + remainingPoints * multiplier;
        remainingPoints--;
        return cmd;
    }

    public Material[][] pattern() {
        return pattern;
    }

    public List<Material> materials() {
        return materials;
    }

    @Override
    public int compareTo(@NotNull Pattern o) {
        return this.id - o.id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Pattern) obj;
        return this.id == that.id &&
                Arrays.deepEquals(this.pattern, that.pattern) &&
                Objects.equals(this.materials, that.materials);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, Arrays.deepHashCode(pattern), materials);
    }

    @Override
    public String toString() {
        return "Pattern[" +
                "id=" + id + ", " +
                "pattern=" + Arrays.deepToString(pattern) + ", " +
                "materials=" + materials + ']';
    }

}
