package space.itoncek.eventmanager.chunklockprototype.commands;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;

import static space.itoncek.eventmanager.chunklockprototype.ChunkLockPrototype.pl;

public class SetupCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender.isOp() && args.length == 1) {
            for (int x = -Integer.parseInt(args[0]); x <= Integer.parseInt(args[0]); x++) {
                for (int z = -Integer.parseInt(args[0]); z < Integer.parseInt(args[0]); z++) {
                    int finalX = x;
                    int finalZ = z;
                    if (!(x == 0 && z == 0)) {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                World w = Objects.requireNonNull(Bukkit.getWorld("world"));
                                Chunk c = w.getChunkAt(finalX, finalZ);
                                Bukkit.getLogger().info("[CL] Adding chunk %d,%d".formatted(finalX, finalZ));
                                JSONArray replacements = new JSONArray();
                                for (int by = w.getMinHeight(); by <= w.getMaxHeight(); by++) {
                                    for (int bx = 0; bx < 16; bx++) {
                                        for (int bz = 0; bz < 16; bz++) {
                                            Block block = c.getBlock(bx, by, bz);
                                            if ((bx == 0 || bx == 15 || bz == 0 || bz == 15)) {
                                                JSONObject replacement = new JSONObject();
                                                replacement.put("material", block.getType());
                                                replacement.put("location", toJsonObject(block.getLocation()));
                                                replacements.put(replacement);
                                                block.setType(block.isSolid() ? Material.LIGHT_BLUE_CONCRETE : Material.LIGHT_BLUE_STAINED_GLASS);
                                            }
                                        }
                                    }
                                }

                                File output = new File("./chunklock-cache/%d/%d.json".formatted(finalX, finalZ));
                                if (!output.getParentFile().exists()) output.getParentFile().mkdirs();
                                if (output.exists()) output.delete();
                                try (FileWriter fw = new FileWriter(output)) {
                                    fw.write(replacements.toString());
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }.runTask(pl);
                    }
                }
            }


        }
        return true;
    }

    private JSONObject toJsonObject(Location location) {
        JSONObject ret = new JSONObject();

        ret.put("world", location.getWorld().getName());
        ret.put("x", location.x());
        ret.put("y", location.y());
        ret.put("z", location.z());

        return ret;
    }

    record ReplaceAction(Block b, int type) {
    }
}