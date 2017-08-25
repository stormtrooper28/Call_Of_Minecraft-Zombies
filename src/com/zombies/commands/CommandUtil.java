package com.zombies.commands;

import com.zombies.COMZombiesMain;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.zombies.game.Game;

public class CommandUtil {
	/**
	 * 
	 * @param player to send the message to
	 * @param message to be sent to the player
	 */
	public static void sendMessageToPlayer(Player player, String message) {
		player.sendMessage(COMZombiesMain.prefix + message);
	}
	
	/**
	 * 
	 * @param player for the no permission message to be sent to
	 */
	public static void noPermission(Player player) {
		player.sendMessage(COMZombiesMain.prefix + ChatColor.RED + "No permission!");
	}

	/**
	 * 
	 * @param message to be sent to all players on the server
	 */
	public static void sendAll(String message) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			sendMessageToPlayer(player, message);
		}
	}
	
	/**
	 * 
	 * @param player that doesnt recive the message
	 * @param arena of the players that the message is for
	 * @param message to be given to the players
	 */
	public static void sendToAllPlayersInGameExcludingPlayer(Player player, Game arena, String message) {
		for (Player pl : arena.players) {
			if (pl.equals(player)) continue;
			sendMessageToPlayer(pl, message);
		}
	}

	/**
	 * 
	 * @param arena of the players that the message is for
	 * @param message to be given to the players
	 */
	public static void sendToAllPlayersInGame(Game arena, String message) {
		for (Player pl : arena.players) {
			sendMessageToPlayer(pl, message);
		}
	}
}
