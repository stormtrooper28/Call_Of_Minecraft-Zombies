package com.zombies.listeners.customEvents;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.zombies.game.Game;

@Deprecated
public class GameStartEvent extends Event {

	private static final HandlerList handlers = new HandlerList();
	private final Game game;

	public GameStartEvent(Game game) {
		this.game = game;
		Bukkit.broadcastMessage("gameStartEvent Over");
	}

	public ArrayList<Player> getInGamePlayers() {
		return (ArrayList<Player>) game.players;
	}

	public Game getGame() {
		return game;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
