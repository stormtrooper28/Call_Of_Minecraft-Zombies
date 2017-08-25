package com.zombies.listeners;

import java.util.ArrayList;

import com.zombies.COMZombiesMain;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import com.zombies.game.Game;
import com.zombies.game.Game.ArenaStatus;
import com.zombies.game.features.Barrier;
import com.zombies.guns.Gun;
import com.zombies.guns.GunManager;

public class OnZombiePerkDrop implements Listener {

	COMZombiesMain plugin;

	private static ArrayList<ItemStack> currentPerks = new ArrayList<ItemStack>();
	private ArrayList<Entity> droppedItems = new ArrayList<Entity>();

	public OnZombiePerkDrop(COMZombiesMain instance) {
		plugin = instance;
	}

	public void perkDrop(Entity zombie, Entity ent) {
		if (!(zombie instanceof Zombie)) { return; }
		if (!(ent instanceof Player)) { return; }
		Player p = (Player) ent;
		int chance = (int) (Math.random() * 100);
		if (chance <= plugin.getConfig().getInt("config.Perks.PercentDropchance")) {
			Game game;
			int randomPerk = (int) (Math.random() * 6);
			try {
				game = plugin.manager.getGame(zombie.getLocation());
				if (!(game.mode == ArenaStatus.INGAME)) { return; }
				if (!(plugin.manager.isPlayerInGame(p))) { return; }
			} catch (NullPointerException e) {
				return;
			}
			if (randomPerk == 0) {
				if (!plugin.config.maxAmmo)perkDrop(zombie, p);
				else {
					ItemStack drop = new ItemStack(Material.CHEST, 1);
					dropItem((Zombie) zombie, drop);
				}
			}
			if (randomPerk == 1) {
				if (!plugin.config.instaKill) perkDrop(zombie, p);
				else {
					ItemStack drop = new ItemStack(Material.DIAMOND_SWORD, 1);
					dropItem((Zombie) zombie, drop);
					game.perkManager.setCurrentPerkDrops(currentPerks);
				}
			}
			if (randomPerk == 2) {
				if (!plugin.config.carpenter) perkDrop(zombie, p);
				else {
					ItemStack drop = new ItemStack(Material.DIAMOND_PICKAXE, 1);
					dropItem((Zombie) zombie, drop);
				}
			}
			if (randomPerk == 3) {
				if (!plugin.config.nuke) perkDrop(zombie, p);
				else {
					ItemStack drop = new ItemStack(Material.TNT, 1);
					dropItem((Zombie) zombie, drop);
				}
			}
			if (randomPerk == 4) {
				if (!plugin.config.doublePoints) perkDrop(zombie, p);
				else {
					ItemStack drop = new ItemStack(Material.EXP_BOTTLE, 1);
					dropItem((Zombie) zombie, drop);
				}
			}
			if (randomPerk == 5) {

				if (!plugin.config.fireSale) perkDrop(zombie, p);
				else {
					ItemStack drop = new ItemStack(Material.GOLD_INGOT, 1);
					dropItem((Zombie) zombie, drop);
				}
			}
			game.perkManager.setCurrentPerkDrops(currentPerks);
		}
	}

	/**
	 * Drops a given itemstack on the ground at the given location.
	 * 
	 * @param zombie
	 *            to get location from
	 * @param stack
	 *            to drop on the ground
	 */
	private void dropItem(Zombie zombie, ItemStack stack) {
		Location loc = zombie.getLocation();
		Entity droppedItem = loc.getWorld().dropItem(loc, stack);
		droppedItems.add(droppedItem);
		currentPerks.add(stack);
		scheduleRemove(droppedItem);
	}

	private void scheduleRemove(final Entity ent) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            ent.remove();
            droppedItems.remove(ent);
        }, 20 * 30);
	}

	@EventHandler
	private void onPerkPickup(EntityPickupItemEvent e) {
	    if(!(e.getEntity() instanceof Player)) return;
	    Player p = (Player) e.getEntity();
		final Item eItem = e.getItem();
		if (plugin.manager.isPlayerInGame(p)) {
			final Game game = plugin.manager.getGame(p);
			Material MaxAmmo = Material.CHEST;
			Material InstaKill = Material.DIAMOND_SWORD;
			Material Carpenter = Material.DIAMOND_PICKAXE;
			Material Nuke = Material.TNT;
			Material DoublePoints = Material.EXP_BOTTLE;
			Material fireSale = Material.GOLD_INGOT;

			if (currentPerks.size() == 0) {
				e.getItem().remove();
				e.setCancelled(true);
				return;
			}
			if (!currentPerks.contains(e.getItem().getItemStack())) {
				e.getItem().remove();
				e.setCancelled(true);
				return;
			}
			ItemStack item = e.getItem().getItemStack();
			if (e.getItem().getItemStack().getType() == MaxAmmo) {
				p.getInventory().remove(item);
				notifyAll(game, "Max ammo!");
				currentPerks.remove(e.getItem().getItemStack());
				for (Player pl : game.players) {
					GunManager manager = game.getPlayersGun(pl);
					for (Gun gun : manager.getGuns()) {
						gun.maxAmmo();
					}
				}
				e.getItem().remove();
				e.setCancelled(true);
				return;
			}
			if (e.getItem().getItemStack().getType() == InstaKill) {
				currentPerks.remove(e.getItem().getItemStack());
				p.getInventory().remove(item);
				game.setInstaKill(true);
				notifyAll(game, "Insta-kill!");
				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					@Override
					public void run() {
						game.setInstaKill(false);
					}

				}, plugin.config.instaKillTimer * 20);
				e.getItem().remove();
				e.setCancelled(true);
				return;
			}
			if (e.getItem().getItemStack().getType() == Carpenter) {
				currentPerks.remove(e.getItem().getItemStack());
				p.getInventory().remove(item);
				notifyAll(game, "Carpenter!");
				for(Barrier barrier : game.barrierManager.getBarriers()) {
					barrier.repairFull();
				}
				e.getItem().remove();
				e.setCancelled(true);
				return;
			}
			if (e.getItem().getItemStack().getType() == Nuke) {
				currentPerks.remove(e.getItem().getItemStack());
				p.getInventory().remove(item);
				notifyAll(game, "Nuke!");
				for (Player pl : game.players) {
					if (game.isDoublePoints()) plugin.pointManager.addPoints(p, 800);
					else plugin.pointManager.addPoints(p, 400);
					plugin.pointManager.notifyPlayer(pl);
				}
				game.spawnManager.nuke();
				e.setCancelled(true);
				e.getItem().remove();
				return;
			}
			if (e.getItem().getItemStack().getType() == DoublePoints) {
				currentPerks.remove(e.getItem().getItemStack());
				p.getInventory().remove(item);
				game.setDoublePoints(true);
				notifyAll(game, "Double points!");
				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

					@Override
					public void run() {
						game.setDoublePoints(false);
					}

				}, plugin.config.doublePointsTimer * 20);
				e.getItem().remove();
				e.setCancelled(true);
				return;
			}
			if (e.getItem().getItemStack().getType() == fireSale) {
				currentPerks.remove(e.getItem().getItemStack());
				p.getInventory().remove(item);
				game.setFireSale(true);
				game.boxManager.FireSale(true);
				notifyAll(game, "Fire Sale!");
				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

					@Override
					public void run() {
						game.setFireSale(false);
						game.boxManager.FireSale(false);
					}

				}, plugin.config.fireSaleTimer * 20);
				e.getItem().remove();
				e.setCancelled(true);
				return;
			}
			p.updateInventory();
			currentPerks.remove(e.getItem().getItemStack());
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

				@Override
				public void run() {
					p.getInventory().removeItem(eItem.getItemStack());
				}

			}, 5L);
		}

	}

	public void notifyAll(Game game, String message) {
		for (Player pl : game.players) {
			pl.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + message);
			if (message.equalsIgnoreCase("Max ammo!")) {
				pl.playSound(pl.getLocation(), Sound.ENTITY_PLAYER_BIG_FALL, 1, 1);
			}
			if (message.equalsIgnoreCase("Insta-kill!")) {
				pl.playSound(pl.getLocation(), Sound.ENTITY_LIGHTNING_THUNDER, 1, 1);
			}
			if (message.equalsIgnoreCase("Carpenter!")) {
				pl.playSound(pl.getLocation(), Sound.BLOCK_STONE_BREAK, 1, 1);
			}
			if (message.equalsIgnoreCase("Nuke!")) {
				pl.playSound(pl.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
			}
			if (message.equalsIgnoreCase("Double points!")) {
				pl.playSound(pl.getLocation(), Sound.BLOCK_GLASS_BREAK, 1, 1);
			}
			if (message.equalsIgnoreCase("Fire sale!")) {
				pl.playSound(pl.getLocation(), Sound.BLOCK_FIRE_AMBIENT, 1, 1);
			}
		}
	}

	/**
	 * Returns the list of all item per drops.
	 * 
	 * @return list of perk drop
	 */
	public ArrayList<ItemStack> getCurrentDroppedPerks() {
		return currentPerks;
	}

	public void removeItemFromList(ItemStack stack) {
		if (currentPerks.contains(stack)) {
			currentPerks.remove(stack);
		}
	}
}