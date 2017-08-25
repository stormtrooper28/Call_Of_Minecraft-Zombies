package com.zombies.listeners;

import com.zombies.COMZombiesMain;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

public class OnEntitySpawnEvent implements Listener {

	private COMZombiesMain plugin;

	public OnEntitySpawnEvent(COMZombiesMain pl) {
		plugin = pl;
	}

	@EventHandler
	public void spawn(CreatureSpawnEvent event) {
		Entity entity = event.getEntity();
		if (!event.getSpawnReason().equals(SpawnReason.CUSTOM)) {
			if (plugin.manager.isLocationInGame(entity.getLocation())) {
				event.setCancelled(true);
			}
		}
	}
}
