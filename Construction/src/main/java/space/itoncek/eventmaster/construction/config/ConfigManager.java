package space.itoncek.eventmaster.construction.config;

import org.bukkit.Bukkit;
import org.json.JSONArray;
import space.itoncek.eventmaster.construction.BuildPlace;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class ConfigManager {
    private static final File file = new File("./plugins/construction/places.json");

    public static List<BuildPlace> loadPlaces() {
        long start = System.currentTimeMillis();

        folderStuff();

        StringBuilder sb = new StringBuilder();

        try (Scanner sc = new Scanner(file)) {
            while (sc.hasNextLine()) sb.append(sc.nextLine()).append("\n");
        } catch (FileNotFoundException e) {
            Bukkit.getLogger().throwing("ConfigManager", "loadPlaces()", e);
        }

        List<BuildPlace> out = BuildPlace.deserialize(new JSONArray(sb.toString()));
        Bukkit.getLogger().info("Places config loaded in " + (System.currentTimeMillis() - start) + "ms");
        return out;
    }

    public static long savePlaces(List<BuildPlace> places) {
        long start = System.currentTimeMillis();
        JSONArray array = BuildPlace.serialize(places);

        folderStuff();

        try (FileWriter fw = new FileWriter(file)) {
            fw.write(array.toString(4));
        } catch (IOException e) {
            Bukkit.getLogger().throwing("ConfigManager", "savePlaces()", e);
        } finally {
            Bukkit.getLogger().info("Places config saved in " + (System.currentTimeMillis() - start) + "ms");
        }
        return (System.currentTimeMillis() - start);
    }

    private static void folderStuff() {
        if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
        if (!file.exists()) {
            try {
                file.createNewFile();
                savePlaces(List.of());
            } catch (IOException e) {
                Bukkit.getLogger().throwing("ConfigManager", "folderStuff()", e);
            }
        }
    }
}
