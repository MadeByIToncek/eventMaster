package space.itoncek.csyt.countdown;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TimeCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender.isOp() && args.length == 1) {
            try {
                Countdown.setRemain(Integer.parseInt(args[0]), BukkitAdapter.adapt((Player) sender).getWorld());
                System.out.println(((Player) sender).getWorld().getName());
            } catch (NumberFormatException ignored) {
            }
        }
        return true;
    }
}
