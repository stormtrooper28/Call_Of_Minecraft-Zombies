package com.zombies.config;

import org.bukkit.Bukkit;
import org.bukkit.Material;

import com.zombies.COMZombiesMain;
import com.zombies.guns.GunType;
import com.zombies.guns.GunTypeEnum;
import com.zombies.leaderboards.PlayerStats;

public class ConfigSetup {
	/**
	 * Field used to get configuration.
	 */
	COMZombiesMain plugin;
	/**
	 * Weather or not to spawn multiple MysteryBoxes
	 */
	public boolean MultiBox;
	
	/**
	 * Default points when a player shoots a zombie.
	 */
	public int pointsOnHit;
	/**
	 * Default points when a player kills a zombie.
	 */
	public int pointsOnKill;
	/**
	 * Time it takes to revive a player.
	 */
	public int reviveTimer;
	/**
	 * Maximum wave.
	 */
	public int maxWave;
	/**
	 * Interval between round change and zombie spawn.
	 */
	public int waveSpawnInterval;
	/**
	 * Max zombies that can be contained in an arena or game.
	 */
	public int maxZombies;
	/**
	 * Does the plugin nag the player a message when they login?
	 */
	public String configVersion;
	/**
	 * Time it takes to reload a gun.
	 */
	public int reloadTime;
	/**
	 * Time double points is active for.
	 */
	public int doublePointsTimer;
	/**
	 * Time insta kill is active for.
	 */
	public int instaKillTimer;
	/**
	 * Maximum range the Reviver can remove from whoever (s)he is reviving.
	 */
	public int reviveRange;
	/**
	 * Maximum range melees are allowed for. Values over 6 -> 6 (Maximum vanilla reach) for now.
	 */
	public float meleeRange;
	/**
	 * Time Fire salse is active for.
	 */
	public int fireSaleTimer;
	/**
	 * True for all if enabled.
	 */
	public boolean maxAmmo;
	public boolean instaKill;
	public boolean carpenter;
	public boolean nuke;
	public boolean doublePoints;
	public boolean fireSale;
	/**
	 * Time it takes for an arena to start.
	 */
	public int arenaStartTime;
	/**
	 * Max possible perks a player can obtain.
	 */
	public int maxPerks;
	
	public double KillMoney;
	
	//public int PistolMaterial;
	
	/**
	 * Instantiates the plugin field as the main class and assigns correct
	 * values to every other field using setup().
	 * 
	 * @param instance
	 *            class
	 */
	public ConfigSetup(COMZombiesMain instance) {
		plugin = instance;
		Setup();
	}
	
	/**
	 * Main method to assign values to every field.
	 */
	public void Setup() {
		CustomConfig conf = plugin.configManager.getConfig("GunConfig");
		MultiBox = plugin.getConfig().getBoolean("config.gameSettings.MultipleMysteryBoxes");
		doublePointsTimer = plugin.getConfig().getInt("config.gameSettings.doublePointsTimer");
		instaKillTimer = plugin.getConfig().getInt("config.gameSettings.instaKillTimer");
		fireSaleTimer = plugin.getConfig().getInt("config.gameSettings.fireSaleTimer");
		maxZombies = (int) plugin.getConfig().getDouble("config.gameSettings.maxZombies");
		waveSpawnInterval = (int) plugin.getConfig().getDouble("config.gameSettings.waveSpawnInterval");
		pointsOnHit = plugin.getConfig().getInt("config.gameSettings.defaultPointsOnHit");
		pointsOnKill = plugin.getConfig().getInt("config.gameSettings.defaultPointsOnKill");
		maxWave = plugin.getConfig().getInt("config.gameSettings.maxWave");
		reviveTimer = plugin.getConfig().getInt("config.ReviveSettings.ReviveTimer");
		reviveRange = plugin.getConfig().getInt("config.ReviveSettings.ReviveRange");
		if (reviveRange > 6)
			reviveRange = 6;
		meleeRange = (float) plugin.getConfig().getDouble("config.ReviveSettings.MeleeRange");
		configVersion = plugin.getConfig().getString("vID");
		reloadTime = plugin.getConfig().getInt("config.gameSettings.reloadTime");
		if (plugin.possibleGuns.size() != 0) {
			plugin.possibleGuns.clear();
		}
		
		for (String group : conf.getConfigurationSection("Guns").getKeys(false)) {
			for (String gun : conf.getConfigurationSection("Guns." + group).getKeys(false)) {
				//String item = conf.getString("Guns." + group + "." + gun + ".Item", GunTypeEnum.getGun(group).getMaterial().name());
				//Material gunItem = (item == null || Material.getMaterial(item) == null) ? GunTypeEnum.getGun(group).getMaterial() : Material.getMaterial(item);
				String ammo = conf.getString("Guns." + group + "." + gun + ".Ammo");
				String packAmmo = conf.getString("Guns." + group + "." + gun + ".PackAPunch.Ammo");
				int clipAmmo = Integer.parseInt(ammo.substring(0, ammo.indexOf("/")));
				int totalAmmo = Integer.parseInt(ammo.substring(ammo.indexOf("/") + 1));
				int damage = conf.getInt("Guns." + group + "." + gun + ".Damage");
				int fireDelay = conf.getInt("Guns." + group + "." + gun + ".FireDelay", 5);
				double speed = conf.getDouble("Guns." + group + "." + gun + ".ProjectileSpeed", 3);
				int pClip = Integer.parseInt(packAmmo.substring(0, packAmmo.indexOf("/")));
				int pTotal = Integer.parseInt(packAmmo.substring(packAmmo.indexOf("/") + 1));
				int packDamage = conf.getInt("Guns." + group + "." + gun + ".PackAPunch.Damage");
				String packGunName = conf.getString("Guns." + group + "." + gun + ".PackAPunch.Name");

				plugin.possibleGuns.add(new GunType(GunTypeEnum.getGun(group), gun, GunTypeEnum.getGun(group).getMaterial(), damage, fireDelay, speed, clipAmmo, totalAmmo, pClip, pTotal, packDamage, packGunName));
			}
		}
		maxAmmo = plugin.getConfig().getBoolean("config.Perks.MaxAmmo");
		instaKill = plugin.getConfig().getBoolean("config.Perks.InstaKill");
		carpenter = plugin.getConfig().getBoolean("config.Perks.Carpenter");
		nuke = plugin.getConfig().getBoolean("config.Perks.Nuke");
		if(!MultiBox)
			fireSale = plugin.getConfig().getBoolean("config.Perks.FireSale");
		else
			fireSale = false;
		doublePoints = plugin.getConfig().getBoolean("config.Perks.DoublePoints");
		arenaStartTime = plugin.getConfig().getInt("config.gameSettings.arenaStartTime");
		maxPerks = plugin.getConfig().getInt("config.Perks.maxPerks");
		KillMoney = plugin.getConfig().getDouble("config.Economy.MoneyPerKill");
		//PistolMaterial = plugin.getConfig().getInt("config.Guns.PistolMaterial");

        if(plugin.configManager.getConfig("kills").contains("kills")) {
			CustomConfig killsconf = plugin.configManager.getConfig("kills");
			for (String a : killsconf.getConfigurationSection("kills").getKeys(true)) {
				PlayerStats stat = new PlayerStats(a, killsconf.getInt("kills." + a));
				plugin.leaderboards.addPlayerStats(stat);
			}
		}
	}
}