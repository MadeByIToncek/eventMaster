package space.itoncek.eventmanager.patternsaver;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class PatternSaver extends JavaPlugin {

    @NotNull
    private static JSONObject getJsonObject(List<List<Material>> lists, List<Material> materialsRequired, int i) {
        JSONArray arr = new JSONArray();
        for (Material material : materialsRequired) {
            arr.put(material.name());
        }
        JSONArray pattern = new JSONArray();
        for (List<Material> materials : lists) {
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
        new File("./patterns/").delete();
        // Plugin startup logic
        List<List<List<Material>>> superList = new ArrayList<>();
        for (int y = 110; y <= 186; y += 4) {
            List<List<Material>> pseudolist = new ArrayList<>();
            for (int x = -119; x <= -115; x++) {
                List<Material> supraList = new ArrayList<>();
                for (int z = -105; z <= -101; z++) {
                    supraList.add(new Location(Bukkit.getWorld("world"), x, y, z).getBlock().getType());
                }
                pseudolist.add(supraList);
            }
            superList.add(pseudolist);
        }

        int i = 0;
        new File("./patterns/").mkdirs();
        try (FileWriter fw = new FileWriter("./patterns/index.json")) {
            JSONArray arrout = new JSONArray();
            for (List<List<Material>> lists : superList) {
                List<Material> materialsRequired = new ArrayList<>();
                for (List<Material> materials : lists) {
                    for (Material material : materials) {
                        if (!materialsRequired.contains(material) && !material.equals(Material.AIR)) {
                            materialsRequired.add(material);
                        }
                    }
                }
                JSONObject object = getJsonObject(lists, materialsRequired, i);
                arrout.put(object);
                i++;
            }
            fw.write(arrout.toString(4));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Bukkit.getServer().shutdown();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
