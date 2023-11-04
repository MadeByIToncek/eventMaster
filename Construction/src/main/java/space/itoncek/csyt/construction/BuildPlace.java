/*
 * Made by IToncek
 *
 * Copyright (c) 2023.
 */

package space.itoncek.csyt.construction;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import space.itoncek.csyt.construction.utils.Orientation;
import space.itoncek.csyt.construction.utils.TeamColor;

import java.util.ArrayList;
import java.util.List;

import static space.itoncek.csyt.construction.Construction.patterns;

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
    public Material[][] pattern;
    public int patternID;
    public long patternStart;
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
        clr();
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
                Material expect = pattern[x][z];
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

    private Material[][] rotateCounterClockWise(Material[][] matrix) {
        int size = matrix.length;
        Material[][] ret = new Material[size][size];

        for (int i = 0; i < size; ++i)
            for (int j = 0; j < size; ++j)
                ret[i][j] = matrix[j][size - i - 1]; //***

        return ret;
    }

    public static Material[][] mirror(Material[][] in) {
        Material[][] out = new Material[in.length][in.length];
        for (int i = 0; i < in.length; i++) {
            System.arraycopy(in[i], 0, out[in.length - i - 1], 0, in.length);
        }
        return out;
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

    public void reward(String player) {
        String cmd = patterns.get(patternID).award(player);
        Bukkit.getLogger().info(cmd);
        sendCmd(cmd);
    }

    public void end() {
        clr();
        active = false;
//        for (Player nearbyPlayer : getRelLoc(2, 2).getNearbyPlayers(20)) {
//            nearbyPlayer.playSound(getRelLoc(2, 2).clone().add(0, 1, 0), "shine", 20f, 1f);
//        }
    }

    public void setPattern(int i) {
        this.pattern = rotate(patterns.get(i).pattern());
        this.patternID = i;
        clr();
        if (this.display) {
            int size = pattern.length;
            for (int x = 0; x < size; x++) {
                for (int z = 0; z < size; z++) {
                    getRelLoc(x, z).getBlock().setType(pattern[x][z]);
                }
            }
        }
        this.active = true;
        patternStart = System.currentTimeMillis();
    }

    private void sendCmd(String cmd) {
        Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), cmd);
    }

    private Material[][] rotate(Material[][] pattern) {
//        return pattern;
        return switch (orientation) {
            case WEST, EAST -> pattern;
            case NORTH -> rotateCounterClockWise(pattern);
            case SOUTH -> mirror(rotateCounterClockWise(pattern));
        };
    }

    public void deactivate() {
        active = false;
        for (Location location : getLocations()) {
            location.getBlock().setType(color.material);
        }
    }

    record Points(Player p, int points) implements Comparable<Points> {
        @Override
        public int compareTo(@NotNull BuildPlace.Points o) {
            return points - o.points;
        }

        @Override
        public boolean equals(Object obj) {
            return obj.equals(p);
        }
    }
}

