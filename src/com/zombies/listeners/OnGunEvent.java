package com.zombies.listeners;

import net.minecraft.server.v1_12_R1.EnumParticle;
import org.bukkit.Bukkit;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.zombies.COMZombiesMain;
import com.zombies.game.Game;
import com.zombies.game.Game.ArenaStatus;
import com.zombies.guns.Gun;
import com.zombies.guns.GunManager;
import com.zombies.particleUtilities.ParticleEffects;


public class OnGunEvent implements Listener {
	
	private COMZombiesMain plugin;
	
	public OnGunEvent(COMZombiesMain pl) {
		plugin = pl;
	}
	
	@EventHandler
	public void onPlayerShootEvent(PlayerInteractEvent event) {
		Bukkit.broadcastMessage("shoot_0");
		if (!(event.getAction() == Action.RIGHT_CLICK_AIR) && !(event.getAction() == Action.RIGHT_CLICK_BLOCK)) return;
		if (event.getAction() == (Action.RIGHT_CLICK_BLOCK))
			if (event.getClickedBlock().getType() == Material.SIGN || event.getClickedBlock().getType() == Material.SIGN_POST || event.getClickedBlock().getType() == Material.WALL_SIGN)  return;

		Player player = event.getPlayer();
		if (plugin.manager.isPlayerInGame(player)) {
			Game game = plugin.manager.getGame(player);
			if (!(game.mode == ArenaStatus.INGAME)) { return; }
			if (game.getPlayersGun(player) != null) {
				GunManager gunManager = game.getPlayersGun(player);
				if (gunManager.isGun()) {
					Gun gun = gunManager.getGun(player.getInventory().getHeldItemSlot());
					if (gun.isReloading())
						player.getLocation().getWorld().playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
					else
						gun.wasShot();
				}
			}
		}
		Bukkit.broadcastMessage("shoot_1");
	}
	
	@EventHandler
	public void onGunReload(PlayerInteractEvent e) {
		Bukkit.broadcastMessage("reload_0");
		if (e.getAction().equals(Action.LEFT_CLICK_AIR) || e.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
			Player player = e.getPlayer();
			if (plugin.manager.isPlayerInGame(player)) {
				Game game = plugin.manager.getGame(player);
				if (!(game.mode == ArenaStatus.INGAME)) { return; }
				if (game.getPlayersGun(player) != null) {
					GunManager gunManager = game.getPlayersGun(player);
					if (gunManager.isGun()) {
						Gun gun = gunManager.getGun(player.getInventory().getHeldItemSlot());
						gun.reload();
						gun.updateGun();
					}
				}
			}
		}
		Bukkit.broadcastMessage("reload_1");
	}

	@EventHandler
	public void onZombieHitEvent(EntityDamageByEntityEvent event) {
	    Bukkit.broadcastMessage("zombie-hit_0");
		if (event.getDamager() instanceof Snowball) {
			Snowball snowball = (Snowball) event.getDamager();
			if (snowball.getShooter() instanceof Player) {
				Player player = (Player) snowball.getShooter();
				if (plugin.manager.isPlayerInGame(player)) {
					Game game = plugin.manager.getGame(player);
					GunManager manager = game.getPlayersGun(player);
					if (manager.isGun()) {
						Gun gun = manager.getGun(player.getInventory().getHeldItemSlot());
						int damage = 0;
						if (gun.isPackOfPunched()) damage = gun.getType().packAPunchDamage;
						else damage = gun.getType().damage;
						if (event.getEntity() instanceof Zombie) {
							Zombie zomb = (Zombie) event.getEntity();
							int totalHealth;

							if (gun.getType().name.equalsIgnoreCase("Zombie BFF")) {
								for (int i = 0; i < 30; i++) {
									float x = (float) (Math.random());
									float y = (float) (Math.random());
									float z = (float) (Math.random());

									ParticleEffects.sendToPlayer(player, EnumParticle.HEART, zomb.getLocation(), x, y, z, 1, 1);
								}
							}
							for (Player pl : game.players) {
								pl.playSound(pl.getLocation(), Sound.BLOCK_LAVA_POP, 1.0F, 0.0F);
							}
							if (game.spawnManager.totalHealth().containsKey(event.getEntity())) {
								totalHealth = game.spawnManager.totalHealth().get(event.getEntity());
							}
							else {
								game.spawnManager.setTotalHealth(event.getEntity(), 20);
								totalHealth = 20;
							}
							if (totalHealth >= 20) {
								zomb.setHealth(20);
								if (game.isDoublePoints()) {
									plugin.pointManager.addPoints(player, plugin.config.pointsOnHit * 2);
								}
								else {
									plugin.pointManager.addPoints(player, plugin.config.pointsOnHit);
								}
								if (game.spawnManager.totalHealth().get(event.getEntity()) <= 20) {
									zomb.setHealth(game.spawnManager.totalHealth().get(event.getEntity()));
								}
								else {
									game.spawnManager.setTotalHealth(event.getEntity(), totalHealth - damage);
								}
								plugin.pointManager.notifyPlayer(player);
							}
							else if (zomb.getHealth() - damage < 1) {
								OnZombiePerkDrop perkdrop = new OnZombiePerkDrop(plugin);
								perkdrop.perkDrop(zomb, player);
								zomb.remove();
								boolean doublePoints = game.isDoublePoints();
								if (doublePoints) {
									plugin.pointManager.addPoints(player, plugin.config.pointsOnKill * 2);
								}
								else {
									plugin.pointManager.addPoints(player, plugin.config.pointsOnKill);
								}
								
								zomb.playEffect(EntityEffect.DEATH);
								plugin.pointManager.notifyPlayer(player);
								game.spawnManager.removeEntity((Entity)zomb);
								game.zombieKilled(player);
								if (game.spawnManager.getEntities().size() <= 0) {
									game.nextWave();
								}
							}
							else {
								event.setDamage(damage);
								boolean doublePoints = game.isDoublePoints();
								if (doublePoints) {
									plugin.pointManager.addPoints(player, plugin.config.pointsOnHit * 2);
								}
								else {
									plugin.pointManager.addPoints(player, plugin.config.pointsOnHit);
								}
								plugin.pointManager.notifyPlayer(player);
							}
							if (game.isInstaKill()) {
								zomb.remove();
							}
						}
					}
				}
			}
		}
		Bukkit.broadcastMessage("zombie-hit_1");
	}
	
	@EventHandler
	public void onPlayerMonkeyBomb(PlayerInteractEvent event) {
		Bukkit.broadcastMessage("monkey_bomb_0");
		if (!(event.getAction() == Action.RIGHT_CLICK_AIR) && !(event.getAction() == Action.RIGHT_CLICK_BLOCK)) return;

		if (event.getAction() == Action.RIGHT_CLICK_BLOCK)
			if (event.getClickedBlock().getType() == Material.SIGN || event.getClickedBlock().getType() == Material.SIGN_POST || event.getClickedBlock().getType() == Material.WALL_SIGN) return;

		final Player player = event.getPlayer();
		if (plugin.manager.isPlayerInGame(player)) {
			Game game = plugin.manager.getGame(player);
			if (!(game.mode == ArenaStatus.INGAME)) return;
			if (player.getInventory().getItemInMainHand().getType() == Material.MAGMA_CREAM) {
				player.getInventory().removeItem(new ItemStack(Material.MAGMA_CREAM, 1));
				final Item item = player.getWorld().dropItemNaturally(player.getEyeLocation(), new ItemStack(Material.MAGMA_CREAM));
				//Location Iloc = item.getLocation();
				item.setVelocity(player.getLocation().getDirection().multiply(1));
				item.setPickupDelay(1000);
				/*for(Entity e: game.spawnManager.mobs) {

				}*/
				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                    Location loc = item.getLocation();
                    player.getWorld().createExplosion(loc.getX(), loc.getY(), loc.getZ(), 4.0F, false, false);
                    item.remove();
                }, 140);
			}
		}
		Bukkit.broadcastMessage("monkey_bomb_1");
	}         
}
