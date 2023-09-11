package space.itoncek.csyt.construction;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record Pattern(int id, List<List<Material>> pattern, List<Material> materials) implements Comparable<Pattern> {
    @Override
    public int compareTo(@NotNull Pattern o) {
        return this.id - o.id;
    }
}
