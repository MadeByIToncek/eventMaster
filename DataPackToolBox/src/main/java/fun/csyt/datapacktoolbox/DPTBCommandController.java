/*
 * Made by IToncek
 *
 * Copyright (c) 2023.
 */

package fun.csyt.datapacktoolbox;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.ShulkerBox;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static fun.csyt.datapacktoolbox.DataPackToolBox.pl;

public class DPTBCommandController implements TabCompleter, CommandExecutor {
    public static List<ItemStack> generateShulkerboxes() {
        return List.of(generateShulkerbox(Component.text("Common Loot Box", Style.style().color(TextColor.color(0xFF, 0xFF, 0xFF)).decoration(TextDecoration.BOLD, true).build()),
                        "common",
                        Material.WHITE_SHULKER_BOX),
                generateShulkerbox(Component.text("Uncommon Loot Box", Style.style().color(TextColor.color(0x00, 0xFF, 0x00)).decoration(TextDecoration.BOLD, true).build()),
                        "uncommon",
                        Material.LIME_SHULKER_BOX),
                generateShulkerbox(Component.text("Rare Loot Box", Style.style().color(TextColor.color(0x00, 0xFF, 0xFF)).decoration(TextDecoration.BOLD, true).build()),
                        "rare",
                        Material.LIGHT_BLUE_SHULKER_BOX),
                generateShulkerbox(Component.text("Gadget Loot Box", Style.style().color(TextColor.color(0x80, 0x00, 0x80)).decoration(TextDecoration.BOLD, true).build()),
                        "gadget",
                        Material.SHULKER_BOX),
                generateShulkerbox(Component.text("Epic Loot Box", Style.style().color(TextColor.color(0xFF, 0x00, 0xFF)).decoration(TextDecoration.BOLD, true).build()),
                        "epic",
                        Material.MAGENTA_SHULKER_BOX),
                generateShulkerbox(Component.text("Legendary Loot Box", Style.style().color(TextColor.color(0xFF, 0xA5, 0x00)).decoration(TextDecoration.BOLD, true).build()),
                        "legendary",
                        Material.ORANGE_SHULKER_BOX));
    }

    public static ItemStack generateShulkerbox(Component name, String loot, Material material) {
        ItemStack itemStack = new ItemStack(material);
        BlockStateMeta itemMeta = (BlockStateMeta) itemStack.getItemMeta();
        itemMeta.displayName(name);

        ShulkerBox shulkerBox = (ShulkerBox) itemMeta.getBlockState();
        shulkerBox.customName(name);
        shulkerBox.setLootTable(Bukkit.getLootTable(NamespacedKey.fromString("csyt:" + loot)));

        itemMeta.setBlockState(shulkerBox);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender.isOp() && args.length > 0) {
            switch (args[0]) {
                case "getShulkerboxes" -> {
                    if (!(sender instanceof Player)) sender.sendMessage(Color.RED + "You are not a player!");
                    Player p = (Player) sender;

                    AtomicInteger i = new AtomicInteger(0);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (i.get() >= generateShulkerboxes().size() - 1) this.cancel();
                            ItemStack is = generateShulkerboxes().get(i.get());
                            p.getInventory().setItem(i.getAndIncrement(), is);
                        }
                    }.runTaskTimer(pl, 0L, 0L);
                }
            }
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender.isOp()) {
            return switch (args.length) {
                case 1 -> List.of("getShulkerboxes");
                default -> List.of("<none>");
            };
        }
        return List.of();
    }
}
