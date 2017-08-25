package com.zombies.listeners;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.EntityEffect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.zombies.COMZombiesMain;
import com.zombies.game.Game;
import com.zombies.game.Game.ArenaStatus;
import com.zombies.game.features.DownedPlayer;
import com.zombies.game.features.PerkType;

public class OnEntityDamageEvent implements Listener {
	private COMZombiesMain plugin;
	private ArrayList<Player> beingHealed = new ArrayList<Player>();
	
	public OnEntityDamageEvent(COMZombiesMain zombies) {
		plugin = zombies;
	}
	
	@EventHandler
	public void damge(EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof Player) {
			if (plugin.manager.isPlayerInGame((Player) e.getEntity())) {
				if (e.getCause() == DamageCause.ENTITY_ATTACK) {
					if (e.getDamager() instanceof Player) {
						e.setCancelled(true);
					}
					else {
						Entity entity = e.getDamager();
						if (!(plugin.manager.isEntityInGame(entity))) {
							if (plugin.manager.isPlayerInGame((Player) e.getEntity())) {
								e.setCancelled(true);
							}
						}
						else {
							final Player player = (Player) e.getEntity();
							Game game = plugin.manager.getGame(player);
							int damage = 6;
							if (game.perkManager.getPlayersPerks().containsKey(player)) {
								if (game.perkManager.getPlayersPerks().get(player).contains(PerkType.JUGGERNOG)) {
									damage /= 2;
								}
							}
							if (player.getHealth() - damage < 1) {
								e.setCancelled(true);
								playerDowned(player, game);
								return;
							}
							else {
								if(game.downedPlayerManager.isPlayerDowned(player)) {
									e.setCancelled(true);
								}
								e.setDamage(damage);
							}
							Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
								
								public void run() {
									healPlayer(player);
								}
								
							}, 100L);
						}
					}
				}
			}
		}
		else if (e.getEntity() instanceof Zombie) {
			Entity entity = e.getEntity();
			int damage = 0;
			if (!(plugin.manager.isEntityInGame(entity))) return;
			Game game = plugin.manager.getGame(entity);
			if (game != null) {
				
				if (e.getDamager() instanceof Player) {
					Player player = (Player) e.getDamager();
					if (player.getInventory().getItemInMainHand().getType().equals(Material.IRON_SWORD)) {
						if (game.players.contains(player)) {
							Zombie zombie1 = (Zombie) entity;
							double damageAmount = e.getDamage();
							int totalHealth;
							double cx = player.getLocation().getX() - zombie1.getLocation().getX();
							double cy = player.getLocation().getY() - zombie1.getLocation().getY();
							double cz = player.getLocation().getZ() - zombie1.getLocation().getZ();
							
							if (Math.sqrt(Math.pow(cx, 2) + Math.pow(cy, 2) + Math.pow(cz, 2)) <= plugin.config.meleeRange) {
								damageAmount = 5;
							}
							else {
								e.setCancelled(true);
								return;
							}
							if (game.spawnManager.totalHealth().containsKey(e.getEntity())) {
								totalHealth = game.spawnManager.totalHealth().get(e.getEntity());
							}
							else {
								game.spawnManager.setTotalHealth(entity, 20);
								totalHealth = 20;
							}
							if (totalHealth >= 20) {
								zombie1.setHealth(20);
								if (game.isDoublePoints()) {
									plugin.pointManager.addPoints(player, plugin.config.pointsOnHit * 2);
								}
								else {
									plugin.pointManager.addPoints(player, plugin.config.pointsOnHit);
								}
								game.spawnManager.setTotalHealth(e.getEntity(), (int) (totalHealth - damageAmount));
								if (game.spawnManager.totalHealth().get(e.getEntity()) < 20) {
									zombie1.setHealth(game.spawnManager.totalHealth().get(e.getEntity()));
								}
								plugin.pointManager.notifyPlayer(player);
							}
							else if (totalHealth < 1 || totalHealth - damageAmount <= 1) {
								e.setCancelled(true);
								OnZombiePerkDrop perkdrop = new OnZombiePerkDrop(plugin);
								perkdrop.perkDrop(zombie1, player);
								zombie1.remove();
								boolean doublePoints = game.isDoublePoints();
								if (doublePoints) {
									plugin.pointManager.addPoints(player, plugin.config.pointsOnKill * 2);
								}
								else {
									plugin.pointManager.addPoints(player, plugin.config.pointsOnKill);
								}
								zombie1.playEffect(EntityEffect.DEATH);
								plugin.pointManager.notifyPlayer(player);
								game.spawnManager.removeEntity((Entity) zombie1);
								game.zombieKilled(player);
							}
							else {
								zombie1.damage(damage);
								if (game.isDoublePoints()) {
									plugin.pointManager.addPoints(player, plugin.config.pointsOnHit * 2);
								}
								else {
									plugin.pointManager.addPoints(player, plugin.config.pointsOnHit);
								}
								plugin.pointManager.notifyPlayer(player);
							}
							game.spawnManager.setTotalHealth(e.getEntity(), (int) (totalHealth - damageAmount));
							if (game.isInstaKill()) {
								zombie1.remove();
								game.spawnManager.removeEntity((Entity) zombie1);
							}
							for (Player pl : game.players) {
								pl.playSound(entity.getLocation().add(0, 1, 0), Sound.BLOCK_STONE_STEP, 1, 1);
							}
						}
						else {
							e.setCancelled(true);
						}
					}
					else {
						e.setCancelled(true);
					}
					return;
				}
			}
		}
	}
	
	@EventHandler
	public void damgeEvent(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player) {
			Player player = (Player) e.getEntity();
			if( plugin.manager.getGame(player) == null)
				return;
			if ( plugin.manager.getGame(player).downedPlayerManager.isPlayerDowned(player)) {
				e.setCancelled(true);
			}
			if (plugin.manager.getGame(player) != null && plugin.manager.getGame(player).mode == ArenaStatus.STARTING) {
				e.setCancelled(true);
			}
			if (player.getHealth() < 1 || player.getHealth() - e.getDamage() < 1) {
				if (plugin.manager.isPlayerInGame(player)) {
					Game game = plugin.manager.getGame(player);
					if (game.mode == ArenaStatus.INGAME) {
						e.setCancelled(true);
						playerDowned(player, game);
					}
				}
			}
			if (plugin.manager.isPlayerInGame(player)) player.getLocation().getWorld().playEffect(player.getLocation().add(0, 1, 0), Effect.STEP_SOUND, 152);
		}
		else if(e.getCause().equals(DamageCause.LAVA) && e.getEntity() instanceof Zombie) {
			Zombie z = (Zombie) e.getEntity();
			Game game =  plugin.manager.getGame(z);
			if(game == null)
				return;
			z.setFireTicks(0);
			z.teleport(game.getPlayerSpawn());
			e.setCancelled(true);
		}
	}
	
	private void playerDowned(Player player, final Game game) {
		if (player.getFireTicks() > 0) {
			player.setFireTicks(0);
		}
		if (!game.downedPlayerManager.isPlayerDowned(player)) {
			Bukkit.broadcastMessage(COMZombiesMain.prefix + player.getName() + " Has gone down! Stand close and right click him to revive");
			DownedPlayer down = new DownedPlayer(player, game);
			down.setPlayerDown(true);
		}
		if(game.downedPlayerManager.getDownedPlayers().size() == game.players.size()) {
			for (DownedPlayer downedPlayer : game.downedPlayerManager.getDownedPlayers()) {
				downedPlayer.cancelDowned();
			}
			game.endGame();
			return;
		}
	}
	
	public void healPlayer(final Player player) {
		if (beingHealed.contains(player)) return;
		else beingHealed.add(player);
		if (!(plugin.manager.isPlayerInGame(player))) return;
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			
			@Override
			public void run() {
				if (!(player.getHealth() == 20)) {
					player.setHealth(player.getHealth() + 1);
					healPlayer(player);
				}
				else {
					beingHealed.remove(player);
					return;
				}
			}
			
		}, 20L);
	}
	
	public void removeDownedPlayer(Player player) {
		plugin.manager.getGame(player).downedPlayerManager.removeDownedPlayer(player);
	}

	public boolean isDownedPlayer(String name) {
		return plugin.manager.getGame(Bukkit.getPlayer(name)).downedPlayerManager.isPlayerDowned(Bukkit.getPlayer(name));
	}
}