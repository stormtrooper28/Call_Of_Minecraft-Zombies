package com.zombies.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.zombies.COMZombiesMain;
import com.zombies.commands.CommandUtil;
import com.zombies.game.Game;
import com.zombies.game.GameManager;
import com.zombies.game.Game.ArenaStatus;

public class OnPlayerMoveEvent implements Listener {

	private COMZombiesMain plugin;
	private GameManager gameManager;

	public OnPlayerMoveEvent(COMZombiesMain zombies) {
		plugin = zombies;
		gameManager = plugin.manager;
	}

	/**
	 * Checks if the player is leaving the arena and takes care of his action.
	 * 
	 * @param playerMove
	 *            - Player move event.
	 */
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent playerMove) {
		Player player = playerMove.getPlayer();
		if (gameManager.isPlayerInGame(player)) {
			Game game = gameManager.getGame(player);
			if (game.arena.containsBlock(player.getLocation())) { return; }
			if (game.mode == ArenaStatus.INGAME) {
				player.teleport(game.getPlayerSpawn());
				CommandUtil.sendMessageToPlayer(player, ChatColor.RED + "Please do not leave the arena!");
			}
		}
	}
}
