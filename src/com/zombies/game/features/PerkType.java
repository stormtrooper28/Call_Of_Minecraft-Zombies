package com.zombies.game.features;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.zombies.COMZombiesMain;

public enum PerkType {
	JUGGERNOG("Juggernog", Material.CHAINMAIL_CHESTPLATE),
	SPEED_COLA("Speed Cola", Material.FEATHER), // half reload time
	QUICK_REVIVE("Quick Revive", Material.SPECKLED_MELON), // revived in half time (unless sp, then extra life)
	STAMIN_UP("Stamina Up", Material.SUGAR),
	PHD_FLOPPER("PHD Flopper", Material.FIREBALL),
	ELECTRIC_C("Electric Cherry", Material.NETHER_STAR),
	MULE_KICK("Mule Kick", Material.STRING),
	DOUBLE_TAP("Double Tap", Material.LEVER), //double bullets per shot
	@Deprecated DEADSHOT_DAIQ("Deadshot Daiq", Material.BOW) /*autoaim||aimbot*/,
	@Deprecated TOMBSTONE_SODA("Tombstone Soda", Material.WEB) /*think gravestone mod*/,
	@Deprecated AMMO_0_MATIC("Ammo-O-Matic", Material.PISTON_BASE) /*Max ammo at end of each round*/,
	@Deprecated WHOS_WHO("Who's Who", Material.MONSTER_EGG) /*onDown: create a clone that can do everything except take damage, even revive yourself. Your downed self can still die*/,
	@Deprecated VULTURE_AID("Vulture Aid", Material.BUCKET) /*See important stuff with hud && zombies can drop extra ammo and points*/,
	@Deprecated WIDOWS_WINE("Widow's Wine", Material.LINGERING_POTION) /*grenades become special that trap nearby zombies in webs && wraps melee attacker in web*/,
	@Deprecated SLAPPY_TAFFY("Slappy Taffy", Material.SLIME_BALL) /*Increased Melee damage & knockback*/,
	@Deprecated TRAILBLAZERS("Trailblazers", Material.BLAZE_POWDER) /*Trail of fire behind player (when sliding... so running?)*/;

	private String toString;
	private Material type;

	PerkType(String toString, Material type) {
		this.toString = toString;
		this.type = type;
	}

	@Override
	public String toString() {
		return toString;
	}

	public void initialEffect(COMZombiesMain plugin, final Player player, int slot) {
		final World world = player.getLocation().getWorld();

		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(player.getLocation(), Sound.ENTITY_GENERIC_DRINK, 1L, 1L), 5L);
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(player.getLocation(), Sound.ENTITY_GENERIC_DRINK, 1L, 1L), 10L);
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playEffect(player.getLocation(), Effect.POTION_BREAK, 1), 20L);

		ItemStack item = new ItemStack(type);
		ItemMeta im = item.getItemMeta();
		im.setDisplayName(toString);
		item.setItemMeta(im);

		player.getInventory().setItem(slot, item);
		player.updateInventory();
	}

	public static PerkType getPerkType(String name) {
		name = ChatColor.stripColor(name);

		for (PerkType pt : values())
			if (pt.toString().equalsIgnoreCase(name) || pt.name().equalsIgnoreCase(name)) { return pt; }

		return null;
	}

	public static void noPower(Player player) {
		World world = player.getLocation().getWorld();
		world.playSound(player.getLocation(), Sound.ENTITY_GHAST_SCREAM, 1L, 1L);
	}

	public static ItemStack getPerkItem(PerkType pt) {
		return new ItemStack(pt.type);
	}
}