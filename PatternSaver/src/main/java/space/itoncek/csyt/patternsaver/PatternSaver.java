/*
 * Made by IToncek
 *
 * Copyright (c) 2023.
 */

package space.itoncek.csyt.patternsaver;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.WorldCreator;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import space.itoncek.csyt.DRMLib;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class PatternSaver extends JavaPlugin {

    @NotNull
    private static JSONObject getJsonObject(Material[][] lists, List<Material> materialsRequired, int i) {
        JSONArray arr = new JSONArray();
        for (Material material : materialsRequired) {
            arr.put(material.name());
        }
        JSONArray pattern = new JSONArray();
        for (Material[] materials : lists) {
            JSONArray row = new JSONArray();
            for (Material material : materials) {
                row.put(material.name());
            }
            pattern.put(row);
        }

        JSONObject object = new JSONObject();
        object.put("id", i);
        object.put("materials", arr);
        object.put("pattern", pattern);
        return object;
    }

    @Override
    public void onEnable() {
        new DRMLib() {
            @Override
            public void callback() {
                Bukkit.shutdown();
            }
        };
        new WorldCreator("loadPatterns").createWorld();

        long start = System.currentTimeMillis();
        new File("./patterns/").delete();
        // Plugin startup logic
        List<Integer> ys = new ArrayList<>();
        for (int i = 70; i < 320; i += 4) {
            if (getBlockAt("world", 0, i, 0).equals(Material.ORANGE_GLAZED_TERRACOTTA)) {
                ys.add(i);
            }
        }
        Material[][][] output = new Material[ys.size()][5][5];
        int i = 0;
        for (Integer y : ys) {
            for (int x = 1; x < 6; x++) {
                for (int z = -2; z < 3; z++) {
                    if (!getBlockAt("world", x, y + 1, z).equals(Material.ORANGE_GLAZED_TERRACOTTA))
                        output[i][x - 1][z + 2] = getBlockAt("world", x, y + 1, z);
                    else {
                        output[i][x - 1][z + 2] = Material.AIR;
                    }
                }
            }
            i++;
        }
        new File("./patterns/").mkdirs();
        try (FileWriter fw = new FileWriter("./patterns/index.json")) {
            JSONArray arrout = new JSONArray();
            for (int pattern = 0; pattern < ys.size() - 1; pattern++) {
                List<Material> materialsRequired = new ArrayList<>();
                System.out.println("Processing pattern #" + pattern);
                for (Material[] materials : output[pattern]) {
                    for (Material material : materials) {
                        if (!materialsRequired.contains(material) && !material.equals(Material.AIR)) {
                            materialsRequired.add(material);
                        }
                    }
                }
                JSONObject object = getJsonObject(output[pattern], materialsRequired, pattern);
                if (!materialsRequired.isEmpty()) arrout.put(object);
            }
            fw.write(arrout.toString(4));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        JSONArray pattern = new JSONArray();
        for (int y = 0; y <= 8; y++) {
            JSONArray progress = new JSONArray();
            for (int x = -2; x <= 2; x++) {
                JSONArray row = new JSONArray();
                for (int z = -2; z <= 2; z++) {
                    Material block = getBlockAt("loadPatterns", x, y, z);
                    row.put(parse(block));
                }
                progress.put(row);
            }
            pattern.put(progress);
        }
        try (FileWriter fw = new FileWriter("./patterns/loading.json")) {
            fw.write(pattern.toString(4));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        Bukkit.getServer().shutdown();
        System.out.println("Saved in " + (System.currentTimeMillis() - start) + "ms");
    }

    private short parse(Material block) {
        return switch (block) {
            case LIME_STAINED_GLASS -> 2;
            case MAGENTA_STAINED_GLASS -> 1;
            default -> 0;
        };
    }

    public Material getBlockAt(String world, int x, int y, int z) {
        return new Location(Bukkit.getWorld(world), x, y, z).getBlock().getType();
    }
    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
