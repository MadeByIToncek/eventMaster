package space.itoncek.eventmanager.chunklockprototype.commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import static space.itoncek.eventmanager.chunklockprototype.ChunkLockPrototype.pl;

public class ResetCommand implements CommandExecutor {
    public static void resetChunk(File chunkFile) {
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getLogger().info("[CL] Unlocking chunk!");
                List<Task> tasks = new ArrayList<>();
                StringJoiner js = new StringJoiner("\n");
                try (Scanner sc = new Scanner(chunkFile)) {
                    while (sc.hasNextLine()) js.add(sc.nextLine());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                JSONArray array = new JSONArray(js.toString());
                for (Object o : array) {
                    JSONObject obj = (JSONObject) o;
                    Location loc = new Location(Bukkit.getWorld(obj.getJSONObject("location").getString("world")), obj.getJSONObject("location").getDouble("x"), obj.getJSONObject("location").getDouble("y"), obj.getJSONObject("location").getDouble("z"));
                    tasks.add(new Task(loc.getBlock(), obj.getEnum(Material.class, "material")));
//                    loc.getBlock().setType(obj.getEnum(Material.class,"material"));
                }
                Bukkit.getLogger().info("[CL] Chunk unlocked! Removing " + tasks.size() + " blocks");
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < 1024; i++) {
                            if (tasks.isEmpty()) {
                                this.cancel();
                                chunkFile.delete();
                                break;
                            }
                            Task task = tasks.get(tasks.size() / 2);
                            task.block().setType(task.material());
                            tasks.remove(tasks.size() / 2);
                        }
                    }
                }.runTaskTimer(pl, 0L, 1L);
            }
        }.runTaskAsynchronously(pl);
    }

    public static void resetChunk(int x, int z) {
        if (new File("./chunklock-cache/%d/%d.json".formatted(x, z)).exists()) {
            resetChunk(new File("./chunklock-cache/%d/%d.json".formatted(x, z)));
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender.isOp()) {
            for (File file : Objects.requireNonNull(new File("./chunklock-cache").listFiles())) {
                for (File chunkFile : Objects.requireNonNull(file.listFiles())) {
                    resetChunk(chunkFile);
                }
            }
        }
        return true;
    }

    private record Task(Block block, Material material) {
    }
}
