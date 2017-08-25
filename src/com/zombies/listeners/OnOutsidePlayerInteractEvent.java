package com.zombies.listeners;

import java.util.ArrayList;

import com.zombies.COMZombiesMain;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

import com.zombies.game.Game;
import com.zombies.game.Game.ArenaStatus;

public class OnOutsidePlayerInteractEvent implements Listener {
	private ArrayList<ItemStack> currentPerks = new ArrayList<ItemStack>();

	COMZombiesMain plugin;

	public OnOutsidePlayerInteractEvent(COMZombiesMain instance) {
		plugin = instance;
	}

	@EventHandler
	public void onOusidePlayerItemPickUp(EntityPickupItemEvent e) {
		if(!(e.getEntity() instanceof Player)) return;
		Player player = (Player) e.getEntity();
		Game game = plugin.manager.getGame(player.getLocation());
		if (game == null || game.mode == null) return;
		if (!(game.mode.equals(ArenaStatus.INGAME))) { return; }
		if (!plugin.manager.isPlayerInGame(player) && plugin.manager.isLocationInGame(player.getLocation())) {
			e.setCancelled(true);
		}
		if (plugin.manager.isPlayerInGame(player)) {
			currentPerks = game.perkManager.getCurrentDroppedPerks();
			if (!currentPerks.contains(e.getItem())) {
				e.getItem().remove();
				return;
			}
			e.setCancelled(true);
			e.getItem().remove();
		}
	}

	@EventHandler
	public void itemDrop(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		if (plugin.manager.isPlayerInGame(player)) {
			event.setCancelled(true);
		}
		Location loc = player.getLocation();
		if (plugin.manager.isLocationInGame(loc)) {
			if (!(plugin.manager.getGame(loc).mode == ArenaStatus.INGAME)) { return; }
			event.setCancelled(true);
			if (!plugin.manager.isPlayerInGame(player)) {
				player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Do not drop items in this arena!");
			}
		}
	}
}
