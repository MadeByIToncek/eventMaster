package space.itoncek.eventmanager.chunklockprototype.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import static space.itoncek.eventmanager.chunklockprototype.commands.ResetCommand.resetChunk;

public class BlockClickListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction().isRightClick())
            if (event.getClickedBlock() != null)
                if (event.getClickedBlock().getType().equals(Material.LIGHT_BLUE_STAINED_GLASS)) {
                    Block block = event.getClickedBlock();

                    resetChunk(block.getChunk().getX(), block.getChunk().getZ());
                }
    }
}
