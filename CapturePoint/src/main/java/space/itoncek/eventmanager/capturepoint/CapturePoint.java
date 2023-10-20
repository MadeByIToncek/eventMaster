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
import space.itoncek.eventmanager.capturepoint.utils.BlockState;
import space.itoncek.eventmanager.capturepoint.utils.TeamColor;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Scanner;
import java.util.StringJoiner;

public final class CapturePoint extends JavaPlugin {
    public static final HashMap<Integer, CapturePointManager> managers = new HashMap<>();
    public static final HashMap<TeamColor, Team> teamMap = new HashMap<>();
    public static CapturePointInstance[] instances;
    public static BlockState[][][] blockStates;
    public static CapturePoint pl;
    @Override
    public void onEnable() {
        // Plugin startup logic
        pl = this;
        getCommand("capt").setExecutor(new CommandManager());
        getCommand("capt").setTabCompleter(new CommandHelper());
        instances = loadInstances();
        blockStates = loadPattern();
    }

    private CapturePointInstance[] loadInstances() {
        try (Scanner sc = new Scanner(new URL("https://raw.githubusercontent.com/MadeByIToncek/eventMaster/master/instances.json").openStream())) {
            StringJoiner js = new StringJoiner("\n");
            while (sc.hasNextLine()) js.add(sc.nextLine());

            JSONArray array = new JSONArray(js.toString());
            CapturePointInstance[] output = new CapturePointInstance[array.length()];
            for (Object o : array) {
                JSONObject object = (JSONObject) o;
                output[object.getInt("ident")] = new CapturePointInstance(parseLocation(object.getJSONObject("center"), 0, 0, 0),
                        parseLocation(object.getJSONObject("center"), -2, 1, -2),
                        parseLocation(object.getJSONObject("center"), 2, 1, 2));
            }
            return output;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Location parseLocation(JSONObject location, int xoff, int yoff, int zoff) {
        return new Location(Bukkit.getWorld(location.getString("world")), location.getFloat("x") + xoff, location.getFloat("y") + yoff, location.getFloat("z") + zoff);
    }

    private BlockState[][][] loadPattern() {
        BlockState[][][] out = new BlockState[9][5][5];
        try (Scanner sc = new Scanner(new URL("https://raw.githubusercontent.com/MadeByIToncek/eventMaster/master/loading.json").openStream())) {
            StringJoiner js = new StringJoiner("\n");
            while (sc.hasNextLine()) js.add(sc.nextLine());

            JSONArray array = new JSONArray(js.toString());
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
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return out;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
