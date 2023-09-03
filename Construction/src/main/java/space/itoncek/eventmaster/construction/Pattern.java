package space.itoncek.eventmaster.construction;

import org.bukkit.Material;

import java.util.List;

public record Pattern(String name, List<List<Material>> pattern) {
}
