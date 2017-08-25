package com.zombies.game;

import java.util.HashMap;

import com.zombies.COMZombiesMain;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

public class GameScoreboard {

	private Game game;
	private COMZombiesMain plugin = COMZombiesMain.getInstance();
	private ScoreboardManager manager = Bukkit.getScoreboardManager();
	private Scoreboard board;
	private Team team;
	private Objective objective;
	private Score round;
	private HashMap<Player, Score> playerScores = new HashMap<Player, Score>();

	public GameScoreboard(Game game) {
		this.game = game;
		board = manager.getNewScoreboard();
		team = board.registerNewTeam(game.getName());
		team.setDisplayName(ChatColor.RED + game.getName());
		team.setCanSeeFriendlyInvisibles(true);
		team.setAllowFriendlyFire(false);
		objective = board.registerNewObjective(this.game.getName(), "dummy");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		objective.setDisplayName(ChatColor.RED + this.game.getName());
		round = objective.getScore(ChatColor.RED + "Round");
		round.setScore(0);
	}

	public void addPlayer(Player player) {
		team.addEntry(player.getDisplayName());
		Score s = objective.getScore(player.getName());
		playerScores.put(player, s);
		for (Player pl : game.players) {
			if (pl.isValid()) pl.setScoreboard(board);
			playerScores.get(player).setScore(500);
		}
		game.signManager.updateGame();
	}

	public void removePlayer(Player player) {
		team.removeEntry(player.getDisplayName());
		board.resetScores(player.getName());
		player.setScoreboard(manager.getNewScoreboard());
		playerScores.remove(player);
		game.signManager.updateGame();
	}

	public void update() {
		Bukkit.broadcastMessage("gameScoreboard.update_0");
		round.setScore(game.waveNumber);
		for (Player player : playerScores.keySet()) {
			if(playerScores.containsKey(player))
				playerScores.get(player).setScore(plugin.pointManager.getPlayersPoints(player));
			else
				playerScores.get(player).setScore(500);
		}
		Bukkit.broadcastMessage("gameScoreboard.update_0.5");
		game.signManager.updateGame();
		Bukkit.broadcastMessage("gameScoreboard.update_1");
	}
}
