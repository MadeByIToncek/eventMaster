/*
 * Made by IToncek
 *
 * Copyright (c) 2023.
 */

package space.itoncek.eventmanager.capturepoint;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONArray;
import org.json.JSONObject;
import space.itoncek.csyt.DRMLib;
import space.itoncek.csyt.UpdateLib;
import space.itoncek.eventmanager.capturepoint.utils.BlockState;
import space.itoncek.eventmanager.capturepoint.utils.TeamColor;

import java.util.HashMap;

public final class CapturePoint extends JavaPlugin {
    public static final HashMap<Integer, CapturePointManager> managers = new HashMap<>();
    public static final HashMap<TeamColor, Team> teamMap = new HashMap<>();
    public static CapturePointInstance[] instances;
    public static BlockState[][][] blockStates;
    public static float multiplier = 1;
    public static CapturePoint pl;

    @Override
    public void onEnable() {
        // Plugin startup logic
        new DRMLib() {
            @Override
            public void callback() {
                Bukkit.shutdown();
            }
        };
        UpdateLib.downloadCommitID(this.getDataFolder());
        pl = this;
        getCommand("capt").setExecutor(new CommandManager());
        getCommand("capt").setTabCompleter(new CommandHelper());
        instances = loadInstances();
        blockStates = loadPattern();
    }

    private Location parseLocation(JSONObject location, int xoff, int yoff, int zoff) {
        return new Location(Bukkit.getWorld(location.getString("world")), location.getFloat("x") + xoff, location.getFloat("y") + yoff, location.getFloat("z") + zoff);
    }

    private BlockState[][][] loadPattern() {
        BlockState[][][] out = new BlockState[9][5][5];
        JSONArray array = new JSONArray(UpdateLib.getFile("loading.json"));
        int y = 0;
        for (Object o : array) {
            JSONArray a = (JSONArray) o;
            int x = 0;
            for (Object object : a) {
                JSONArray b = (JSONArray) object;
                int z = 0;

                for (Object in : b) {
                    int i = (int) in;
                    out[y][x][z] = switch (i) {
                        case 1 -> BlockState.BASE;
                        case 2 -> BlockState.ACCENT;
                        default -> BlockState.KEEP;
                    };
                    z++;
                }
                x++;
            }
            y++;
        }
        return out;
    }

    private CapturePointInstance[] loadInstances() {

        String sb = UpdateLib.getFile("instances.json");
        JSONArray array = new JSONArray(sb);
        CapturePointInstance[] output = new CapturePointInstance[array.length()];
        for (Object o : array) {
            JSONObject object = (JSONObject) o;
            output[object.getInt("ident")] = new CapturePointInstance(parseLocation(object.getJSONObject("center"), 0, 0, 0),
                    parseLocation(object.getJSONObject("center"), -2, 1, -2),
                    parseLocation(object.getJSONObject("center"), 2, 1, 2));
        }
        Bukkit.getLogger().warning(array.toString());
        return output;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        UpdateLib.checkForUpdates(this.getDataFolder(), "CapturePoint", this.getFile());
    }
}
