package com.zombies.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.zombies.COMZombiesMain;

public class OnPlayerJoinEvent implements Listener {

	private COMZombiesMain plugin;

	public OnPlayerJoinEvent(COMZombiesMain p) {
		plugin = p;
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		for (Player pl : Bukkit.getOnlinePlayers()) {
			if (plugin.manager.isPlayerInGame(pl)) {
				pl.hidePlayer(player);
				player.showPlayer(pl);
			}
			else {
				pl.showPlayer(player);
				player.showPlayer(pl);
			}
		}
	}
}
