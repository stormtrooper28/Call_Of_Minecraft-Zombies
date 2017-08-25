package com.zombies.guns;

import org.bukkit.Material;

public enum GunTypeEnum {

	ASSAULT_RIFLES(Material.GOLD_HOE, "AssaultRifles"),
	SUB_MACHINE_GUNS(Material.STICK, "SubMachineGuns"),
	LIGHT_MACHINE_GUNS(Material.IRON_HOE, "LightMachineGuns"),
	PISTOLS(Material.WOOD_HOE, "Pistols"),
	SNIPER_RIFLES(Material.BLAZE_ROD, "SniperRifles"),
	SHOTGUNS(Material.STONE_HOE, "Shotguns"),
	SPECIAL(Material.DIAMOND_HOE, "Specials");
	
	private Material material;
	private String toString;
	
	GunTypeEnum(Material material, String toString) {
		this.material = material;
		this.toString = toString;
	}

	@Override
	public String toString() {
		return toString;
	}
	
	public Material getMaterial() {
		return material;
	}

	public static GunTypeEnum getGun(String name) {
		for (GunTypeEnum type : values()) if (type.toString.equalsIgnoreCase(name) || type.name().equalsIgnoreCase(name)) return type;
		return null;
	}
}
