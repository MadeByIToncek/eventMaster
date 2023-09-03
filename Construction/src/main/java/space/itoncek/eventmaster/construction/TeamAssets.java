package space.itoncek.eventmaster.construction;

import org.bukkit.Material;

import java.util.List;
import java.util.Objects;

public final class TeamAssets {
    private BuildPlace display;
    private List<BuildPlace> buildPlaces;

    public TeamAssets(BuildPlace display, List<BuildPlace> buildPlaces) {
        this.display = display;
        this.buildPlaces = buildPlaces;
    }

    public void setPattern(List<List<Material>> pattern) {

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

}
