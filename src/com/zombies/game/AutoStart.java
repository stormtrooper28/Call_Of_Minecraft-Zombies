package com.zombies.game;

import com.zombies.COMZombiesMain;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.zombies.commands.CommandUtil;
import com.zombies.game.Game.ArenaStatus;

/**
 * Arena auto start class.
 * 
 * @credit garbagemule for the private Countdown class.
 */
public class AutoStart {

	/**
	 * Game in which will be started once timer is activated.
	 */
	private Game game;
	/**
	 * Main class instance.
	 */
	private COMZombiesMain plugin;
	/**
	 * Seconds until game starts.
	 */
	private int seconds;
	/**
	 * If the timer is started, value is true.
	 */
	public boolean started = false;
	/**
	 * Countdown class used as a timer.
	 */
	private Countdown timer;
	/**
	 * If the arena is force started, value is true.
	 */
	public boolean forced = false;
	/**
	 * If this is false, the timer will not continue.
	 */
	public boolean stopped = false;
	private int timeLeft = 0;

	/**
	 * Constructs a new AutoStart based off of the params
	 * 
	 * @param instance
	 *            class / plugin instance
	 * @param game
	 *            to be started
	 * @param seconds
	 *            until game starts
	 */
	public AutoStart(COMZombiesMain instance, Game game, int seconds) {
		if (seconds == -1) { return; }
		this.game = game;
		plugin = instance;
		this.seconds = seconds;
	}

	/**
	 * Begins the countdown!
	 */
	public void startTimer() {
		try {
			if (seconds > 0 && !started) {
				started = true;
				timer = new Countdown(seconds);
				timer.run();
			}
		} catch (Exception e) {
			try {
				for (Player pl : game.players) {
					CommandUtil.sendMessageToPlayer(pl, "Error in joining " + game.getName() + ". Try rejoining!");
				}
			} catch (NullPointerException ex) { e.printStackTrace(); }
		}
	}

	public int getTimeLeft() {
		return timeLeft;
	}

	public void endTimer() {
		stopped = true;
	}

	public class Countdown implements Runnable {

		public int remain;
		private int index;
		private int[] warnings = { 1, 2, 3, 4, 5, 10, 30, 60 };

		private Countdown(int seconds) {
			remain = seconds;

			for (int i = 0; (i < warnings.length) && (seconds > warnings[i]); i++)
				index = i;
		}

		@Override
		public void run() {
			synchronized (this) {
				Bukkit.broadcastMessage("autoStart.run_0");
				if (AutoStart.this.game.mode == ArenaStatus.INGAME ||  false/*AutoStart.this.game.players.isEmpty()*/) { //@change to fix this!
					notifyAll();
					return;
				}

				remain--;

				if (remain <= 0) {
					Bukkit.broadcastMessage("begin Start");
					AutoStart.this.game.startArena();
					Bukkit.broadcastMessage("middle Start");
					//AutoStart.this.plugin.getServer().getPluginManager().callEvent(new GameStartEvent(AutoStart.this.game));
					Bukkit.broadcastMessage("end Start");
				}
				else {
					if (remain == warnings[index]) {
						for (Player pl : game.players) {
							CommandUtil.sendMessageToPlayer(pl, warnings[index] + " seconds!");
						}
						index = index - 1;
					}
					AutoStart.this.timeLeft = remain;
					game.signManager.updateGame();
					if (!stopped) AutoStart.this.game.scheduleSyncTask(this, 20);
				}
				notifyAll();

				Bukkit.broadcastMessage("autoStart.run_1");
			}

			Bukkit.broadcastMessage("autoStart.run_2");
		}

	}
}
