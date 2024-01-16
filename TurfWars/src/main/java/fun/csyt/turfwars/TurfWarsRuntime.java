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
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static fun.csyt.turfwars.TurfState.*;

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

	/**
	 * Updates playing field fill
	 *
	 * @param override override 'not-in-game' checks
	 */
	private void updatePlayingField(boolean override) {
		if (state != INGAME && !override) {
			Bukkit.getLogger().info(Color.fromRGB(150, 0, 0) + "Minigame has not started!");
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
	 * Resets playing field, preparing for storage
	 */
	public void resetPlayingField() {
		try {
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
	 * Performs checks before allowing game start
	 *
	 * @implNote required to run before startMinigame()
	 */
	public void validateSetup() {
		if (check()) {
			updatePlayingField(true);
			state = READY;
		}
	}

	/**
	 * Performs team checks and more stuff
	 *
	 * @return true if server is ready
	 */
	private boolean check() {
		if (redPlayers.isEmpty()) {
			Bukkit.getLogger().info("Red team members are not set up properly!");
			return false;
		}
		if (bluePlayers.isEmpty()) {
			Bukkit.getLogger().info("Blue team members are not set up properly!");
			return true;
		}
		for (Player p : Stream.concat(redPlayers.stream(), bluePlayers.stream()).toList()) {
			if (!p.isOnline()) {
				Bukkit.getLogger().info("One of the players isn't online!");
				return true;
			}
		}
		return true;
	}

	/**
	 * Starts minigame
	 */
	public void startMinigame() {

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
						sender.sendMessage(Color.fromRGB(150, 0, 0) + "Minigame is not in SETUP!");
						return true;
					}
					redPlayers.clear();
					for (int i = 1; i < args.length; i++) {
						redPlayers.add(Bukkit.getPlayer(args[i]));
					}
				}
				case "setTeamBlue" -> {
					if (!(state == SETUP)) {
						sender.sendMessage(Color.fromRGB(150, 0, 0) + "Minigame is not in SETUP!");
						return true;
					}
					bluePlayers.clear();
					for (int i = 1; i < args.length; i++) {
						bluePlayers.add(Bukkit.getPlayer(args[i]));
					}
				}
				case "setRedBlock" -> {
					if (!(state == SETUP)) {
						sender.sendMessage(Color.fromRGB(150, 0, 0) + "Minigame is not in SETUP!");
						return true;
					}
					try {
						redMaterial = Material.valueOf(args[1]);
					} catch (IllegalArgumentException e) {
						sender.sendMessage(Color.fromRGB(150, 0, 0) +
								"Unable to do so, this is not a valid material. \nPlease refer to https://jd.papermc.io/paper/1.20/org/bukkit/Material.html#enum-constant-summary");
					}
				}
				case "setBlueBlock" -> {
					if (!(state == SETUP)) {
						sender.sendMessage(Color.fromRGB(150, 0, 0) + "Minigame is not in SETUP!");
						return true;
					}
					try {
						blueMaterial = Material.valueOf(args[1]);
					} catch (IllegalArgumentException e) {
						sender.sendMessage(Color.fromRGB(150, 0, 0) +
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
				}
			}
		}
		return true;
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
				case 0 ->
						List.of("setTeamRed", "setTeamBlue", "setRedBlock", "setBlueBlock", "setup", "verify", "start", "terminate");
			}
		}
		return List.of();
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
}