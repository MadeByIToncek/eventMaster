package space.itoncek.csyt.construction;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record Pattern(int id, Material[][] pattern, List<Material> materials) implements Comparable<Pattern> {
    private static int remainingPoints = 40;
    @Override
    public int compareTo(@NotNull Pattern o) {
        return this.id - o.id;
    }

    public String award(String player) {
        String cmd = "ptsadd " + player + " " + remainingPoints;
        remainingPoints--;
        return cmd;
    }
}
