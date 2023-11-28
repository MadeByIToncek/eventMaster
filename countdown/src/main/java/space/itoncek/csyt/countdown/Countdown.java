/*
 * Made by IToncek
 *
 * Copyright (c) 2023.
 */

package space.itoncek.csyt.countdown;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.JSONObject;
import space.itoncek.csyt.DRMLib;
import space.itoncek.csyt.UpdateLib;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

import static space.itoncek.csyt.countdown.LogicAdapter.getDigits;

public final class Countdown extends JavaPlugin {
    public static JSONObject config;
    public static Countdown pl;
    public static BukkitRunnable acdR;
    public static void setRemain(int rem) {
        String digs = getDigits(rem);
        List<Action> actions = new ArrayList<>();
        for (Integer i : List.of(0, 1, 2, 3)) {
            int x = config.getJSONObject("loc" + (i + 1)).getInt("x");
            int y = config.getJSONObject("loc" + (i + 1)).getInt("y");
            int z = config.getJSONObject("loc" + (i + 1)).getInt("z");
            File file = new File(config.getJSONObject("schematic").getString("prefix") + digs.charAt(i) + config.getJSONObject("schematic").getString("suffix"));
            actions.add(new Action(file, x, y, z));
        }

        for (Action action : actions) {
            ClipboardFormat format = ClipboardFormats.findByFile(action.file());
            assert format != null;
            try (ClipboardReader reader = format.getReader(Files.newInputStream(action.file().toPath()))) {
                action.setClipboard(reader.read());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        try (EditSession editSession = WorldEdit.getInstance().newEditSessionBuilder()
                .world(BukkitAdapter.adapt(Objects.requireNonNull(Bukkit.getWorld(config.getString("world")))))
                .maxBlocks(-1)
                .build()) {
            for (Action action : actions) {
                Operations.complete(new ClipboardHolder(action.getClipboard())
                        .createPaste(editSession)
                        .to(BlockVector3.at(action.x(), action.y(), action.z()))
                        .ignoreAirBlocks(false)
                        .build());
            }


        } catch (WorldEditException e) {
            throw new RuntimeException(e);
        }
    }

    public static BukkitRunnable generateACDR(int time) {
        return new BukkitRunnable() {
            int i = time;

            @Override
            public void run() {
                if (i == 0) this.cancel();
                Countdown.setRemain(i);
                i--;
            }
        };
    }
    @Override
    public void onEnable() {
        new DRMLib() {
            @Override
            public void callback() {
                Bukkit.shutdown();
            }
        };
        UpdateLib.downloadCommitID(this.getDataFolder(), "./config/.ghcreds");
        // Plugin startup logic
        pl = this;
        Objects.requireNonNull(getCommand("acd")).setExecutor(new ACDCommand());
        Objects.requireNonNull(getCommand("acd")).setTabCompleter(new ACDHelper());
        Objects.requireNonNull(getCommand("autocd")).setExecutor(new ACDCommand());
        Objects.requireNonNull(getCommand("autocd")).setTabCompleter(new ACDHelper());
        Objects.requireNonNull(getCommand("autocountdown")).setExecutor(new ACDCommand());
        Objects.requireNonNull(getCommand("autocountdown")).setTabCompleter(new ACDHelper());
        try {
            config = setupConfig();
        } catch (IOException e) {
            e.printStackTrace();
            this.setEnabled(false);
        }
        //setRemain(0, BukkitAdapter.adapt(Objects.requireNonNull(Bukkit.getWorld("world"))));
    }

    public JSONObject setupConfig() throws IOException {
        File config = new File(this.getDataFolder().toPath() + "/config.json");
        if (!config.getParentFile().exists()) this.getDataFolder().mkdirs();
        if (!config.exists()) {
            try (FileWriter fw = new FileWriter(config)) {
                JSONObject def = new JSONObject();
                def.put("loc1", new JSONObject().put("x", 0).put("y", 0).put("z", 0));
                def.put("loc2", new JSONObject().put("x", 0).put("y", 0).put("z", 0));
                def.put("loc3", new JSONObject().put("x", 0).put("y", 0).put("z", 0));
                def.put("loc4", new JSONObject().put("x", 0).put("y", 0).put("z", 0));
                def.put("world", "world");
                def.put("schematic", new JSONObject().put("prefix", "./plugins/FastAsyncWorldEdit/schematics/numbers/").put("suffix", ".schem"));
                fw.write(def.toString(4));
            }
        }
        ;

        try (Scanner sc = new Scanner(config)) {
            StringJoiner js = new StringJoiner("\n");
            while (sc.hasNextLine()) js.add(sc.nextLine());
            return new JSONObject(js.toString());
        }
    }

    public void unloadConfig(JSONObject data) throws IOException {
        File config = new File(this.getDataFolder().toPath() + "/config.json");
        if (!config.getParentFile().exists()) this.getDataFolder().mkdirs();
        try (FileWriter fw = new FileWriter(config)) {
            fw.write(data.toString(4));
        }
    }

    public JSONObject reloadConfig(JSONObject data) throws IOException {
        unloadConfig(data);
        return setupConfig();
    }
    @Override
    public void onDisable() {
        // Plugin shutdown logic
        try {
            unloadConfig(config);
        } catch (IOException e) {
            e.printStackTrace();
        }
        UpdateLib.checkForUpdates(this.getDataFolder(), "countdown", this.getFile(), "./config/.ghcreds");
    }
}

final class Action {
    private final File file;
    private final int x;
    private final int y;
    private final int z;
    private Clipboard clipboard;

    Action(File file, int x, int y, int z) {
        this.file = file;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public File file() {
        return file;
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    public int z() {
        return z;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Action) obj;
        return Objects.equals(this.file, that.file) &&
                this.x == that.x &&
                this.y == that.y &&
                this.z == that.z;
    }

    @Override
    public int hashCode() {
        return Objects.hash(file, x, y, z);
    }

    @Override
    public String toString() {
        return "Action[" +
                "file=" + file + ", " +
                "x=" + x + ", " +
                "y=" + y + ", " +
                "z=" + z + ']';
    }

    public Clipboard getClipboard() {
        return clipboard;
    }

    public void setClipboard(Clipboard clipboard) {
        this.clipboard = clipboard;
    }
}