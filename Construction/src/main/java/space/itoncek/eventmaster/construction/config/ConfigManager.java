package space.itoncek.eventmaster.construction.config;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.json.JSONArray;
import org.json.JSONObject;
import space.itoncek.eventmaster.construction.BuildPlace;
import space.itoncek.eventmaster.construction.Pattern;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

public class ConfigManager {
    private static final File placesConfig = new File("./plugins/construction/places.json");
    private static final File patternFolder = new File("./plugins/construction/patterns/");

    public static List<BuildPlace> loadPlaces() {
        long start = System.currentTimeMillis();

        folderStuff();

        StringBuilder sb = new StringBuilder();

        try (Scanner sc = new Scanner(placesConfig)) {
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

        try (FileWriter fw = new FileWriter(placesConfig)) {
            fw.write(array.toString(4));
        } catch (IOException e) {
            Bukkit.getLogger().throwing("ConfigManager", "savePlaces()", e);
        } finally {
            Bukkit.getLogger().info("Places config saved in " + (System.currentTimeMillis() - start) + "ms");
        }
        return (System.currentTimeMillis() - start);
    }

    public static List<Pattern> loadPatterns() {
        long start = System.currentTimeMillis();
        List<Pattern> output = new ArrayList<>();
        folderStuff();
        JSONObject object;
        try (Scanner sc = new Scanner(new URL("https://cdn.itoncek.space/patterns/index.json").openStream())) {
            StringJoiner sj = new StringJoiner("\n");
            while (sc.hasNextLine()) sj.add(sc.nextLine());
            object = new JSONObject(sj.toString());

            Files.copy(new URL("https://cdn.itoncek.space/patterns/index.json").openStream(), Paths.get("./plugins/construction/patternIndex.json"), StandardCopyOption.REPLACE_EXISTING);
            if (Objects.requireNonNull(patternFolder.listFiles()).length != object.getInt("maxFile")) {
                for (int i = 1; i <= object.getInt("maxFile"); i++) {
                    InputStream in = new URL("https://cdn.itoncek.space/patterns/" + i + ".csv").openStream();
                    Files.copy(in, Paths.get("./plugins/construction/patterns/" + i + ".csv"), StandardCopyOption.REPLACE_EXISTING);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Bukkit.getLogger().info("Patterns downloaded in " + (System.currentTimeMillis() - start) + "ms, parsing");

        File[] patterns = patternFolder.listFiles();
        assert patterns != null;
        for (File pattern : patterns) {
            try {
                Scanner sc = new Scanner(pattern);
                List<List<Material>> pat = new ArrayList<>();
                while (sc.hasNextLine()) {
                    List<Material> line = new ArrayList<>();

                    String[] lin = sc.nextLine().split("\\r?,");
                    for (String s : lin) {
                        line.add(Material.valueOf(s));
                    }

                    pat.add(line);
                }
                List<Material> required = new ArrayList<>();
                for (Object o : object.getJSONArray("materials").getJSONObject(Integer.parseInt(pattern.toPath().getFileName().toString().substring(0, 1))).getJSONArray("materials")) {
                    required.add(Material.valueOf((String) o));
                }
                Pattern pattern1 = new Pattern(Integer.parseInt(pattern.toPath().getFileName().toString().substring(0, 1)), pat, required);
                output.add(pattern1);
            } catch (FileNotFoundException e) {
                Bukkit.getLogger().throwing("ConfigManager", "loadPatterns()", e);
            }
        }
        Bukkit.getLogger().info("Patterns loaded in " + (System.currentTimeMillis() - start) + "ms");
        return output;
    }

    private static void folderStuff() {
        if (!placesConfig.getParentFile().exists()) placesConfig.getParentFile().mkdirs();
        if (!placesConfig.exists()) {
            try {
                placesConfig.createNewFile();
                savePlaces(List.of());
            } catch (IOException e) {
                Bukkit.getLogger().throwing("ConfigManager", "folderStuff()", e);
            }
        }
        if (!patternFolder.exists()) patternFolder.mkdirs();
    }
}
