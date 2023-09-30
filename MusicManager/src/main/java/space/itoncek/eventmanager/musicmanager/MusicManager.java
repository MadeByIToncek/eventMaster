package space.itoncek.eventmanager.musicmanager;

import net.roxeez.advancement.Advancement;
import net.roxeez.advancement.AdvancementManager;
import net.roxeez.advancement.trigger.TriggerType;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONArray;
import org.json.JSONObject;
import space.itoncek.eventmanager.musicmanager.commands.ReloadCommand;
import space.itoncek.eventmanager.musicmanager.sponsors.Sponsors;
import space.itoncek.eventmanager.musicmanager.sponsors.TamHost;
import space.itoncek.eventmanager.musicmanager.sponsors.TenM;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.StringJoiner;

public final class MusicManager extends JavaPlugin {

    public static AdvancementManager manager;
    public static MusicManager pl;

    public static void reload() {
        File cfg = new File("./plugins/MusicManager/music.json");
        if (!cfg.getParentFile().exists()) cfg.getParentFile().mkdirs();
        if (!cfg.exists()) {
            try (FileWriter fw = new FileWriter(cfg)) {
                JSONArray arr = new JSONArray();
                arr.put(serialize("gilded", "Gilded", "T_en_M", "TODO", Material.MUSIC_DISC_PIGSTEP));
                arr.put(serialize("noescape", "No Escape", "T_en_M", "TODO", Material.MUSIC_DISC_13));
                arr.put(serialize("shulker", "Shulker", "T_en_M", "TODO", Material.MUSIC_DISC_MALL));
                arr.put(serialize("wither", "The Wither", "T_en_M", "TODO", Material.MUSIC_DISC_5));
                fw.write(arr.toString(4));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        // Plugin startup logic
        manager.register(new Sponsors());
        manager.register(new TamHost());
        manager.register(new TenM());

        StringJoiner js = new StringJoiner("\n");
        try (Scanner sc = new Scanner(cfg)) {
            while (sc.hasNextLine()) js.add(sc.nextLine());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        for (Object o : new JSONArray(js.toString())) {
            manager.register(deserialize((JSONObject) o));
        }

        manager.createAll(true);
    }

    public static Advancement deserialize(JSONObject object) {
        Advancement advancement = new Advancement(pl, object.getString("id"));

        advancement.setParent(new NamespacedKey(pl, TenM.ID));

        advancement.setDisplay(x -> {
            x.setTitle(object.getString("name") + " by " + object.getString("author"));
            x.setDescription(object.getString("description"));
            x.setIcon(object.getEnum(Material.class, "icon"));
        });

        advancement.addCriteria("void", TriggerType.IMPOSSIBLE, impossible -> {
        });

        return advancement;
    }

    public static JSONObject serialize(String id, String name, String author, String description, Material icon) {
        JSONObject object = new JSONObject();

        object.put("id", id);
        object.put("name", name);
        object.put("author", author);
        object.put("description", description);
        object.put("icon", icon);

        return object;
    }

    @Override
    public void onEnable() {
        pl = this;
        manager = new AdvancementManager(this);
        getCommand("reloadAdvancements").setExecutor(new ReloadCommand());
        reload();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
