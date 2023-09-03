package space.itoncek.eventmanager.patternsaver;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public final class PatternSaver extends JavaPlugin {

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

        int i = 1;
        new File("./patterns/").mkdirs();
        for (List<List<Material>> pattern : superList) {
            try (FileWriter fw = new FileWriter("./patterns/" + i + ".csv")) {
                //fw.write("sep=;\n");
                for (List<Material> materials : pattern) {
                    StringJoiner joiner = new StringJoiner(",");
                    for (Material material : materials) {
                        joiner.add(material.name());
                    }
                    fw.write(joiner.toString());
                    fw.write("\n");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            i++;
        }
        i = 1;
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
                JSONArray arr = new JSONArray();
                for (Material material : materialsRequired) {
                    arr.put(material.name());
                }
                JSONObject object = new JSONObject();
                object.put("id", i);
                object.put("materials", arr);
                arrout.put(object);
                i++;
            }
            JSONObject output = new JSONObject();
            output.put("maxFile", i - 1);
            output.put("materials", arrout);
            fw.write(output.toString(4));
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
