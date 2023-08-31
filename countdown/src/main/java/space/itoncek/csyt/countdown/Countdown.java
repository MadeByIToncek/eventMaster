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
import com.sk89q.worldedit.world.World;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static space.itoncek.csyt.countdown.LogicAdapter.getDigits;

public final class Countdown extends JavaPlugin {
    public static FileConfiguration config;
    public static Countdown pl;

    public static void setRemain(int rem, World world) {
        String digs = getDigits(rem);
        List<Action> actions = new ArrayList<>();
        for (Integer i : List.of(0, 1, 3, 4)) {
            int x = config.getInt("loc." + i + ".x");
            int y = config.getInt("loc." + i + ".y");
            int z = config.getInt("loc." + i + ".z");
            Bukkit.broadcast(Component.text(x + " ; " + y + " ; " + z + " ; " + digs));
            File file = new File(config.getString("schematic.prefix") + digs.charAt(i) + config.getString("schematic.suffix"));
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
                .world(world)
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

    @Override
    public void onEnable() {
        // Plugin startup logic
        pl = this;
        Objects.requireNonNull(getCommand("timer")).setExecutor(new TimeCommand());
        Objects.requireNonNull(getCommand("autotimer")).setExecutor(new AutoTimerCommand());
        saveDefaultConfig();
        config = getConfig();
        setRemain(0, BukkitAdapter.adapt(Objects.requireNonNull(Bukkit.getWorld("world"))));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
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