/*
 * Made by IToncek
 *
 * Copyright (c) 2023.
 */

package fun.csyt.dancefloor;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Closeable;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import static fun.csyt.dancefloor.DanceFloor.pl;
import static fun.csyt.dancefloor.Mode.RUN;
import static fun.csyt.dancefloor.Mode.WAIT;

public final class DanceFloor extends JavaPlugin {
	public static DanceFloor pl;

	@Override
	public void onEnable() {
		// Plugin startup logic
		pl = this;
	}

	@Override
	public void onDisable() {
		// Plugin shutdown logic
	}
}

class DanceFloorRuntime implements CommandExecutor, Listener, TabCompleter, Closeable {
	private final Location min;
	private final Location max;
	private final List<BukkitTask> tasks = new ArrayList<>();
	private Mode mode = WAIT;

	DanceFloorRuntime(Location min, Location max) {
		this.min = min;
		this.max = max;

		pl.getCommand("minigame_dance_control").setExecutor(this);
		pl.getServer().getPluginManager().registerEvents(this, pl);
	}

	public void updateFloor() {
		try (EditSession editSession = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(min.getWorld()))) {
			List<Material> mat = new ArrayList<>(List.of(Material.LIGHT_GRAY_CONCRETE,
					Material.GRAY_CONCRETE,
					Material.YELLOW_CONCRETE,
					Material.ORANGE_CONCRETE,
					Material.RED_CONCRETE,
					Material.BLACK_CONCRETE,
					Material.WHITE_CONCRETE,
					Material.LIME_CONCRETE,
					Material.LIGHT_BLUE_CONCRETE,
					Material.PINK_CONCRETE,
					Material.MAGENTA_CONCRETE));

			Collections.shuffle(mat, new SecureRandom());
			AtomicInteger i = new AtomicInteger();

			int limit = 3;

			Material[] lut = mat.stream().filter(m -> {
				i.getAndIncrement();
				return i.get() <= limit;
			}).toArray(Material[]::new);

			for (int x = min.getBlockX(); x <= max.getBlockX(); x++) {
				for (int z = min.getBlockZ(); z <= max.getBlockZ(); z++) {
					editSession.setBlock(BlockVector3.at(x, 90, z), BukkitAdapter.adapt(lut[new Random().nextInt(lut.length)].createBlockData()));
				}
			}
		} catch (MaxChangedBlocksException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void close() {
		pl.getCommand("minigame_dance_control").setExecutor(null);
		HandlerList.unregisterAll(this);
	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if (sender.isOp() && args.length > 0) {
			switch (args[0]) {
				case "setup" -> {
				}
				case "start" -> {
					StartRunnable start = new StartRunnable() {
						@Override
						void setModeToRUN() {
							mode = RUN;
						}

						@Override
						void addTask(BukkitTask task) {
							tasks.add(task);
						}

						@Override
						void updatePlayingField() {
							updateFloor();
						}

						@Override
						BukkitRunnable getGameplayTask() {
							return null;
						}
					};
					start.runTaskTimer(pl, 20L, 20L);
				}
				case "terminate" -> {
				}
			}
		}
		return true;
	}

	@Override
	public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if (sender.isOp()) {
			return switch (args.length) {
				case 0 -> List.of("setup", "start", "terminate");
				default -> List.of();
			};
		}
		return List.of();
	}
}

abstract class StartRunnable extends BukkitRunnable {
	int i = 10;

	public static String convertToText(int i) {
		return switch (i) {
			case 10 -> "\u05c2\u05c3";
			case 9 -> "\u05cb";
			case 8 -> "\u05ca";
			case 7 -> "\u05c9";
			case 6 -> "\u05c8";
			case 5 -> "\u05c7";
			case 4 -> "\u05c6";
			case 3 -> "\u05c5";
			case 2 -> "\u05c4";
			case 1 -> "\u05c3";
			default -> "";
		};
	}

	abstract void setModeToRUN();

	abstract void addTask(BukkitTask task);

	abstract void updatePlayingField();

	abstract BukkitRunnable getGameplayTask();

	@Override
	public void run() {
		if (i == 3) Bukkit.getOnlinePlayers()
				.forEach(p -> p.playSound(p.getLocation(), "minecraft:countdown", SoundCategory.MASTER, 1, 1));
		else if (i > 3) Bukkit.getOnlinePlayers()
				.forEach(p -> p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, SoundCategory.MASTER, 1, 1));

		if (i > 0) {
			Bukkit.getOnlinePlayers()
					.forEach(p -> p.showTitle(Title.title(
							Component.text(convertToText(i), convertToColor(i)),
							Component.text("א", TextColor.color(247, 178, 4)),
							Title.Times.times(Duration.of(0, ChronoUnit.SECONDS), Duration.of(1, ChronoUnit.SECONDS), Duration.of(0, ChronoUnit.SECONDS)))));
		} else {
			Bukkit.getOnlinePlayers()
					.forEach(Audience::clearTitle);
			removeProtection();
			setModeToRUN();
			addTask(getGameplayTask().runTaskTimer(pl, 20L, 20L));
			this.cancel();
		}
		updatePlayingField();
		i--;
	}

	private void removeProtection() {

	}

	public TextColor convertToColor(int i) {
		if (i == 2) {
			return TextColor.color(247, 247, 56);
		} else if (i == 1) {
			return TextColor.color(56, 247, 56);
		} else return TextColor.color(247, 56, 56);
	}
}

abstract class RuntimeTask extends BukkitRunnable {
	@Override
	public void run() {

	}
}