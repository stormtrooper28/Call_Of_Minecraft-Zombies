package com.zombies.particleUtilities;

import com.zombies.COMZombiesMain;
import net.minecraft.server.v1_12_R1.EnumParticle;
import net.minecraft.server.v1_12_R1.PacketPlayOutWorldParticles;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.logging.Level;

public class ParticleEffects {

	public static void sendToPlayer(Player player, EnumParticle particle, Location location, float offsetX, float offsetY, float offsetZ, float speed, int count)  {
		PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles();

		try {
			ReflectionUtilities.setValue(packet, "a", particle);
			ReflectionUtilities.setValue(packet, "b", (float) location.getX());
			ReflectionUtilities.setValue(packet, "c", (float) location.getY());
			ReflectionUtilities.setValue(packet, "d", (float) location.getZ());
			ReflectionUtilities.setValue(packet, "e", offsetX);
			ReflectionUtilities.setValue(packet, "f", offsetY);
			ReflectionUtilities.setValue(packet, "g", offsetZ);
			ReflectionUtilities.setValue(packet, "h", speed);
			ReflectionUtilities.setValue(packet, "i", count);

			((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
		} catch (Exception e) {
			COMZombiesMain.getInstance().log.log(Level.WARNING, "\n\n" + COMZombiesMain.consolePrefix + " Failed on particles!\n\n");

			e.printStackTrace();
		}

	}

	public static void sendToAllPlayers(EnumParticle particle, Location loc, float offsetX, float offsetY, float offsetZ, float speed, int count) {
		for (Player player : loc.getWorld().getPlayers())
				sendToPlayer(player, particle, loc, offsetX, offsetY, offsetZ, speed, count);
	}
}