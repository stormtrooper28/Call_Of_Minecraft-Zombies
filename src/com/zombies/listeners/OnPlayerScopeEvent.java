package com.zombies.listeners;

import com.zombies.COMZombiesMain;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;

import com.zombies.game.Game;
import com.zombies.guns.Gun;
import com.zombies.guns.GunManager;
import com.zombies.guns.GunTypeEnum;

public class OnPlayerScopeEvent implements Listener {
	private final COMZombiesMain plugin;

	public OnPlayerScopeEvent(COMZombiesMain zombies) {
		plugin = zombies;
	}

	@EventHandler
	public void onPlayerSneak(final PlayerToggleSneakEvent e) {
		Player player = e.getPlayer();
		if (plugin.manager.isPlayerInGame(player)) {
			Game game = plugin.manager.getGame(player);
			GunManager manager = game.getPlayersGun(player);
			if (!manager.isGun()) return;
			Gun g = manager.getGun(player.getInventory().getHeldItemSlot());
			boolean isSniper = false;
			if (g.getType().type.equals(GunTypeEnum.SNIPER_RIFLES))
				isSniper = true;
			if (game.mode.equals(Game.ArenaStatus.INGAME)) {
				if (player.isSneaking()) {
					player.setWalkSpeed(0.2F);
					player.getInventory().setHelmet(new ItemStack(Material.CHAINMAIL_HELMET, 1));
				}
				else {
					if (player.getWalkSpeed() == 0.2F) {
						if (isSniper) {
							player.setWalkSpeed(-0.2F);
							if(plugin.getConfig().getBoolean("config.gameSettings.ZoomTexture")) {
								player.getInventory().setHelmet(new ItemStack(Material.PUMPKIN, 1));
							}
						}
						else  {
							player.setWalkSpeed(-0.1F);
						}
					}
				}
			}
			else if ((player.getWalkSpeed() == 0.2F) || (player.getWalkSpeed() == 0.15F)) {
				player.setWalkSpeed(-0.2F);
				player.getInventory().setHelmet(new ItemStack(Material.CHAINMAIL_HELMET, 1));
			}
		}
	}
}