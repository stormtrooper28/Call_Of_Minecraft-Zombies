package com.zombies.listeners;

import com.zombies.COMZombiesMain;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerExpChangeEvent;

public class OnPlayerGetEXPEvent implements Listener {

	private COMZombiesMain plugin;

	public OnPlayerGetEXPEvent(COMZombiesMain zm) {
		plugin = zm;
	}

	@EventHandler
	public void playerExp(PlayerExpChangeEvent event) {
		Player player = event.getPlayer();
		if (plugin.manager.isPlayerInGame(player)) player.setExp(0);
	}
}
