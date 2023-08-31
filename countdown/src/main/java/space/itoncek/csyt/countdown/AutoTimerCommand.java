package space.itoncek.csyt.countdown;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import static space.itoncek.csyt.countdown.Countdown.pl;

public class AutoTimerCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender.isOp() && args.length == 1) {
            try {
                final int[] i = {Integer.parseInt(args[0])};
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (i[0] == 0) this.cancel();
                        Countdown.setRemain(i[0],
                                BukkitAdapter.adapt((Player) sender).getWorld());
                        i[0]--;
                    }
                }.runTaskTimer(pl, 20L, 20L);
            } catch (NumberFormatException ignored) {
                Bukkit.broadcast(Component.text("Nah"));
            }
        }
        return true;
    }
}
