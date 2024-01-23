/*
 * Made by IToncek
 *
 * Copyright (c) 2023.
 */

package fun.csyt.turfwars;

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import me.neznamy.tab.api.TabAPI;
import me.neznamy.tab.api.TabPlayer;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerPickupArrowEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Closeable;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Stream;

import static fun.csyt.turfwars.TurfState.*;

/**
 * Main Turf Wars Runtime
 */
public class TurfWarsRuntime implements CommandExecutor, TabCompleter, Listener, Closeable {
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
	boolean updateState = false;
	/** Material used to fill blue area */
	private Material blueMaterial = Material.LIGHT_BLUE_CONCRETE;
	private Material redBuildMaterial = Material.RED_WOOL;
	/** Current game ratio */
	private double ratio = 0d;
	/** Internal state holder */
	private TurfState state = INACTIVE;
	/** List of all runnables associated with this class */
	private final List<BukkitTask> tasks = new ArrayList<>();
	private final List<BukkitTask> arrowReplenishTasks = new ArrayList<>();
	private final HashMap<Arrow, Player> arrowsInFlightList = new HashMap<>();
	/** Is pvp active */
	boolean pvp = false;
	private Material blueBuildMaterial = Material.LIGHT_BLUE_WOOL;
	private TextColor redChatColor = TextColor.color(208, 17, 17);
	private TextColor blueChatColor = TextColor.color(29, 31, 222);

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


		plugin.getCommand("turf").setExecutor(this);
		plugin.getCommand("turf").setTabCompleter(this);
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		if (plugin.getServer().getPluginManager().getPlugin("TAB") != null) {
			TabAPI.getInstance().getPlaceholderManager().registerPlayerPlaceholder("%turfwars_progress%", 1000, this::buildTabText);
		}
	}


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
		updatePlayingField(true);
	}

	/**
	 * Updates playing field fill
	 *
	 * @implNote specifically disallows modifications to the area while not in game
	 */
	public void updatePlayingField() {
		updatePlayingField(false);
	}

	public long getMidBlock() {
		return Math.round((ratio / 2d + .5) * (redMaxCorner.getBlockX() - 2 - blueMinCorner.getBlockX())) + blueMinCorner.getBlockX() + 1;
	}

	/**
	 * Updates playing field fill
	 *
	 * @param override override 'not-in-game' checks
	 */
	public void updatePlayingField(boolean override) {
		if (!(state == PVP || state == BUILD) && !override) {
			Bukkit.getLogger().info(Component.text("Minigame has not started!", TextColor.color(150, 0, 0)).content());
			return;
		}

		long x = getMidBlock();

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

			if (x - 1 > blueMinCorner.x() - 1 && x - 1 < redMaxCorner.x() + 1) {
				CuboidRegion spacer = new CuboidRegion(BlockVector3.at(x - 1, 67, blueMinCorner.z()),
						BlockVector3.at(x - 1, 67, redMaxCorner.z()));
				editSession.setBlocks(spacer, BukkitAdapter.adapt(Material.WHITE_CONCRETE_POWDER.createBlockData()));
			}

			if (x - 1 > blueMinCorner.x() - 1 && x + 1 < redMaxCorner.x() + 1) {
				CuboidRegion spacer = new CuboidRegion(BlockVector3.at(x - 1, 68, blueMinCorner.z()),
						BlockVector3.at(x + 1, 72, redMaxCorner.z()));
				editSession.setBlocks(spacer, BukkitAdapter.adapt(Material.AIR.createBlockData()));
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

			ratio = 0;
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
			fillField(Material.END_PORTAL);
			closeCabines();
			clearBlocks();
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
		closeCabines();
		bluePlayers.forEach(p -> {
			Location loc = new Location(blueMinCorner.getWorld(), -37, 68, -3, -90, 0);
			p.teleportAsync(loc);
		});
		redPlayers.forEach(p -> {
			Location loc = new Location(blueMinCorner.getWorld(), 15, 68, -3, 90, 0);
			p.teleportAsync(loc);
		});
		Bukkit.getOnlinePlayers().forEach(p -> {
			p.getInventory().clear();
			p.setGameMode(GameMode.SURVIVAL);
		});
		BukkitRunnable gameplayTask = new BukkitRunnable() {
			final int buildTime = 20;
			final int fightTime = 60;
			int currentRemainingTime = 0;

			@Override
			public void run() {
				if (state == PVP || state == BUILD) {
					updateRatio(currentRemainingTime);
					currentRemainingTime--;
					if (currentRemainingTime < 0) {
						if (state == PVP) {
							state = BUILD;
							currentRemainingTime = buildTime;
							switchToBuild();
						} else if (state == BUILD) {
							state = PVP;
							currentRemainingTime = fightTime;
							switchToPVP();
						}
						updatePlayingField();
						Bukkit.getOnlinePlayers().forEach(p -> p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1, 1));
					}
				}
			}

		};
		BukkitTask countdownTask = new BukkitRunnable() {
			int i = 10;

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
					openCabines();
					state = PVP;
					updatePlayingField();
					tasks.add(gameplayTask.runTaskTimer(plugin, 20L, 20L));
					this.cancel();
				}
				i--;
			}
		}.runTaskTimer(plugin, 20L, 20L);

		tasks.add(countdownTask);
	}

	private void switchToBuild() {
		for (Player p : Stream.concat(redPlayers.stream(), bluePlayers.stream()).toList()) {
			boolean isRed = redPlayers.contains(p);
			p.getInventory().clear();
			ItemStack blocks = new ItemStack(isRed ? redBuildMaterial : blueBuildMaterial, 20);
			p.getInventory().addItem(blocks);
			if (redPlayers.contains(p)) setTunic(p, Color.RED);
			if (bluePlayers.contains(p)) setTunic(p, Color.BLUE);
		}
	}

	private void switchToPVP() {
		for (Player p : Stream.concat(redPlayers.stream(), bluePlayers.stream()).toList()) {
			arrowReplenishTasks.stream().map(t -> {
				t.cancel();
				return "";
			});
			p.getInventory().clear();
			ItemStack bow = new ItemStack(Material.BOW, 1);
			ItemStack arrow = new ItemStack(Material.ARROW, 1);
			p.getInventory().addItem(bow);
			p.getInventory().addItem(arrow);
			if (redPlayers.contains(p)) setTunic(p, Color.RED);
			if (bluePlayers.contains(p)) setTunic(p, Color.BLUE);
		}
	}

	private void updateRatio(int remTimeLocal) {
		Component c = buildActionBar(remTimeLocal);
		Bukkit.getOnlinePlayers().forEach(p -> p.sendActionBar(c));
	}

	private String buildTabText(TabPlayer tp) {
		double internalRatio = (ratio + 1) / 2;
		int lenght = 40;
		int blocks = (int) Math.round(internalRatio * lenght);
		int otherBlocks = lenght - blocks;

		return convertToText(TextColor.color(202, 144, 51)) + "[" +
				convertToText(redChatColor) + "|".repeat(blocks) +
				convertToText(blueChatColor) + "|".repeat(otherBlocks) +
				convertToText(TextColor.color(202, 144, 51)) + "]";
	}

	private String convertToText(TextColor c) {
		int r = c.red();
		int g = c.green();
		int b = c.blue();
		return "<#%02x%02x%02x>".formatted(r, g, b);
	}


	private Component buildActionBar(int remTimeLocal) {
		List<Component> components = new ArrayList<>();
		TextColor color = TextColor.color(0, 0, 0);
		if (state == PVP) {
			color = TextColor.color(255, 0, 6);
		} else if (state == BUILD) {
			color = TextColor.color(0, 94, 152);
		}

		components.add(Component.text(state.name() + "  ").style(Style.style().color(color).build()));

		int remOther = (state == PVP ? 60 : 20) - remTimeLocal;
		components.add(Component.text("[").style(Style.style().color(TextColor.color(202, 144, 51)).build()));
		components.add(Component.text("|".repeat(Math.max(0, remTimeLocal))).style(Style.style().color(TextColor.color(70, 255, 70)).build()));
		components.add(Component.text("|".repeat(Math.max(0, remOther))).style(Style.style().color(TextColor.color(255, 255, 255)).build()));
		components.add(Component.text("]").style(Style.style().color(TextColor.color(172, 122, 44)).build()));
		return Component.join(JoinConfiguration.noSeparators(), components);
	}

	/**
	 * Forcefully terminates minigame
	 */
	public void terminateMinigame() {
		tasks.forEach(BukkitTask::cancel);
		tasks.forEach(t -> Bukkit.getScheduler().cancelTask(t.getTaskId()));

		resetPlayingField();

		ratio = 0;
		if (bluePlayers != null) bluePlayers.clear();
		if (redPlayers != null) redPlayers.clear();
	}


	public void victory(boolean isRed) {
		tasks.forEach(BukkitTask::cancel);
		tasks.forEach(t -> Bukkit.getScheduler().cancelTask(t.getTaskId()));

		clearBlocks();
		Bukkit.getOnlinePlayers().forEach(p -> p.getInventory().clear());

		System.out.println(isRed);
		List<Location> launchBlocks = new ArrayList<>(40);
		for (int x = blueMinCorner.getBlockX() + 1; x < redMaxCorner.getBlockX(); x++) {
			for (int z = blueMinCorner.getBlockZ() + 1; z < redMaxCorner.getBlockZ(); z++) {
				launchBlocks.add(new Location(blueMinCorner.getWorld(), x, 68, z));
			}
		}
		new BukkitRunnable() {
			int runs = 50;

			@Override
			public void run() {
				fireFireworks(launchBlocks, isRed);
				runs--;
				if (runs < 0) {
					this.cancel();
					Bukkit.getOnlinePlayers().forEach(p -> teleportToSpawn(p));
					tasks.add(new BukkitRunnable() {
						@Override
						public void run() {
							resetPlayingField();
						}
					}.runTaskLater(plugin, 5L));
				}
			}
		}.runTaskTimer(plugin, 0L, 8L);

	}

	private void fireFireworks(List<Location> launchBlocks, boolean isRed) {
		Random r = new Random();
		for (int i = 0; i < 16; i++) {
			Location b = launchBlocks.get(r.nextInt(launchBlocks.size()));
			Firework fw = (Firework) b.getWorld().spawnEntity(b, EntityType.FIREWORK);
			FireworkMeta fwm = fw.getFireworkMeta();
			fwm.setPower(r.nextInt(2) + 1);
			fwm.addEffect(FireworkEffect.builder().withColor(isRed ? c(redChatColor) : c(blueChatColor)).withFade(isRed ? c(blueChatColor) : c(redChatColor)).with(FireworkEffect.Type.BALL_LARGE).build());
			fwm.addEffect(FireworkEffect.builder().withColor(isRed ? c(redChatColor) : c(blueChatColor)).withFade(isRed ? c(blueChatColor) : c(redChatColor)).with(FireworkEffect.Type.BURST).build());
			fwm.addEffect(FireworkEffect.builder().withColor(isRed ? c(redChatColor) : c(blueChatColor)).withFade(isRed ? c(blueChatColor) : c(redChatColor)).with(FireworkEffect.Type.BALL).build());
			fw.setFireworkMeta(fwm);
		}
	}

	private Color c(TextColor color) {
		return Color.fromRGB(color.red(), color.green(), color.blue());
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
						sender.sendMessage(Component.text("Minigame is not in SETUP!", TextColor.color(150, 0, 0)));
						return true;
					}
					redPlayers.clear();
					for (int i = 1; i < args.length; i++) {
						redPlayers.add(Bukkit.getPlayer(args[i]));
					}
				}
				case "setTeamBlue" -> {
					if (!(state == SETUP)) {
						sender.sendMessage(Component.text("Minigame is not in SETUP!", TextColor.color(150, 0, 0)));
						return true;
					}
					bluePlayers.clear();
					for (int i = 1; i < args.length; i++) {
						bluePlayers.add(Bukkit.getPlayer(args[i]));
					}
				}
				case "setRedBlock", "setBlueBlock", "setRedBuildBlock", "setBlueBuildBlock" -> {
					if (!(state == SETUP)) {
						sender.sendMessage(Component.text("Minigame is not in SETUP!", TextColor.color(150, 0, 0)));
						return true;
					}
					try {
						switch (args[0]) {
							case "setRedBlock" -> redMaterial = Material.valueOf(args[1]);
							case "setBlueBlock" -> blueMaterial = Material.valueOf(args[1]);
							case "setRedBuildBlock" -> redBuildMaterial = Material.valueOf(args[1]);
							case "setBlueBuildBlock" -> blueBuildMaterial = Material.valueOf(args[1]);
						}
					} catch (IllegalArgumentException e) {
						sender.sendMessage(Component.text("Unable to do so, this is not a valid material. \nPlease refer to https://jd.papermc.io/paper/1.20/org/bukkit/Material.html#enum-constant-summary", TextColor.color(150, 0, 0)));
					}
				}
				case "reset" -> resetPlayingField();
				case "setup" -> startSetup();
				case "verify" -> validateSetup();
				case "start" -> startMinigame();
				case "terminate" -> {
					terminateMinigame();
					resetPlayingField();
				}
				case "forceTogglePVP" -> {
					pvp = !pvp;
					updateState = true;
				}
				case "forceWin" -> victory(Boolean.parseBoolean(args[1]));
				case "setRedCC", "setBlueCC" -> {
					int r = Integer.parseInt(args[1]);
					int g = Integer.parseInt(args[2]);
					int b = Integer.parseInt(args[3]);
					TextColor color = TextColor.color(r, g, b);
					if (args[0].equals("setRedCC")) {
						redChatColor = color;
					} else {
						blueChatColor = color;
					}
				}
				default -> sender.sendMessage(Component.text("Not a valid argument!", TextColor.color(255, 0, 0)));
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
			if (args.length == 1) {
				return List.of("setTeamRed", "setTeamBlue", "setRedBlock", "setBlueBlock", "setBlueBuildBlock", "setRedBuildBlock", "setRedCC", "setBlueCC", "setup", "verify", "start", "terminate", "forceTogglePVP", "forceWin");
			} else if (args[0].equals("setTeamRed") || args[0].equals("setTeamBlue")) {
				return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
			} else if (args.length == 2 && (args[0].equals("setRedBlock") || args[0].equals("setBlueBlock") || args[0].equals("setRedBuildBlock") || args[0].equals("setBlueBuildBlock"))) {
				return Arrays.stream(Material.values()).map(Material::name).toList();
			} else if (args[0].equals("forceWin") && args.length == 2) {
				return Stream.of(true, false).map(b -> b + "").toList();
			} else if (args[0].equals("setRedCC") || args[0].equals("setBlueCC")) {
				return switch (args.length) {
					case 2 -> List.of("<red>");
					case 3 -> List.of("<green>");
					case 4 -> List.of("<blue>");
					default -> List.of();
				};
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
			CuboidRegion red = new CuboidRegion(BlockVector3.at(-13, 70, -1),
					BlockVector3.at(13, 68, -5));
			CuboidRegion blue = new CuboidRegion(BlockVector3.at(-34, 70, -1),
					BlockVector3.at(-34, 68, -5));

			editSession.setBlocks(red, BukkitAdapter.adapt(mat.createBlockData()));
			editSession.setBlocks(blue, BukkitAdapter.adapt(mat.createBlockData()));
		} catch (MaxChangedBlocksException e) {
			e.printStackTrace();
		}
	}

	private void clearBlocks() {
		try (EditSession editSession = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(blueMinCorner.getWorld()))) {
			CuboidRegion blue = new CuboidRegion(BlockVector3.at(blueMinCorner.x(), 68, blueMinCorner.z()),
					BlockVector3.at(redMaxCorner.x(), 73, redMaxCorner.z()));
			editSession.setBlocks(blue, BukkitAdapter.adapt(Material.AIR.createBlockData()));
		} catch (MaxChangedBlocksException e) {
			e.printStackTrace();
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerLaunchProjectile(EntityShootBowEvent event) {
		if (event.getEntity().getWorld() != blueMinCorner.getWorld()) return;
		if (state != PVP) {
			event.getEntity().sendActionBar(Component.text("Není PVP!", TextColor.color(255, 0, 0)));
		}
		BukkitTask replenishTask = new BukkitRunnable() {
			@Override
			public void run() {
				((Player) event.getEntity()).getInventory().addItem(new ItemStack(Material.ARROW, 1));
				((Player) event.getEntity()).playSound(event.getEntity().getLocation(), Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 1, 1);

				updatePlayingField();
			}
		}.runTaskLater(plugin, 40L);
		arrowReplenishTasks.add(replenishTask);
		Player p = (Player) event.getEntity();
		arrowsInFlightList.put((Arrow) event.getProjectile(), p);
	}

	@EventHandler(ignoreCancelled = true)
	public void onProjectileHit(ProjectileHitEvent event) {
		if (event.getHitEntity() != null && event.getEntity() instanceof Arrow a) {
			if (event.getEntity().getWorld() != blueMinCorner.getWorld()) return;
			if (event.getHitEntity() instanceof Player p) {
				Player originPlayer = arrowsInFlightList.get(a);
				if (bluePlayers.contains(p) && redPlayers.contains(originPlayer)) {
					if (ratio - .1 <= -1) {
						victory(true);
					} else {
						updateRatio(ratio - .1);
						originPlayer.playSound(originPlayer.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
					}
					p.setHealth(0);
				} else if (redPlayers.contains(p) && bluePlayers.contains(originPlayer)) {
					if (ratio + .1 >= 1) {
						victory(false);
					} else {
						updateRatio(ratio + .1);
						originPlayer.playSound(originPlayer.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
					}
					p.setHealth(0);
				}
				arrowsInFlightList.remove(a);
				updatePlayingField();
			}
		} else if (event.getEntity() instanceof Arrow && event.getHitBlock() != null) {
			if ((event.getHitBlock().getType() == redBuildMaterial) || (event.getHitBlock().getType() == blueBuildMaterial)) {
				event.getHitBlock().setType(Material.AIR);
				updatePlayingField();
			}
			event.getEntity().remove();
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerPickupArrow(PlayerPickupArrowEvent event) {
		if (event.getPlayer().getWorld() != blueMinCorner.getWorld()) return;
		event.setCancelled(true);
	}

	@EventHandler(ignoreCancelled = true)
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.getEntity().getWorld() != blueMinCorner.getWorld()) return;
		if (!event.getCause().equals(EntityDamageEvent.DamageCause.PROJECTILE)) {
			event.setCancelled(true);
		}
	}


	@EventHandler(ignoreCancelled = true)
	public void onPlayerPostRespawn(PlayerPostRespawnEvent event) {
		if (event.getPlayer().getWorld() != blueMinCorner.getWorld()) return;
		Player p = event.getPlayer();
		if (bluePlayers.contains(p)) {
			p.teleportAsync(new Location(blueMinCorner.getWorld(), -37, 68, -3, -90, 0));
			setTunic(p, Color.BLUE);
		} else if (redPlayers.contains(p)) {
			p.teleportAsync(new Location(blueMinCorner.getWorld(), 15, 68, -3, 90, 0));
			setTunic(p, Color.RED);
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent event) {
		if (event.getBlock().getWorld() != blueMinCorner.getWorld()) return;
		if (event.getBlock().getY() > 72) event.setCancelled(true);
		if (event.getBlock().getType() == redBuildMaterial) {
			if (event.getBlock().getLocation().getBlockX() < getMidBlock() + 2) {
				event.setCancelled(true);
			}
		} else if (event.getBlock().getType() == blueBuildMaterial) {
			if (event.getBlock().getLocation().getBlockX() > getMidBlock() - 2) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent event) {
		if (event.getBlock().getWorld() != blueMinCorner.getWorld()) return;
		if (!(event.getPlayer().isOp() && event.getPlayer().getGameMode().equals(GameMode.CREATIVE))) {
			event.setCancelled(true);
		}
	}


	@EventHandler(ignoreCancelled = true)
	public void onPlayerPortal(PlayerPortalEvent event) {
		if (event.getFrom().getWorld() != blueMinCorner.getWorld()) return;
		if (event.getTo().getWorld().getEnvironment().equals(World.Environment.THE_END)) {
			teleportToSpawn(event.getPlayer());
		}
	}

	public void teleportToSpawn(Player p) {
		p.teleportAsync(new Location(p.getWorld(), 0, 86, 0));
	}

	public void setTunic(Player p, Color c) {
		ItemStack redTunic = new ItemStack(Material.LEATHER_CHESTPLATE);
		LeatherArmorMeta meta = (LeatherArmorMeta) redTunic.getItemMeta();

		meta.setColor(c);

		redTunic.setItemMeta(meta);
		p.getInventory().setChestplate(redTunic);
	}

	@Override
	public void close() {
		plugin.getCommand("turf").setExecutor(null);
		plugin.getCommand("turf").setTabCompleter(null);
		HandlerList.unregisterAll(this);

		if (plugin.getServer().getPluginManager().getPlugin("TAB") != null) {
			TabAPI.getInstance().getPlaceholderManager().unregisterPlaceholder("%turfwars_progress%");
		}
	}

	public void loadTeam(boolean isRed, String data, CommandSender sender) {
		try {
			Team team = Team.valueOf(data);
			sender.sendMessage(Component.text(team.color.asHexString(), team.color));
			if (isRed) {
				redChatColor = team.color;
				redMaterial = team.ground;
				redBuildMaterial = team.build;
			} else {
				blueChatColor = team.color;
				blueMaterial = team.ground;
				blueBuildMaterial = team.build;
			}
		} catch (IllegalArgumentException e) {
			sender.sendMessage(Component.text("Unable to parse value of '" + data + "'", TextColor.color(255, 0, 0)));
		}
	}
}