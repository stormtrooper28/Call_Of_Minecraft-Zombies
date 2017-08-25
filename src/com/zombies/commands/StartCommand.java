package com.zombies.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.zombies.COMZombiesMain;
import com.zombies.game.Game;
import com.zombies.game.Game.ArenaStatus;

public class StartCommand implements SubCommand {

	private COMZombiesMain plugin;

	public StartCommand(ZombiesCommand cmd) {
		plugin = cmd.plugin;
	}

	public boolean onCommand(CommandSender sender, String[] args) {
		if (sender.hasPermission("zombies.forcestart") || sender.hasPermission("zombies.admin")) {
			if (args.length == 1) {
				if (sender instanceof Player && plugin.manager.isPlayerInGame((Player) sender)) {
				    Player p = (Player) sender;
					Game game = plugin.manager.getGame(p);
					if (game.mode == ArenaStatus.INGAME) {
						CommandUtil.sendMessageToPlayer(p, ChatColor.RED + "Game already started!");
					}
					else {
						try {
							game.forceStart();
						} catch (IllegalAccessError e) {
							game.startArena();
						}
					}
				}
				else {
					if(sender instanceof Player)
						CommandUtil.sendMessageToPlayer((Player) sender, ChatColor.RED + "You must either be waiting in a game or specify a game! /z s [arena]");
					else
						System.err.print("You must either be waiting in a game or specify a game! /z s [arena]");
					return true;
				}
			}
			else {
				if (plugin.manager.isValidArena(args[1])) {
					Game game = plugin.manager.getGame(args[1]);
					game.forceStart();
				}
				else {
					if(sender instanceof Player)
						CommandUtil.sendMessageToPlayer((Player) sender, ChatColor.RED + "No such game!");
					else
						System.err.print("No Such game!");
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean onCommand(Player player, String[] args) {
		return onCommand(player, args);
	}
}
