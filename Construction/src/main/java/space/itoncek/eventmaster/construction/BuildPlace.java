package space.itoncek.eventmaster.construction;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.json.JSONArray;
import org.json.JSONObject;
import space.itoncek.eventmaster.construction.utils.Orientation;
import space.itoncek.eventmaster.construction.utils.TeamColor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static space.itoncek.eventmaster.construction.Construction.patterns;

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
    public boolean active;
    public List<List<Material>> pattern;
    public HashMap<Location, Player> locationPlayerHashMap = new HashMap<>(25);
    public int patternID;
    public final boolean display;
    /**
     * @param markerLocation Location of orientation defining block (top of BuildPlace)
     * @param orientation    Orientation of BuildPlace (NORTH if markerLocation is at the north [neg z] side of BuildPlace)
     * @param color          Color of the team, owning this BuildPlace
     * @param display        Mark BuildPlace as DisplayPlace
     */
    public BuildPlace(Location markerLocation, Orientation orientation, TeamColor color, boolean display) {
        this.markerLocation = markerLocation;
        this.orientation = orientation;
        this.color = color;
        this.display = display;
        this.active = true;
    }

    public static List<BuildPlace> deserialize(JSONArray place) {
        List<BuildPlace> out = new ArrayList<>();
        for (Object o : place) {
            JSONObject obj = (JSONObject) o;
            Orientation ori = obj.getEnum(Orientation.class, "orientation");
            TeamColor tc = obj.getEnum(TeamColor.class, "color");
            boolean disp = obj.getBoolean("display");

            JSONObject locObj = obj.getJSONObject("loc");
            Location loc = new Location(Bukkit.getWorld(locObj.getString("world")),
                    locObj.getInt("x"),
                    locObj.getInt("y"),
                    locObj.getInt("z"));

            out.add(new BuildPlace(loc, ori, tc, disp));
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
            obj.put("display", place.display);

            out.put(obj);
        }

        return out;
    }

    public boolean matchPattern() {
        boolean out = true;
        for (int x = 0; x < 5; x++) {
            for (int z = 0; z < 5; z++) {
                Material expect = pattern.get(x).get(z);
                if (!isMaterial(x, z, expect)) out = false;
            }
        }
        return out;
    }

    public List<Location> getLocations() {
        switch (orientation) {
            case NORTH -> markerLocation.clone().add(0, 0, 1);
            case EAST -> markerLocation.clone().add(-1, 0, 0);
            case SOUTH -> markerLocation.clone().add(0, 0, -1);
            case WEST -> markerLocation.clone().add(1, 0, 0);
        }
        List<Location> res = new ArrayList<>();

        for (int x = 0; x < 5; x++) {
            for (int z = 0; z < 5; z++) {
                switch (orientation) {
                    case NORTH -> res.add(new Location(markerLocation.getWorld(),
                            markerLocation.getBlockX() + (x - 2),
                            markerLocation.getBlockY(),
                            markerLocation.getBlockZ() + z + 1));
                    case EAST -> res.add(new Location(markerLocation.getWorld(),
                            markerLocation.getBlockX() - x - 1,
                            markerLocation.getBlockY(),
                            markerLocation.getBlockZ() - (z - 2)));
                    case SOUTH -> res.add(new Location(markerLocation.getWorld(),
                            markerLocation.getBlockX() + (x - 2),
                            markerLocation.getBlockY(),
                            markerLocation.getBlockZ() - z - 1));
                    case WEST -> res.add(new Location(markerLocation.getWorld(),
                            markerLocation.getBlockX() + x + 1,
                            markerLocation.getBlockY(),
                            markerLocation.getBlockZ() + (z - 2)));
                }
            }
        }

        return res;
    }

    public void addPlayerBlockPoints(Player p, Location loc) {
        locationPlayerHashMap.put(loc, p);
    }

    public Location getRelLoc(int x, int z) {
        if (x < 0 || x > 4 || z < 0 || z > 4) {
            return null;
        } else {
            return switch (orientation) {
                case NORTH -> new Location(markerLocation.getWorld(),
                        markerLocation.getBlockX() + (x - 2),
                        markerLocation.getBlockY(),
                        markerLocation.getBlockZ() + z + 1);
                case EAST -> new Location(markerLocation.getWorld(),
                        markerLocation.getBlockX() - x - 1,
                        markerLocation.getBlockY(),
                        markerLocation.getBlockZ() - (z - 2));
                case SOUTH -> new Location(markerLocation.getWorld(),
                        markerLocation.getBlockX() + (x - 2),
                        markerLocation.getBlockY(),
                        markerLocation.getBlockZ() - z - 1);
                case WEST -> new Location(markerLocation.getWorld(),
                        markerLocation.getBlockX() + x + 1,
                        markerLocation.getBlockY(),
                        markerLocation.getBlockZ() + (z - 2));
            };
        }
    }


    public boolean isMaterial(int x, int z, Material mat) {
        Location loc = getRelLoc(x, z);
        return mat.equals(loc.getBlock().getType());
    }

    public void reward() {
        int totalPTS = 200;
        HashMap<Player, Integer> map = new HashMap<>();
        int partPTS = totalPTS / locationPlayerHashMap.size();

        for (Map.Entry<Location, Player> entry : locationPlayerHashMap.entrySet()) {
            map.put(entry.getValue(), map.getOrDefault(entry.getValue(), 0) + 1);
        }

        for (Map.Entry<Player, Integer> e : map.entrySet()) {
            int finalPoints = partPTS * e.getValue();
            String cmd = "ptsadd " + e.getKey().getName() + " " + finalPoints;
            sendCmd(cmd);
        }

        markerLocation.getWorld().spawnParticle(Particle.TOTEM, getRelLoc(2, 2).clone().add(0, 1, 0), 60, 1, 1, 1);
        for (Player nearbyPlayer : getRelLoc(2, 2).getNearbyPlayers(20)) {
            nearbyPlayer.playSound(getRelLoc(2, 2).clone().add(0, 1, 0), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 20f, 1f);
        }
    }

    public void clr() {
        for (Location location : getLocations()) {
            location.getBlock().setType(Material.AIR);
        }
    }

    public void setTeamBlock() {
        for (Location location : getLocations()) {
            location.getBlock().setType(color.material);
        }
    }

    public void end() {
        setTeamBlock();
        active = false;
        for (Player nearbyPlayer : getRelLoc(2, 2).getNearbyPlayers(20)) {
            nearbyPlayer.playSound(getRelLoc(2, 2).clone().add(0, 1, 0), "sparkle", 20f, 1f);
        }
    }

    public void setPattern(int i) {
        this.pattern = patterns.get(i).pattern();
        this.patternID = i;
        clr();
        sendCmd("minigame_construction_clear " + color.name().toLowerCase());
        if (this.display) {
            int x = 0;
            for (List<Material> materials : pattern) {
                int z = 0;
                for (Material material : materials) {
                    getRelLoc(x, z).getBlock().setType(material);
                    z++;
                }
                x++;
            }
        }
        this.active = true;
        for (Material material : patterns.get(i).materials()) {
            sendCmd("minigame_construction_give " + color.name().toLowerCase() + " " + material.name().toLowerCase());
        }
    }

    private void sendCmd(String cmd) {
        Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), cmd);
    }
}

