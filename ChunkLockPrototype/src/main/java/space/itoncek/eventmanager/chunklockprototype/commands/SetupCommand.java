package space.itoncek.eventmanager.chunklockprototype.commands;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static space.itoncek.eventmanager.chunklockprototype.ChunkLockPrototype.pl;

public class SetupCommand implements CommandExecutor {

    private List<Block> replace = new ArrayList<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender.isOp() && args.length == 1) {
            for (int x = -Integer.parseInt(args[0]); x <= Integer.parseInt(args[0]); x++) {
                for (int z = -Integer.parseInt(args[0]); z < Integer.parseInt(args[0]); z++) {
                    World w = Objects.requireNonNull(Bukkit.getWorld("world"));
                    Chunk c = w.getChunkAt(x, z);

                    for (int by = w.getMinHeight(); by <= w.getMaxHeight(); by++) {
                        for (int bx = 0; bx < 16; bx++) {
                            for (int bz = 0; bz < 16; bz++) {
                                Block block = c.getBlock(bx, by, bz);
                                if (!block.isEmpty() || !block.isLiquid()) continue;
                                if (bx == 0 || bx == 15 || bz == 0 || bz == 15) {
                                    replace.add(block);
                                }
                            }
                        }
                    }
                }
            }

            new BukkitRunnable() {
                @Override
                public void run() {
                    for (int i = 0; i < 64; i++) {
                        Block block = replace.get(0);
                        block.setType(Material.LIGHT_BLUE_STAINED_GLASS);
                        replace.remove(0);
                    }
                }
            }.runTaskTimer(pl, 20L, 1L);
        }
        return true;
    }
}
