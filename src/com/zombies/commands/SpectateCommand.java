package com.zombies.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.zombies.COMZombiesMain;
import com.zombies.game.Game;

public class SpectateCommand implements SubCommand {

	private COMZombiesMain plugin;

	public SpectateCommand(ZombiesCommand cmd) {
		plugin = cmd.plugin;
	}

	public boolean onCommand(Player player, String[] args) {
		if (player.hasPermission("zombies.spectate")) {
			if (args.length == 1) {
				CommandUtil.sendMessageToPlayer(player, ChatColor.RED + "Please specify an arena to spectate!");
				return true;
			}
			else {
				String name = args[1];
				if (plugin.manager.isValidArena(name)) {
					Game game = plugin.manager.getGame(name);
					Location specLocation = game.getSpectateLocation();
					player.teleport(specLocation);
					CommandUtil.sendMessageToPlayer(player, ChatColor.RED + "You are now spectating " + ChatColor.GOLD + game.getName() + ChatColor.RED + "!");
					return true;
				}
				else {
					CommandUtil.sendMessageToPlayer(player, ChatColor.RED + "That is not a valid arena!");
					return true;
				}
			}
		}
		else {
			CommandUtil.sendMessageToPlayer(player, ChatColor.RED + "You do not have permission to spectate!");
		}
		return false;
	}

}
