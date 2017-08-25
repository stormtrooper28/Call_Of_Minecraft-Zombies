//MODES
//DISABLED
//INGAME
//STARTING
//WAITING
//ERROR

package com.zombies.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.zombies.COMZombiesMain;
import net.minecraft.server.v1_12_R1.BlockPosition;
import net.minecraft.server.v1_12_R1.PacketPlayOutBlockBreakAnimation;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_12_R1.CraftServer;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;

import com.zombies.commands.CommandUtil;
import com.zombies.config.CustomConfig;
import com.zombies.game.features.Door;
import com.zombies.game.features.DownedPlayer;
import com.zombies.game.features.RandomBox;
import com.zombies.game.managers.BarrierManager;
import com.zombies.game.managers.BoxManager;
import com.zombies.game.managers.DoorManager;
import com.zombies.game.managers.DownedPlayerManager;
import com.zombies.game.managers.PerkManager;
import com.zombies.game.managers.SignManager;
import com.zombies.game.managers.TeleporterManager;
import com.zombies.guns.Gun;
import com.zombies.guns.GunManager;
import com.zombies.guns.GunType;
import com.zombies.kits.KitManager;
import com.zombies.leaderboards.Leaderboards;
import com.zombies.leaderboards.PlayerStats;
import com.zombies.spawning.SpawnManager;

/**
 * Main game class.
 */
public class Game {
	
	/**
	 * Main class / plugin instance.
	 */
	private COMZombiesMain plugin;
	
	/**
	 * List of every player contained in game.
	 */
	public List<Player> players = new ArrayList<Player>();
	
	/**
	 * Status of the game.
	 */
	public ArenaStatus mode = ArenaStatus.DISABLED;
	
	/**
	 * Assuring that the game has every warp, spectator, game, and lobby.
	 */
	private boolean hasWarps = false;
	
	/**
	 * Assuring that the game has every point, point one for the arena, and
	 * point two.
	 */
	private boolean hasPoints = false;
	
	/**
	 * If the game is disabled / edit mode, true.
	 */
	private boolean isDisabled = false;
	
	/**
	 * If double points is active.
	 */
	private boolean doublePoints = false;
	
	/**
	 * If fire salse is active
	 */
	private boolean isFireSale = false;
	
	/**
	 * If insta kill is active.
	 */
	private static boolean instaKill = false;
	
	/**
	 * If the power is on
	 */
	private boolean power;
	
	/**
	 * If the game has power enabled
	 */
	private boolean powerEnabled;
	
	/**
	 * Contains a player and the gun manager corresponding to that player.
	 */
	private HashMap<Player, GunManager> playersGuns = new HashMap<Player, GunManager>();
	
	/**
	 * Current wave number.
	 */
	public int waveNumber = 0;
	
	/**
	 * World name for the game.
	 */
	public String worldName = "world";
	
	/**
	 * Arena name for the game.
	 */
	private String arenaName;
	
	/**
	 * Min point in which the game is contained.
	 *
	 * @see Arena arena
	 */
	private Location min;
	
	/**
	 * Max point in which the game is contained.
	 * 
	 * @see Arena arena
	 */
	private Location max;
	
	/**
	 * Location players will teleport to when the game starts.
	 */
	private Location playerTPLocation;
	
	/**
	 * Location players will teleport to when they leave or die.
	 */
	private Location spectateLocation;
	
	/**
	 * Location in which players will teleport upon first join.
	 */
	private Location lobbyLocation;
	
	/**
	 * Arena contained in the game.
	 */
	public Arena arena;
	
	/**
	 * Manager controlling zombie spawing and spawn points for the game.
	 */
	public SpawnManager spawnManager;
	
	/**
	 * Auto start timer, constructed upon join.
	 */
	public AutoStart starter;
	
	/**
	 * contains all of the Mysteryboxes in the game
	 */
	public BoxManager boxManager;
	
	/**
	 * contains all of the Barriers in the game
	 */
	public BarrierManager barrierManager;
	
	/**
	 * contains all of the doors in the game
	 */
	public DoorManager doorManager;
	
	/**
	 * contains all of the perks in the game as well as the perks that are
	 * currently dropped in the map
	 */
	public PerkManager perkManager;
	
	/**
	 * contains all of the teleporters in the game
	 */
	public TeleporterManager teleporterManager;
	
	/**
	 * contains all of the downed palyers in the game
	 */
	public DownedPlayerManager downedPlayerManager;
	
	/**
	 * contains all of the downed palyers in the game
	 */
	public SignManager signManager;
	
	/**
	 * Information containing gamemode, and fly mode the player was in before
	 * they joined the game.
	 */
	private PreJoinInformation pInfo = new PreJoinInformation();
	
	/**
	 * Scoreboard used to manage players points
	 */
	public GameScoreboard scoreboard;
	
	/**
	 * Max players is used to check for player count and if not to remove a
	 * player if the game is full.
	 */
	public int maxPlayers;
	
	/**
	 * contains all of the Kits in the game
	 */
	public KitManager kitManager;
	
	public boolean changingRound = false;
	
	/**
	 * Creates a game based off of the parameters and arena configuration file.
	 * 
	 * @param instance
	 *            class / plugin instance
	 * @param name
	 *            of the game
	 */
	public Game(COMZombiesMain instance, String name) {
		plugin = instance;
		arenaName = name;
		
		powerEnabled = plugin.configManager.getConfig("ArenaConfig").getBoolean(name + ".Power", false);
		
		starter = new AutoStart(plugin, this, 60);
		
		spawnManager = new SpawnManager(plugin, this);
		boxManager = new BoxManager(plugin, this);
		barrierManager = new BarrierManager(plugin, this);
		doorManager = new DoorManager(plugin, this);
		perkManager = new PerkManager(plugin);
		teleporterManager = new TeleporterManager(plugin, this);
		downedPlayerManager = new DownedPlayerManager();
		signManager = new SignManager(this);
		kitManager = plugin.kitManager;
		
		scoreboard = new GameScoreboard(this);
		
		spawnManager.loadAllSpawnsToGame();
		boxManager.loadAllBoxesToGame();
		barrierManager.loadAllBarriersToGame();
		doorManager.loadAllDoorsToGame();
		teleporterManager.loadAllTeleportersToGame();
		
		if (plugin.configManager.getConfig("ArenaConfig").getBoolean(arenaName + ".IsForceNight", false)) {
			forceNight();
		}
	}
	
	/**
	 * Gets the guns the player currently has
	 * 
	 * @param player
	 *            to get the guns of
	 * @return GunManager of the players guns
	 */
	public GunManager getPlayersGun(Player player) {
		if (playersGuns.containsKey(player))
			return playersGuns.get(player);
		playersGuns.put(player, new GunManager(plugin, player));
		return playersGuns.get(player);
	}
	
	/**
	 * 
	 * @return the players spawn location on the map
	 */
	public Location getPlayerSpawn() {
		return playerTPLocation;
	}
	
	/**
	 * 
	 * @return the spectators spawn location on the map
	 */
	public Location getSpectateLocation() {
		return spectateLocation;
	}
	
	/**
	 * 
	 * @return the lobby spawn location on the map
	 */
	public Location getLobbyLocation() {
		return lobbyLocation;
	}
	
	/**
	 * 
	 * @return if Double Points is active
	 */
	public boolean isDoublePoints() {
		return doublePoints;
	}
	
	/**
	 * 
	 * @return if Insta Kill is active
	 */
	public boolean isInstaKill() {
		return instaKill;
	}
	
	/**
	 * Turns on or off instakill.
	 * 
	 * @param isInstaKill
	 */
	public void setInstaKill(boolean isInstaKill) {
		instaKill = isInstaKill;
	}
	
	/**
	 * Turns on double points.
	 * 
	 * @param isDoublePoints
	 */
	public void setDoublePoints(boolean isDoublePoints) {
		doublePoints = isDoublePoints;
	}
	
	/**
	 *
	 * @return if the game currently has the power on
	 */
	public boolean isPowered() {
		return power;
	}
	
	/**
	 * Turns off the power for the game
	 */
	public void turnOffPower() {
		power = false;
	}
	
	/**
	 * Turns on the power for the game
	 */
	public void turnOnPower() {
		power = true;
		try {
			for (Player pl : players) {
				Location loc = pl.getLocation();
				loc.getWorld().playSound(loc, Sound.ENTITY_LIGHTNING_THUNDER, 1L, 1L);
			}
		}
		catch (NullPointerException e) { e.printStackTrace(); }
	}
	
	/**
	 * 
	 * @return if power is enabled for the game
	 */
	public boolean containsPower() {
		if (!powerEnabled)
			return false;
		return true;
	}
	
	/**
	 * Resets the blocks to air at the spawn locations
	 */
	public void resetSpawnLocationBlocks() {
		for (int i = 0; i < spawnManager.getPoints().size(); i++) {
			Location loc = spawnManager.getPoints().get(i).getLocation();
			loc.getBlock().setType(Material.AIR);
		}
	}
	
	/**
	 * 
	 * @return if the game has been created fully
	 */
	public boolean isCreated() {
		if (isDisabled) {
			return false;
		}
		if (hasPoints == true && hasWarps == true && !isDisabled) {
			return true;
		}
		return false;
	}
	
	/**
	 * force starts the arena
	 */
	public void forceStart() {
		if (mode == ArenaStatus.INGAME)
			return;

		if (starter != null && starter.forced)
			return;

		for (Player pl : players)
			CommandUtil.sendMessageToPlayer(pl, "Game force started!");

		if (starter != null)
			starter.endTimer();

		starter = new AutoStart(plugin, this, 6);
		starter.startTimer();
		mode = ArenaStatus.STARTING;
		starter.forced = true;
		
	}
	
	/**
	 * starts the game normally
	 * 
	 * @return TRUE if the game starts correctly
	 */
	public boolean startArena() {

		if (mode == ArenaStatus.INGAME)
			return false;

		mode = ArenaStatus.INGAME;

		playerTPLocation.getChunk().load();
		for (Player player : players) {
			player.teleport(playerTPLocation);

			player.setAllowFlight(false);
			player.setFlying(false);

			player.setHealth(20);
			player.setFoodLevel(20);
			player.setLevel(0);
			plugin.pointManager.setPoints(player, 500);
		}

		scoreboard.update();
		if (plugin.config.MultiBox) {
			for (Player player : players)
				player.sendMessage("" + ChatColor.RED + "[Zombies] All mysteryboxes are generating.");

			this.boxManager.loadAllBoxes();
		}
		else {
			this.boxManager.unloadAllBoxes();
			RandomBox b = this.boxManager.getRandomBox();
			if (b != null) {
				this.boxManager.setCurrentBox(b);
				this.boxManager.getCurrentbox().loadBox();
			}
		}
		spawnManager.update();

		
		for (Door door : doorManager.getDoors())
			door.loadSpawns();

			for (LivingEntity entity : getWorld().getLivingEntities()) {
				if (arena.containsBlock(entity.getLocation())) {
					if (entity instanceof Player) {
						continue;
					}
					int times = 0;
					while (!entity.isDead()) {
						entity.damage(Integer.MAX_VALUE);
						if (times > 20) {
							break;
						}
						times++;
					}
				}
			}


		this.waveNumber = 0;
		nextWave();
		signManager.updateGame();
		kitManager.giveOutKits(this);



		return true;
	}
	
	/**
	 * Starts a delayed task
	 * 
	 * @param run
	 *            Runnable that runs the task
	 * @param delayInSeconds
	 *            delay for the task
	 */
	public void scheduleSyncTask(Runnable run, int delayInSeconds) {
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, run, delayInSeconds);
	}
	
	/**
	 * Spawns in the next wave of zombies.
	 */
	public void nextWave() {
		Bukkit.broadcastMessage("wave number: "+waveNumber);
		if (players.size() == 0 && false) { //@change this to function right
			this.endGame();
			return;
		}

		if (!(spawnManager.getZombiesAlive() == 0) || !(spawnManager.getZombiesToSpawn() <= spawnManager.getZombiesSpawned()))
			return;
		if (changingRound)
			return;

		changingRound = true;
		scoreboard.update();

		for (Player pl : players)
			for (Player p : players)
				pl.showPlayer(p);


		if (mode != ArenaStatus.INGAME) {
			waveNumber = 0;
			return;
		}

		waveNumber++;
		if (waveNumber > 1) {

			for (Player pl : players) {
				pl.playSound(pl.getLocation(), Sound.BLOCK_PORTAL_AMBIENT, 1, 1);
				CommandUtil.sendMessageToPlayer(pl, "Round " + waveNumber + " will start is 10 seconds!");
			}

			spawnManager.nextWave();

			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
				Bukkit.broadcastMessage("telling about wave number");

				for (Player pl : players) {
					pl.playSound(pl.getLocation(), Sound.BLOCK_PORTAL_TRAVEL, 1, 1);
					CommandUtil.sendMessageToPlayer(pl, "Round " + waveNumber + "!");
				}

				spawnManager.startWave(waveNumber, players);
				signManager.updateGame();
				changingRound = false;
				Bukkit.broadcastMessage("done telling about wave number");
			}, 200L);

		}
		else {

			for (Player pl : players) {
				CommandUtil.sendMessageToPlayer(pl, "Round " + waveNumber + "!");
			}
			
			spawnManager.nextWave();
			spawnManager.startWave(waveNumber, players);
			signManager.updateGame();
			changingRound = false;

		}
		Bukkit.broadcastMessage("end wave number: " + waveNumber);
	}
	
	/**
	 * Adds a player to the game
	 * 
	 * @param player
	 *            to be added to the game
	 */
	public void addPlayer(Player player) {
		if (mode == ArenaStatus.WAITING || mode == ArenaStatus.STARTING) {
			players.add(player);

			pInfo.addPlayerFL(player, player.isFlying());
			pInfo.addPlayerGM(player, player.getGameMode());
			pInfo.addPlayerLevel(player, player.getLevel());
			pInfo.addPlayerExp(player, player.getExp());
			pInfo.addPlayerInventoryContents(player, player.getInventory().getContents());
			pInfo.addPlayerInventoryArmorContents(player, player.getInventory().getArmorContents());
			pInfo.addPlayerOldLocation(player, player.getLocation());

			scoreboard.addPlayer(player);
			playersGuns.put(player, new GunManager(plugin, player));

			player.setHealth(20);
			player.setFoodLevel(20);
			player.getInventory().clear();
			player.getInventory().setArmorContents(null);
			player.setLevel(0);
			player.setExp(0);
			lobbyLocation.getWorld().loadChunk(lobbyLocation.getChunk());
			player.teleport(lobbyLocation);

			plugin.pointManager.setPoints(player, 500);
			assignPlayerInventory(player);
			player.setGameMode(GameMode.SURVIVAL);
			String gunName = plugin.configManager.getConfig("GunConfig").getString("StartingGun", "M1911");

			waveNumber = 0;
			for (Player pl : players) {
				for (Player p : Bukkit.getOnlinePlayers()) {
					if (!(players.contains(p)))
						pl.hidePlayer(p);
					else
						pl.showPlayer(p);
				}
			}

			GunType gun = plugin.getGun(gunName);
			Game game = plugin.manager.getGame(player);
			if (game != null) {
				GunManager manager = game.getPlayersGun(player);
				Gun gunType = new Gun(gun, player, 1);
				manager.addGun(gunType);
			}

			for (Player pl : players)
				CommandUtil.sendMessageToPlayer(pl, player.getName() + " has joined with " + players.size() + "/" + maxPlayers + "!");

			if (players.size() >= plugin.configManager.getConfig("ArenaConfig").getInt(arenaName + ".minPlayers")) {
				if (starter == null) {
					starter = new AutoStart(plugin, this, plugin.config.arenaStartTime + 1);
					starter.startTimer();
					for (Player pl : players)
						CommandUtil.sendMessageToPlayer(pl, ChatColor.RED + "" + ChatColor.BOLD + "Game starting soon!");
				}
				else {
					if (starter.started)
						return;

					starter = new AutoStart(plugin, this, plugin.config.arenaStartTime + 1);
					starter.startTimer();

					for (Player pl : players)
						CommandUtil.sendMessageToPlayer(pl, "Game starting soon!");
				}
				mode = ArenaStatus.STARTING;
			}
		}
		else
			CommandUtil.sendMessageToPlayer(player, "Something could have went wrong here, COM Zombies has picked this up and will continue without error.");
		signManager.updateGame();
	}
	
	/**
	 * Removes the given player from the game
	 * 
	 * @param player
	 *            to be removed from the game
	 */
	public void playerLeave(Player player, boolean endGame) {
	    players.remove(player);

		if (endGame && players.size() == 0) {
			if (!isDisabled) {
				mode = ArenaStatus.WAITING;
				starter = null;
				players.clear();
				waveNumber = 0;
				plugin.pointManager.clearGamePoints(this);
				endGame();
				for (int i = 0; i < doorManager.getDoors().size(); i++) {
					doorManager.getDoors().get(i).closeDoor();
				}
			}
		}
		if (downedPlayerManager.isPlayerDowned(player)) {
			downedPlayerManager.removeDownedPlayer(player);
		}

		try {
			resetPlayer(player);
			playersGuns.remove(player);
			try {
				player.setFlying(pInfo.getFly(player));
			}
			catch (Exception e) { e.printStackTrace(); }
			plugin.pointManager.playerLeaveGame(player);
		}
		catch (NullPointerException e) {
			plugin.manager.loadAllGames();
		}
		signManager.updateGame();
	}

	private void resetPlayer(Player player) {
		if(!player.getActivePotionEffects().isEmpty())
			for (PotionEffectType t : PotionEffectType.values())
				player.removePotionEffect(t);

		player.removePotionEffect(PotionEffectType.SPEED);
		player.getInventory().clear();
		player.teleport(pInfo.getOldLocation(player));
		player.setHealth(20);
		player.setGameMode(pInfo.getGM(player));
		try {
			player.setFlying(pInfo.getFly(player));
		}
		catch (Exception e) { e.printStackTrace(); }
		player.getInventory().setContents(pInfo.getContents(player));
		player.getInventory().setArmorContents(pInfo.getArmor(player));
		player.setExp(pInfo.getExp(player));
		player.setLevel(pInfo.getLevel(player));
		player.setWalkSpeed(0.2F);
		scoreboard.removePlayer(player);
		player.updateInventory();
		for (Player pl : Bukkit.getOnlinePlayers()) {
			if (!players.contains(pl)) {
				player.showPlayer(pl);
			}
			else {
				pl.hidePlayer(player);
			}
		}
		signManager.updateGame();
	}
	
	/**
	 * Removes a player from the game
	 * 
	 * @param player
	 *            to be removed
	 */
	public void removePlayer(Player player) {
		players.remove(player);
		for (Player pl : players) {
			if (!isDisabled)
				CommandUtil.sendMessageToPlayer(pl, player.getName() + " has left the game! Only " + players.size() + "/" + this.maxPlayers + " players left!");
		}
		if (players.size() == 0) {
			if (!isDisabled) {
				mode = ArenaStatus.WAITING;
				starter = null;
				players.clear();
				waveNumber = 0;
				plugin.pointManager.clearGamePoints(this);
				endGame();
				for (int i = 0; i < doorManager.getDoors().size(); i++) {
					doorManager.getDoors().get(i).closeDoor();
				}
			}
		}
		try {
			playersGuns.remove(player);
			resetPlayer(player);
			try {
				player.setFlying(pInfo.getFly(player));
			}
			catch (Exception e) { e.printStackTrace(); }
			plugin.pointManager.playerLeaveGame(player);
		}
		catch (NullPointerException e) {
			plugin.manager.loadAllGames();
		}
		signManager.updateGame();
	}
	
	/**
	 * Sets the spectator warp location
	 * 
	 * @param p
	 *            player the set the location
	 * @param loc
	 *            location where the warp will be
	 * @return if the spawn was set or not
	 */
	public boolean setSpectateLocation(Player p, Location loc) {
		if (min == null || max == null || playerTPLocation == null || lobbyLocation == null) {
			CommandUtil.sendMessageToPlayer(p, "Set the spectator location last!");
			return false;
		}
		spectateLocation = loc;
		saveLocationsInConfig(p);
		hasWarps = true;
		mode = ArenaStatus.WAITING;
		return true;
	}
	
	/**
	 * Causes the game to always be at night time.
	 */
	public void forceNight() {
		plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> getWorld().setTime(14000L), 5L, 1200L);
	}
	
	/**
	 * Gets the world where the map is located
	 * 
	 * @return
	 */
	public World getWorld() {
		return plugin.getServer().getWorld(plugin.configManager.getConfig("ArenaConfig").getString(arenaName + ".Location.world"));
	}
	
	/**
	 * Sets the lobby spawn location
	 * 
	 * @param player
	 *            that set the spawn
	 * @param loc
	 *            location where the spawn wll be
	 * @return if the spawn was set or not
	 */
	public boolean setLobbySpawn(Player player, Location loc) {
		if (min == null || max == null) {
			CommandUtil.sendMessageToPlayer(player, "Set arena points first!");
			return false;
		}
		lobbyLocation = loc;
		return true;
	}
	
	/**
	 * Sets the first point in the arena
	 * 
	 * @param p
	 *            player that set the point
	 * @param loc
	 *            location that
	 * @return if the point was set or not
	 */
	public boolean addPointOne(Player p, Location loc) {
		min = loc;
		saveLocationsInConfig(p);
		worldName = loc.getWorld().getName();
		return true;
	}
	
	/**
	 * Sets the Second point in the arena
	 * 
	 * @param p
	 *            player that set the point
	 * @param loc
	 *            location that
	 * @return if the point was set or not
	 */
	public boolean addPointTwo(Player p, Location loc) {
		if (min == null) {
			CommandUtil.sendMessageToPlayer(p, "Type p1 before p2!");
			return false;
		}
		World world = min.getWorld();
		max = loc;
		arena = new Arena(min, max, world);
		saveLocationsInConfig(p);
		hasPoints = true;
		return true;
	}
	
	/**
	 * Disables the game
	 */
	public void setDisabled() {
		endGame();
		isDisabled = true;
		mode = ArenaStatus.DISABLED;
	}
	
	/**
	 * Enables the game
	 */
	public void setEnabled() {
		resetSpawnLocationBlocks();
		isDisabled = false;
		if (mode == ArenaStatus.INGAME) {
			return;
		}
		mode = ArenaStatus.WAITING;
		signManager.updateGame();
	}
	
	/**
	 * Ends the game
	 */
	public void endGame() {
		this.mode = ArenaStatus.WAITING;
		for (Player p : players) {
			double points = waveNumber;
			plugin.vault.addMoney(p.getName(), points);
			CommandUtil.sendMessageToPlayer(p, "You got " + points + " for getting to round: " + waveNumber + "!");
			scoreboard.removePlayer(p);
			playerLeave(p, true);
		}
		spawnManager.killAll(false);
		spawnManager.reset();
		for (Door door : doorManager.getDoors()) {
			door.closeDoor();
		}
		perkManager.clearPerks();
		for (DownedPlayer pl : downedPlayerManager.getDownedPlayers()) {
			pl.setPlayerDown(false);
		}
		downedPlayerManager.clearDownedPlayers();
		turnOffPower();
		boxManager.loadAllBoxes();
		barrierManager.unloadAllBarriers();
		players.clear();
		this.starter = null;
		scoreboard = new GameScoreboard(this);
		instaKill = false;
		doublePoints = false;
		waveNumber = 0;
		clearArena();
		clearArenaItems();
		for (Player pl : Bukkit.getOnlinePlayers()) {
			for (Player p : Bukkit.getOnlinePlayers()) {
				p.showPlayer(pl);
				pl.showPlayer(p);
			}
		}
		signManager.updateGame();
	}
	
	/**
	 * Sets the players warp location in game.
	 * 
	 * @param p
	 *            player that set the point
	 * @param loc
	 *            location where the point will be set
	 * @return if the point was set or not
	 */
	public boolean setPlayerTPLocation(Player p, Location loc) {
		if (min == null || max == null) {
			CommandUtil.sendMessageToPlayer(p, "Set the player warp after spawns are set!");
			return false;
		}
		playerTPLocation = loc;
		saveLocationsInConfig(p);
		return true;
	}
	
	/**
	 * Sets the name of the game
	 * 
	 * @param name
	 *            Name of the arena to be set to
	 */
	public void setName(String name) {
		arenaName = name;
	}
	
	public static enum ArenaStatus {
		DISABLED, STARTING, WAITING, INGAME;
	}
	
	/**
	 * Saves all locations to the config
	 * 
	 * @param player
	 *            that saved the locations
	 */
	public void saveLocationsInConfig(Player player) {
		CustomConfig conf = plugin.configManager.getConfig("ArenaConfig");
		
		if (min.getWorld().getName() != null) {
			conf.set(arenaName + ".Location.world", min.getWorld().getName());
		}
		try {
			conf.set(arenaName + ".Location.P1.x", min.getBlockX());
			conf.set(arenaName + ".Location.P1.y", min.getBlockY());
			conf.set(arenaName + ".Location.P1.z", min.getBlockZ());
			conf.set(arenaName + ".Location.P2.x", max.getBlockX());
			conf.set(arenaName + ".Location.P2.y", max.getBlockY());
			conf.set(arenaName + ".Location.P2.z", max.getBlockZ());
			conf.set(arenaName + ".PlayerSpawn.x", playerTPLocation.getBlockX());
			conf.set(arenaName + ".PlayerSpawn.y", playerTPLocation.getBlockY());
			conf.set(arenaName + ".PlayerSpawn.z", playerTPLocation.getBlockZ());
			conf.set(arenaName + ".PlayerSpawn.pitch", playerTPLocation.getPitch());
			conf.set(arenaName + ".PlayerSpawn.yaw", playerTPLocation.getYaw());
			conf.set(arenaName + ".SpectatorSpawn.x", spectateLocation.getBlockX());
			conf.set(arenaName + ".SpectatorSpawn.y", spectateLocation.getBlockY());
			conf.set(arenaName + ".SpectatorSpawn.z", spectateLocation.getBlockZ());
			conf.set(arenaName + ".SpectatorSpawn.pitch", spectateLocation.getPitch());
			conf.set(arenaName + ".SpectatorSpawn.yaw", spectateLocation.getYaw());
			conf.set(arenaName + ".LobbySpawn.x", lobbyLocation.getBlockX());
			conf.set(arenaName + ".LobbySpawn.y", lobbyLocation.getBlockY());
			conf.set(arenaName + ".LobbySpawn.z", lobbyLocation.getBlockZ());
			conf.set(arenaName + ".LobbySpawn.pitch", lobbyLocation.getPitch());
			conf.set(arenaName + ".LobbySpawn.yaw", lobbyLocation.getYaw());
			conf.saveConfig();
			conf.reloadConfig();
			
			CommandUtil.sendMessageToPlayer(player, "Arena " + arenaName + " setup!");
			plugin.isArenaSetup.remove(player);
			hasWarps = true;
		}
		catch (Exception e) {
			return;
		}
	}
	
	/**
	 * Sets up the arena when the server loads
	 */
	public void enable() {
		CustomConfig conf = plugin.configManager.getConfig("ArenaConfig");
		conf.reloadConfig();
		if (conf.getString(arenaName + ".Location.world") == null)
			worldName = arena.getWorld();
		else
			worldName = conf.getString(arenaName + ".Location.world");
		int x1 = conf.getInt(arenaName + ".Location.P1.x");
		int y1 = conf.getInt(arenaName + ".Location.P1.y");
		int z1 = conf.getInt(arenaName + ".Location.P1.z");
		int x2 = conf.getInt(arenaName + ".Location.P2.x");
		int y2 = conf.getInt(arenaName + ".Location.P2.y");
		int z2 = conf.getInt(arenaName + ".Location.P2.z");
		int x3 = conf.getInt(arenaName + ".PlayerSpawn.x");
		int y3 = conf.getInt(arenaName + ".PlayerSpawn.y");
		int z3 = conf.getInt(arenaName + ".PlayerSpawn.z");
		int pitch3 = conf.getInt(arenaName + ".PlayerSpawn.pitch");
		int yaw3 = conf.getInt(arenaName + ".PlayerSpawn.yaw");
		int x4 = conf.getInt(arenaName + ".SpectatorSpawn.x");
		int y4 = conf.getInt(arenaName + ".SpectatorSpawn.y");
		int z4 = conf.getInt(arenaName + ".SpectatorSpawn.z");
		int pitch4 = conf.getInt(arenaName + ".SpectatorSpawn.pitch");
		int yaw4 = conf.getInt(arenaName + ".SpectatorSpawn.yaw");
		int x5 = conf.getInt(arenaName + ".LobbySpawn.x");
		int y5 = conf.getInt(arenaName + ".LobbySpawn.y");
		int z5 = conf.getInt(arenaName + ".LobbySpawn.z");
		int pitch5 = conf.getInt(arenaName + ".LobbySpawn.pitch");
		int yaw5 = conf.getInt(arenaName + ".LobbySpawn.yaw");
		maxPlayers = conf.getInt(arenaName + ".maxPlayers", 8);
		Location minLoc = new Location(Bukkit.getWorld(worldName), x1, y1, z1);
		Location maxLoc = new Location(Bukkit.getWorld(worldName), x2, y2, z2);
		Location pwarp = new Location(Bukkit.getWorld(worldName), x3, y3, z3, yaw3, pitch3);
		Location swarp = new Location(Bukkit.getWorld(worldName), x4, y4, z4, yaw4, pitch4);
		Location lwarp = new Location(Bukkit.getWorld(worldName), x5, y5, z5, yaw5, pitch5);
		min = minLoc;
		max = maxLoc;
		playerTPLocation = pwarp.add(0.5, 0, 0.5);
		spectateLocation = swarp.add(0.5, 0, 0.5);
		lobbyLocation = lwarp.add(0.5, 0, 0.5);
		arena = new Arena(min, max, Bukkit.getWorld(worldName));
		spawnManager.loadAllSpawnsToGame();
		mode = ArenaStatus.WAITING;
	}
	
	/**
	 * Sets up the arena when the server loads
	 */
	public void setup() {
		
		// Sets up ArenaConfig
		CustomConfig conf = plugin.configManager.getConfig("ArenaConfig");
		// Adding ArenaName
		String loc = arenaName;
		conf.set(loc + ".Power", false);
		// Adds Location
		String locL = loc + ".Location";
		conf.set(locL, null);
		// adds arenas world
		conf.set(locL + ".World", null);
		// Adds p1
		String locP1 = locL + ".P1";
		conf.set(locP1, null);
		// Adds p2
		String locP2 = locL + ".P2";
		conf.set(locP2, null);
		// adds point1x
		conf.set(locP1 + ".x", null);
		// adds point1y
		conf.set(locP1 + ".y", null);
		// adds point1z
		conf.set(locP1 + ".z", null);
		// adds point2x
		conf.set(locP2 + ".x", null);
		// adds point2y
		conf.set(locP2 + ".y", null);
		// adds point2z
		conf.set(locP2 + ".z", null);
		// adds arenas dificulty
		// plugin.files.getArenasFile().addDefault(, "EASY");
		// adds the playerwarp main
		String locPS = loc + ".PlayerSpawn";
		conf.set(locPS, null);
		// adds the playerwarpsx
		conf.set(locPS + ".x", null);
		// adds the playerwarpsy
		conf.set(locPS + ".y", null);
		// adds the playerwarpsz
		conf.set(locPS + ".z", null);
		
		String locLB = loc + ".LobbySpawn";
		// adds the lobby LB spawn
		conf.set(locLB, null);
		// adds the lobby LB spawn for the X coord
		conf.set(locLB + ".x", null);
		// adds the lobby LB spawn for the Y coord
		conf.set(locLB + ".y", null);
		// adds the lobby LB spawn for the Z coord
		conf.set(locLB + ".z", null);
		// adds specatorMain
		String locSS = loc + ".SpectatorSpawn";
		conf.set(locSS, 0);
		// adds specatorx
		conf.set(locSS + ".x", 0);
		// adds specatory
		conf.set(locSS + ".y", 0);
		// adds specatorz
		conf.set(locSS + ".z", 0);
		// adds ZombieSpawn Main
		String locZS = loc + ".ZombieSpawns";
		conf.set(locZS, null);
		// adds PerkMachine main
		String locPMS = locL + ".PerkMachines";
		conf.set(locPMS, null);
		// adds PerkMachine main
		String locMBL = locL + ".MysteryBoxLocations";
		conf.set(locMBL, null);
		// adds Door Locations main
		String locD = loc + ".Doors";
		conf.set(locD, null);
		
		String isForceNight = loc + ".IsForceNight";
		conf.set(isForceNight, false);
		
		String minToStart = loc + ".minPlayers";
		conf.set(minToStart, 1);
		
		String spawnDelay = loc + ".ZombieSpawnDelay";
		conf.set(spawnDelay, 15);
		// Setup starting items data, default vaules added.
		ArrayList<String> startItems = new ArrayList<String>();
		conf.set(loc + ".StartingItems", startItems);
		conf.set(loc, null);
		conf.set(loc + ".maxPlayers", 8);
		// Saves and reloads Arenaconfig
		conf.saveConfig();
	}
	
	/**
	 * Takes a spawn point out of the config file.
	 */
	public void removeFromConfig() {
		CustomConfig conf = plugin.configManager.getConfig("ArenaConfig");
		try {
			conf.set(arenaName, null);
			conf.saveConfig();
		}
		catch (Exception e) {
			return;
		}
	}
	
	/**
	 * gets the name of the game
	 * 
	 * @return
	 */
	public String getName() {
		return arenaName;
	}
	
	/**
	 * Sets up the players inventory in game
	 * 
	 * @param slot
	 *            of the item
	 * @param item
	 *            to be set up
	 * @return the item after set up
	 */
	private ItemStack setItemMeta(int slot, ItemStack item) {
		ItemMeta data = item.getItemMeta();
		List<String> lore = new ArrayList<String>();
		switch (slot) {
			case 27:
				data.setDisplayName("Knife slot");
				lore.add("Holds players knife");
				lore.add("Knife only works within 2 blocks!");
				break;
			case 28:
				data.setDisplayName("Gun Slot 1");
				lore.add("Holds 1 Gun");
				break;
			case 29:
				data.setDisplayName("Gun Slot 2");
				lore.add("Holds 1 gun");
				break;
			case 30:
				data.setDisplayName("Gun Slot 3");
				lore.add("Holds 1 Gun");
				lore.add("Requires MuleKick to work!");
				break;
			case 31:
				data.setDisplayName("Perk Slot 1");
				lore.add("Holds 1 Perk");
				break;
			case 32:
				data.setDisplayName("Perk Slot 2");
				lore.add("Holds 1 Perk");
				break;
			case 33:
				data.setDisplayName("Perk Slot 3");
				lore.add("Holds 1 Perk");
				break;
			case 34:
				data.setDisplayName("Perk Slot 4");
				lore.add("Holds 1 Perk");
				break;
			case 35:
				data.setDisplayName("Grenade Slot");
				lore.add("");
				break;
		}
		data.setLore(lore);
		item.setItemMeta(data);
		return item;
	}

	public void assignPlayerInventory(Player player) {
		player.getInventory().clear();
		ItemStack helmet = new ItemStack(Material.LEATHER_HELMET, 1);
		ItemStack chestPlate = new ItemStack(Material.LEATHER_CHESTPLATE, 1);
		ItemStack pants = new ItemStack(Material.LEATHER_LEGGINGS, 1);
		ItemStack boots = new ItemStack(Material.LEATHER_BOOTS, 1);
		ItemStack knife = new ItemStack(Material.IRON_SWORD, 1);
		ItemMeta kMeta = knife.getItemMeta();
		kMeta.setDisplayName(ChatColor.RED + "Knife");
		knife.setItemMeta(kMeta);
		ItemStack ib = new ItemStack(Material.THIN_GLASS, 1);
		player.getInventory().setHelmet(helmet);
		player.getInventory().setChestplate(chestPlate);
		player.getInventory().setLeggings(pants);
		player.getInventory().setBoots(boots);
		player.getInventory().setItem(0, knife);
		// player.getInventory().setItem(8, new ItemStack(Material.MAGMA_CREAM,
		// 4));
		player.getInventory().setItem(27, setItemMeta(27, ib));
		player.getInventory().setItem(28, setItemMeta(28, ib));
		player.getInventory().setItem(29, setItemMeta(29, ib));
		player.getInventory().setItem(30, setItemMeta(30, ib));
		player.getInventory().setItem(31, setItemMeta(31, ib));
		player.getInventory().setItem(32, setItemMeta(32, ib));
		player.getInventory().setItem(33, setItemMeta(33, ib));
		player.getInventory().setItem(34, setItemMeta(34, ib));
		player.getInventory().setItem(35, setItemMeta(35, ib));
		player.updateInventory();
	}
	
	/**
	 * Clears the arena
	 */
	public void clearArena() {
		if (this.getWorld() == null)
			return;
		for (Entity entity : this.getWorld().getEntities()) {
			if (arena.containsBlock(entity.getLocation())) {
				if (!(entity instanceof Player)) {
					entity.setTicksLived(Integer.MAX_VALUE);
					entity.remove();
				}
			}
		}
	}
	
	/**
	 * Clears items out of the arena
	 */
	public void clearArenaItems() {
		if (this.getWorld() == null)
			return;
		List<Entity> entList = getWorld().getEntities();// get all entities in
														// the world
		
		for (Entity current : entList) {// loop through the list
			if (current instanceof Item) {// make sure we are only deleting what we want to delete
				current.remove();// remove it
			}
			if (current instanceof Zombie) {
				current.remove();
			}
		}
	}
	
	public void setFireSale(boolean b) {
		isFireSale = b;
	}
	
	public boolean isFireSale() {
		return isFireSale;
	}
	
	public void zombieKilled(Player player) {
		CustomConfig conf = plugin.configManager.getConfig("kills");
		Leaderboards lb = plugin.leaderboards;
		if (conf.contains("kills." + player.getName())) {
			int kills = conf.getInt("kills." + player.getName());
			kills++;
			conf.set("kills." + player.getName(), kills);
			PlayerStats stat = lb.getPlayerStatFromPlayer(player);
			if (stat == null) {
				PlayerStats newstat = new PlayerStats(player.getName(), 1);
				lb.addPlayerStats(newstat);
			}
			else {
				stat.setKills(stat.getKills() + 1);
			}
		}
		else {
			conf.set("kills." + player.getName(), 1);
			PlayerStats stat = new PlayerStats(player.getName(), 1);
			lb.addPlayerStats(stat);
			
		}
		if (plugin.vault != null) {
			try {
				if (!plugin.vault.hasAccount(player.getName()))
					plugin.vault.newAccount(player.getName());
				plugin.vault.addMoney(player.getName(), (double) plugin.config.KillMoney);
			}
			catch (NullPointerException e) { e.printStackTrace(); }
		}
		conf.saveConfig();
	}
	
	public void updateBarrierDamage(int damage, Block block) {
		for (Player player : this.players) {
			PacketPlayOutBlockBreakAnimation packet = new PacketPlayOutBlockBreakAnimation(0, new BlockPosition(block.getX(), block.getY(), block.getZ()), damage);
			int dimension = ((CraftWorld) player.getWorld()).getHandle().dimension;
			((CraftServer) player.getServer()).getHandle().sendPacketNearby(((CraftPlayer)player).getHandle(), block.getX(), block.getY(), block.getZ(), 120, dimension, packet);
		}
	}
}
