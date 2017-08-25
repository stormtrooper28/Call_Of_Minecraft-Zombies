package com.zombies.commands;

import com.zombies.COMZombiesMain;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.zombies.game.Game;

public class ReloadCommand implements SubCommand {

	private COMZombiesMain plugin;

	public ReloadCommand(ZombiesCommand cmd) {
		plugin = cmd.plugin;
	}

	public boolean onCommand(Player player, String[] args) {
		if (player.hasPermission("zombies.reload") || player.hasPermission("zombies.admin")) {
			try {
				Bukkit.getServer().getPluginManager().disablePlugin(plugin);
				Bukkit.getServer().getPluginManager().enablePlugin(plugin);
				plugin.configManager.getConfig("ArenaConfig").reloadConfig();
				plugin.reloadConfig();
				for (Game gl : plugin.manager.games) {
					gl.endGame();
					gl.setDisabled();
				}
				plugin.manager.games.clear();
				plugin.manager.loadAllGames();
				plugin.clearAllSetup();
				for (Game gl : plugin.manager.games) {
					gl.enable();
				}
				CommandUtil.sendMessageToPlayer(player, ChatColor.RED + "" + ChatColor.BOLD + "Zombies has been reloaded!");
			} catch (org.bukkit.command.CommandException e) { e.printStackTrace(); }
			return true;
		}
		else {
			CommandUtil.sendMessageToPlayer(player, ChatColor.RED + "You do not have permission to reload zombies!");
			return true;
		}
	}
}
