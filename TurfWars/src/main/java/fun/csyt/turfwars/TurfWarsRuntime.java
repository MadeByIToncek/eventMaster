/*
 * Made by IToncek
 *
 * Copyright (c) 2023.
 */

package fun.csyt.turfwars;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.TitlePart;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static fun.csyt.turfwars.TurfState.*;

/**
 * Main Turf Wars Runtime
 */
public class TurfWarsRuntime implements CommandExecutor, TabCompleter, Listener {
	/** Carries original object for bukkit interactions */
	private final TurfWars plugin;
	/** Minimal corner of the arena (blue side) */
	private final Location blueMinCorner;
	/** Maximal corner of the arena (red side) */
	private final Location redMaxCorner;
	/** Lisf of red team's players */
	private List<Player> redPlayers = new ArrayList<>(3);
	/** Lisf of blue team's players */
	private List<Player> bluePlayers = new ArrayList<>(3);
	/** Material used to fill red area */
	private Material redMaterial = Material.RED_CONCRETE;
	/** Material used to fill blue area */
	private Material blueMaterial = Material.LIGHT_BLUE_CONCRETE;
	/** Current game ratio */
	private double ratio = 0d;
	/** Internal state holder */
	private TurfState state = INACTIVE;
	/** List of all runnables associated with this class */
	private final List<BukkitTask> tasks = new ArrayList<>();
	/** Is pvp active */
	boolean pvp = false;

	/**
	 * Creates TurfWarsRuntime object to do stuff
	 *
	 * @param blueMinCorner the corner with the lowest x and z coordinates, defines blue's side
	 * @param redMaxCorner  the corner with the highest x and z coordinates, defines red's side
	 * @param plugin        carries the parent class, used to run this from another plugin
	 */
	public TurfWarsRuntime(Location blueMinCorner, Location redMaxCorner, TurfWars plugin) {
		this.blueMinCorner = blueMinCorner;
		this.redMaxCorner = redMaxCorner;
		this.plugin = plugin;
		resetPlayingField();
	}

	/**
	 * Updates game ratio
	 *
	 * @param ratio ratio to set game to
	 *
	 * @apiNote DEVELOPEMENT/DEBUG ONLY, DO NOT INTERFERE WITH DURING GAME
	 */
	public void updateRatio(double ratio) {
		if (ratio >= -1d && ratio <= 1d) {
			this.ratio = ratio;
		} else {
			Bukkit.getLogger().info("Unable to move, ratio out of bounds, requested: " + ratio + " (low: " + (ratio < -1d) + ", high: " + (ratio > 1d) + ")");
		}
		updatePlayingField();
	}

	/**
	 * Updates playing field fill
	 *
	 * @implNote specifically disallows modifications to the area while not in game
	 */
	public void updatePlayingField() {
		updatePlayingField(false);
	}

	public static String convertToText(int i) {
		return switch (i) {
			case 10 -> "\u05c3 \u05c2";
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

	/**
	 * Updates playing field fill
	 *
	 * @param override override 'not-in-game' checks
	 */
	private void updatePlayingField(boolean override) {
		if (state != PVP && !override) {
			Bukkit.getLogger().info(TextColor.color(150, 0, 0) + "Minigame has not started!");
			return;
		}

		long x = Math.round((ratio / 2d + .5) * (redMaxCorner.getBlockX() - 2 - blueMinCorner.getBlockX())) + blueMinCorner.getBlockX() + 1;

		if (x > redMaxCorner.getBlockX() + 1 || x < blueMinCorner.getBlockX() - 1) return;

		try (EditSession editSession = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(blueMinCorner.getWorld()))) {
			if (x - 2 > blueMinCorner.x() - 1 && x - 2 < redMaxCorner.x() + 1) {
				CuboidRegion blue = new CuboidRegion(BlockVector3.at(blueMinCorner.x(), 67, blueMinCorner.z()),
						BlockVector3.at(x - 2, 67, redMaxCorner.z()));
				editSession.setBlocks(blue, BukkitAdapter.adapt(blueMaterial.createBlockData()));
			}
			if (x - 1 > blueMinCorner.x() - 1 && x - 1 < redMaxCorner.x() + 1) {
				CuboidRegion spacer = new CuboidRegion(BlockVector3.at(x - 1, 67, blueMinCorner.z()),
						BlockVector3.at(x - 1, 67, redMaxCorner.z()));
				editSession.setBlocks(spacer, BukkitAdapter.adapt(Material.WHITE_CONCRETE_POWDER.createBlockData()));
			}
			if (x > blueMinCorner.x() - 1 && x < redMaxCorner.x() + 1) {
				CuboidRegion white = new CuboidRegion(BlockVector3.at(x, 67, blueMinCorner.z()),
						BlockVector3.at(x, 67, redMaxCorner.z()));
				editSession.setBlocks(white, BukkitAdapter.adapt(Material.WHITE_CONCRETE.createBlockData()));
			}
			if (x + 1 > blueMinCorner.x() - 1 && x + 1 < redMaxCorner.x() + 1) {
				CuboidRegion spacer2 = new CuboidRegion(BlockVector3.at(x + 1, 67, blueMinCorner.z()),
						BlockVector3.at(x + 1, 67, redMaxCorner.z()));
				editSession.setBlocks(spacer2, BukkitAdapter.adapt(Material.WHITE_CONCRETE_POWDER.createBlockData()));
			}
			if (x + 2 > blueMinCorner.x() - 1 && x + 2 < redMaxCorner.x() + 1) {
				CuboidRegion red = new CuboidRegion(BlockVector3.at(x + 2, 67, blueMinCorner.z()),
						BlockVector3.at(redMaxCorner.x(), 67, redMaxCorner.z()));
				editSession.setBlocks(red, BukkitAdapter.adapt(redMaterial.createBlockData()));
			}
			editSession.commit();
		} catch (MaxChangedBlocksException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Prepares server for setup
	 *
	 * @implNote run before setting anything up, resets the minigame!
	 */
	public void startSetup() {
		try {
			fillField(Material.BLACK_CONCRETE);
			if (bluePlayers != null) bluePlayers.clear();
			if (redPlayers != null) redPlayers.clear();

			bluePlayers = new ArrayList<>(3);
			redPlayers = new ArrayList<>(3);

			redMaterial = Material.RED_CONCRETE;
			blueMaterial = Material.LIGHT_BLUE_CONCRETE;
		} catch (MaxChangedBlocksException | UnsupportedOperationException e) {
			throw new RuntimeException(e);
		} finally {
			state = SETUP;
		}
	}

	/**
	 * Resets playing field, preparing for storage
	 */
	public void resetPlayingField() {
		try {
			terminateMinigame();
			fillField(Material.END_PORTAL);

			if (bluePlayers != null) bluePlayers.clear();
			if (redPlayers != null) redPlayers.clear();

			redMaterial = Material.RED_CONCRETE;
			blueMaterial = Material.LIGHT_BLUE_CONCRETE;
		} catch (MaxChangedBlocksException e) {
			throw new RuntimeException(e);
		} finally {
			state = INACTIVE;
		}
	}

	/**
	 * Performs checks before allowing game start
	 *
	 * @implNote required to run before startMinigame()
	 */
	public void validateSetup() {
		if (redPlayers.isEmpty()) {
			Bukkit.getLogger().info("Red team members are not set up properly!");
			return;
		}
		if (bluePlayers.isEmpty()) {
			Bukkit.getLogger().info("Blue team members are not set up properly!");
			return;
		}
		for (Player p : Stream.concat(redPlayers.stream(), bluePlayers.stream()).toList()) {
			if (!p.isOnline()) {
				Bukkit.getLogger().info("One of the players isn't online!");
				return;
			}
		}
		updatePlayingField(true);
		closeCabines();
		state = READY;
	}

	/**
	 * Starts minigame
	 */
	public void startMinigame() {
		bluePlayers.forEach(p -> {
			Location loc = new Location(blueMinCorner.getWorld(), -37, 68, -3);
			p.teleportAsync(loc);
			p.setBedSpawnLocation(loc);
		});
		redPlayers.forEach(p -> {
			Location loc = new Location(blueMinCorner.getWorld(), 15, 68, -3);
			p.teleportAsync(loc);
			p.setBedSpawnLocation(loc);
		});

		BukkitTask countdownTask = new BukkitRunnable() {
			final int i = 10;

			@Override
			public void run() {
				if (i == 3) Bukkit.getOnlinePlayers()
						.forEach(p -> p.playSound(p.getLocation(), "minecraft:countdown", SoundCategory.MASTER, 1, 1));
				else if (i > 3) Bukkit.getOnlinePlayers()
						.forEach(p -> p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, SoundCategory.MASTER, 1, 1));

				if (i > 0) {
					Bukkit.getOnlinePlayers()
							.stream()
							.peek(p -> p.sendTitlePart(TitlePart.TITLE, Component.text("א", Style.style().color(TextColor.color(247, 178, 4)).build())))
							.peek(p -> p.sendTitlePart(TitlePart.SUBTITLE, Component.text(convertToText(i), Style.style().color(convertToColor(i)).build())))
							.peek(p -> p.sendTitlePart(TitlePart.TIMES, Title.Times.times(Duration.of(0, ChronoUnit.SECONDS), Duration.of(1, ChronoUnit.SECONDS), Duration.of(0, ChronoUnit.SECONDS))));
				} else {
					Bukkit.getOnlinePlayers()
							.forEach(Audience::clearTitle);
					openCabines();
					this.cancel();
				}
			}
		}.runTaskTimer(plugin, 20L, 20L);

		BukkitTask gameplayTask = new BukkitRunnable() {
			final int buildTime = 20;
			final int fightTime = 60;
			int currentRemainingTime = buildTime;
			final BossBar bar = BossBar.bossBar(Component.text("Building", Style.style().color(TextColor.color(32, 77, 110)).build()), computeRatio(), BossBar.Color.BLUE, BossBar.Overlay.PROGRESS); //Bukkit.createBossBar(TextColor.color(32, 77, 110) + "Building", BarColor.BLUE, BarStyle.SOLID);
			final boolean init = initialize();

			private boolean initialize() {
				for (Player p : Bukkit.getOnlinePlayers()) {
					p.showBossBar(bar);
				}
				bar.progress(computeRatio());
				updateState();
				return true;
			}

			private void destroy() {
				if (init) {
					for (Player p : Bukkit.getOnlinePlayers()) {
						p.hideBossBar(bar);
					}
				}
			}

			private void updateRatio() {
				bar.progress(computeRatio());
			}

			private float computeRatio() {
				return (float) currentRemainingTime / (pvp ? fightTime : buildTime);
			}

			private void updateState() {
				currentRemainingTime = pvp ? fightTime : buildTime;
				bar.name(Component.text(pvp ? "PVP" : "Building", pvp ? Style.style().color(TextColor.color(94, 0, 0)).build() : Style.style().color(TextColor.color(32, 77, 110)).build()));
				bar.color(pvp ? BossBar.Color.RED : BossBar.Color.BLUE);
			}

			@Override
			public void run() {
				currentRemainingTime--;
				updateRatio();
				if (currentRemainingTime < 0) {
					pvp = !pvp;
					updateState();
				}
			}


		}.runTaskTimer(plugin, 20L, 20L);
		tasks.add(countdownTask);
		tasks.add(gameplayTask);
	}

	/**
	 * Forcefully terminates minigame
	 */
	public void terminateMinigame() {

	}

	/**
	 * Implementing /turf command
	 *
	 * @param sender  Source of the command
	 * @param command Command which was executed
	 * @param label   Alias of the command which was used
	 * @param args    Passed command arguments
	 *
	 * @return true if valid
	 */
	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if (sender.isOp()) {
			switch (args[0]) {
				case "setTeamRed" -> {
					if (!(state == SETUP)) {
						sender.sendMessage(TextColor.color(150, 0, 0) + "Minigame is not in SETUP!");
						return true;
					}
					redPlayers.clear();
					for (int i = 1; i < args.length; i++) {
						redPlayers.add(Bukkit.getPlayer(args[i]));
					}
				}
				case "setTeamBlue" -> {
					if (!(state == SETUP)) {
						sender.sendMessage(TextColor.color(150, 0, 0) + "Minigame is not in SETUP!");
						return true;
					}
					bluePlayers.clear();
					for (int i = 1; i < args.length; i++) {
						bluePlayers.add(Bukkit.getPlayer(args[i]));
					}
				}
				case "setRedBlock" -> {
					if (!(state == SETUP)) {
						sender.sendMessage(TextColor.color(150, 0, 0) + "Minigame is not in SETUP!");
						return true;
					}
					try {
						redMaterial = Material.valueOf(args[1]);
					} catch (IllegalArgumentException e) {
						sender.sendMessage(TextColor.color(150, 0, 0) +
								"Unable to do so, this is not a valid material. \nPlease refer to https://jd.papermc.io/paper/1.20/org/bukkit/Material.html#enum-constant-summary");
					}
				}
				case "setBlueBlock" -> {
					if (!(state == SETUP)) {
						sender.sendMessage(TextColor.color(150, 0, 0) + "Minigame is not in SETUP!");
						return true;
					}
					try {
						blueMaterial = Material.valueOf(args[1]);
					} catch (IllegalArgumentException e) {
						sender.sendMessage(TextColor.color(150, 0, 0) +
								"Unable to do so, this is not a valid material. \nPlease refer to https://jd.papermc.io/paper/1.20/org/bukkit/Material.html#enum-constant-summary");
					}
				}
				case "reset" -> {
					resetPlayingField();
				}
				case "setup" -> {
					startSetup();
				}
				case "verify" -> {
					validateSetup();
				}
				case "start" -> {
					startMinigame();
				}
				case "terminate" -> {
					terminateMinigame();
					resetPlayingField();
				}
			}
		}
		return true;
	}

	/**
	 * Fills the entire area of playing field with specified block
	 *
	 * @param m Material to fill the area with
	 *
	 * @throws MaxChangedBlocksException thrown if too many blocks are changed
	 */
	private void fillField(Material m) throws MaxChangedBlocksException {
		try (EditSession editSession = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(blueMinCorner.getWorld()))) {
			CuboidRegion blue = new CuboidRegion(BlockVector3.at(blueMinCorner.x(), 67, blueMinCorner.z()),
					BlockVector3.at(redMaxCorner.x(), 67, redMaxCorner.z()));
			editSession.setBlocks(blue, BukkitAdapter.adapt(m.createBlockData()));
		}
	}

	/**
	 * Implementing /turf helper
	 *
	 * @param sender  Source of the command.  For players tab-completing a
	 *                command inside a command block, this will be the player, not
	 *                the command block.
	 * @param command Command which was executed
	 * @param label   Alias of the command which was used
	 * @param args    The arguments passed to the command, including final
	 *                partial argument to be completed
	 *
	 * @return List of strings with helpers
	 */
	@Override
	public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if (sender.isOp()) {
			switch (args.length) {
				case 1 ->
						List.of("setTeamRed", "setTeamBlue", "setRedBlock", "setBlueBlock", "setup", "verify", "start", "terminate");
				default -> List.of();
			}
		}
		return List.of();
	}

	private void closeCabines() {
		fillCabines(Material.BARRIER);
	}

	private void openCabines() {
		fillCabines(Material.AIR);
	}

	public TextColor convertToColor(int i) {
		if (i == 2) {
			return TextColor.color(247, 247, 56);
		} else if (i == 1) {
			return TextColor.color(56, 247, 56);
		} else return TextColor.color(247, 56, 56);
	}

	private void fillCabines(Material mat) {
		try (EditSession editSession = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(blueMinCorner.getWorld()))) {
			CuboidRegion red = new CuboidRegion(BlockVector3.at(-34, 70, -1),
					BlockVector3.at(13, 68, -5));
			CuboidRegion blue = new CuboidRegion(BlockVector3.at(-34, 70, -1),
					BlockVector3.at(13, 68, -5));

			editSession.setBlocks(red, BukkitAdapter.adapt(mat.createBlockData()));
			editSession.setBlocks(blue, BukkitAdapter.adapt(mat.createBlockData()));
		} catch (MaxChangedBlocksException e) {
			e.printStackTrace();
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerLaunchProjectile(EntityShootBowEvent event) {
		if (!pvp) {
			event.setCancelled(true);
			event.getEntity().sendActionBar(Component.text("Aktuálně nemůžeš útočit", TextColor.color(117, 0, 0)));
			((Player) event.getEntity()).getInventory().addItem(new ItemStack(Material.ARROW, 1));
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player p && event.getCause().equals(EntityDamageEvent.DamageCause.PROJECTILE)) {
			if (bluePlayers.contains(p)) {
				updateRatio(ratio - .1);
				((Player) event.getEntity()).setHealth(0);
			} else if (redPlayers.contains(p)) {
				if (ratio + .1 > 1) {

				} else {
					updateRatio(ratio + .1);
					((Player) event.getEntity()).setHealth(0);
				}
			}
		}
	}
}