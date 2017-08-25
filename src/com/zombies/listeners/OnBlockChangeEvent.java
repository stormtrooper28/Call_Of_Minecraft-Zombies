package com.zombies.listeners;

import com.zombies.COMZombiesMain;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;

public class OnBlockChangeEvent implements Listener {
	
	private COMZombiesMain plugin;
	
	public OnBlockChangeEvent(COMZombiesMain pl) {
		plugin = pl;
	}
	
	@EventHandler
	public void onBlockChange(BlockDamageEvent event) {
		Location loc = event.getBlock().getLocation();
		if (plugin.manager.isLocationInGame(loc)) {
			event.setCancelled(true);
		}
	}
}
