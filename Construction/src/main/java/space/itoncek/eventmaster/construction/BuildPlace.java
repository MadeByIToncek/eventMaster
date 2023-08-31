package space.itoncek.eventmaster.construction;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.json.JSONArray;
import org.json.JSONObject;
import space.itoncek.eventmaster.construction.utils.Orientation;
import space.itoncek.eventmaster.construction.utils.TeamColor;

import java.util.ArrayList;
import java.util.List;

public class BuildPlace {

    /**
     * Location of orientation defining block (top of BuildPlace)
     */
    public final Location markerLocation;
    /**
     * Orientation of BuildPlace (NORTH if markerLocation is at the north [neg z] side of BuildPlace)
     */
    public final Orientation orientation;
    /**
     * Color of the team, owning this BuildPlace
     */
    public final TeamColor color;

    /**
     * @param markerLocation Location of orientation defining block (top of BuildPlace)
     * @param orientation    Orientation of BuildPlace (NORTH if markerLocation is at the north [neg z] side of BuildPlace)
     * @param color          Color of the team, owning this BuildPlace
     */
    public BuildPlace(Location markerLocation, Orientation orientation, TeamColor color) {
        this.markerLocation = markerLocation;
        this.orientation = orientation;
        this.color = color;
    }

    public static List<BuildPlace> deserialize(JSONArray place) {
        List<BuildPlace> out = new ArrayList<>();
        for (Object o : place) {
            JSONObject obj = (JSONObject) o;
            Orientation ori = obj.getEnum(Orientation.class, "orientation");
            TeamColor tc = obj.getEnum(TeamColor.class, "color");

            JSONObject locObj = obj.getJSONObject("loc");
            Location loc = new Location(Bukkit.getWorld(locObj.getString("world")),
                    locObj.getInt("x"),
                    locObj.getInt("y"),
                    locObj.getInt("z"));

            out.add(new BuildPlace(loc, ori, tc));
        }
        return out;
    }

    public static JSONArray serialize(List<BuildPlace> places) {
        JSONArray out = new JSONArray();

        for (BuildPlace place : places) {
            JSONObject obj = new JSONObject();
            JSONObject loc = new JSONObject();

            loc.put("world", place.markerLocation.getWorld().getName());
            loc.put("x", place.markerLocation.getBlockX());
            loc.put("y", place.markerLocation.getBlockY());
            loc.put("z", place.markerLocation.getBlockZ());

            obj.put("orientation", place.orientation);
            obj.put("loc", loc);
            obj.put("color", place.color);

            out.put(obj);
        }

        return out;
    }

    public boolean matchPattern(List<List<Material>> expected) {

        return false;
    }

    public Location getLocations() {
        return switch (orientation) {
            case NORTH -> markerLocation.clone().add(0, 0, 1);
            case EAST -> markerLocation.clone().add(-1, 0, 0);
            case SOUTH -> markerLocation.clone().add(0, 0, -1);
            case WEST -> markerLocation.clone().add(1, 0, 0);
        };
    }
}

