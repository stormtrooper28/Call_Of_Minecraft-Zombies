package com.zombies.listeners;

import com.zombies.COMZombiesMain;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import com.zombies.commands.CommandUtil;

public class OnPreCommandEvent implements Listener {

	private COMZombiesMain plugin;

	public OnPreCommandEvent(COMZombiesMain p) {
		plugin = p;
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		String args[] = event.getMessage().split(" ");
		if (args[0].substring(0, 1).equalsIgnoreCase("/")) {
			String command = args[0].substring(1);
			Player player = event.getPlayer();
			if (command.equalsIgnoreCase("zombies") || command.equalsIgnoreCase("z") || command.equalsIgnoreCase("zom")) { return; }
			if (plugin.manager.isPlayerInGame(player)) {
				CommandUtil.sendMessageToPlayer(player, ChatColor.RED + "" + ChatColor.BOLD + "You are not allowed to use commands in game!");
				event.setCancelled(true);
			}
		}
	}
}
