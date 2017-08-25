package com.zombies.kits;

import java.util.ArrayList;
import java.util.HashMap;

import com.zombies.COMZombiesMain;
import org.bukkit.entity.Player;

import com.zombies.game.Game;

public class KitManager {
	private COMZombiesMain plugin;
	private ArrayList<Kit> kits = new ArrayList<Kit>();
	private HashMap<Player, Kit> selectedKits = new HashMap<Player, Kit>();

	public KitManager(COMZombiesMain plugin) {
		this.plugin = plugin;
	}

	public void newKit(String name) {
		plugin.getClass();
	}

	public Kit getKit(String name) {
		for(Kit k: kits) {
			if(k.getName().equalsIgnoreCase(name)) {
				return k;
			}
		}
		return null;
	}

	public void loadKits() {
		for (String key : plugin.configManager.getConfig("Kits").getConfigurationSection("").getKeys(false)) {
			Kit kit = new Kit(plugin, key);
			kit.load();
			kits.add(kit);
		}
	}

	public void giveOutKits(Game game) {
		for(Player player: selectedKits.keySet())
			selectedKits.get(player).GivePlayerStartingItems(player);

	}
	
	public void addPlayersSelectedKit(Player player, Kit kit) {
		selectedKits.put(player, kit);
	}
}
