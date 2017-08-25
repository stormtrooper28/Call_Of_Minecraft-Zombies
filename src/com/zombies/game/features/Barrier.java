package com.zombies.game.features;

import java.util.ArrayList;
import java.util.List;

import com.zombies.COMZombiesMain;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;

import com.zombies.game.Game;
import com.zombies.spawning.SpawnPoint;

public class Barrier implements Runnable  {
	
	private Location loc;
	private Block block;
	private Material blockMat;
	private Location repairLoc;
	
	private int stage;
	private boolean breaking = false;
	
	private SpawnPoint spawn;
	
	private int number;
	
	private Game game;
	
	private int reward;
	
	private List<Entity> ents = new ArrayList<Entity>();
	private List<Entity> entsToAdd = new ArrayList<Entity>();
	
	public Barrier(Location l, Block b, int n, Game game) {
		loc = l;
		block = b;
		blockMat = b.getType();
		stage = 0;
		number = n;
		this.game = game;
	}
	
	public boolean damage() {
		stage++;
		
		if(stage > 5)
			stage = 5;
		
		game.updateBarrierDamage(stage, block);
		
		if(stage >= 5) {
			game.getWorld().getBlockAt(loc).setType(Material.AIR);
			return true;
		}
		else {
			if(stage > -1) {
				game.getWorld().getBlockAt(this.repairLoc).setType(Material.SIGN_POST);
				Sign sign = (Sign) game.getWorld().getBlockAt(this.repairLoc).getState();
				sign.setLine(0, "[BarrierRepair]");
				sign.setLine(1, "Break this to");
				sign.setLine(2, "repair the");
				sign.setLine(3, "barrier");
				sign.update();
			}
			return false;
		}
	}
	
	public boolean repair() {
		stage--;
		
		if(stage < -1)
			stage = -1;
		
		game.updateBarrierDamage(stage, block);
		
		if(stage > -1) {
			game.getWorld().getBlockAt(this.repairLoc).setType(Material.SIGN_POST);
			Sign sign = (Sign) game.getWorld().getBlockAt(this.repairLoc).getState();
			sign.setLine(0, "[BarrierRepair]");
			sign.setLine(1, "Break this to");
			sign.setLine(2, "repair the");
			sign.setLine(3, "barrier");
			sign.update();
		}
		else {
			game.getWorld().getBlockAt(this.repairLoc).setType(Material.AIR);
		}
		
		if(game.getWorld().getBlockAt(loc).getType().equals(Material.AIR))
			game.getWorld().getBlockAt(loc).setType(blockMat);

		return stage <= -1;
	}
	
	public void repairFull() {
		stage = -1;
		
		game.updateBarrierDamage(-1, block);
		
		if(game.getWorld().getBlockAt(loc).getType().equals(Material.AIR))
			game.getWorld().getBlockAt(loc).setType(blockMat);
		
		game.getWorld().getBlockAt(this.repairLoc).setType(Material.AIR);
		
		this.breaking = false;
	}
	
	public Location getLocation() {
		return loc;
	}
	
	public Block getBlock() {
		return block;
	}
	
	public int getStage() {
		return stage;
	}
	
	public void assingSpawnPoint(SpawnPoint sp) {
		spawn = sp;
	}
	
	public SpawnPoint getSpawnPoint() {
		return spawn;
	}
	
	public int getNum() {
		return number;
	}
	
	public int getReward() {
		return reward;
	}
	
	public void setReward(int reward) {
		this.reward = reward;
	}
	
	public Location getRepairLoc() {
		return repairLoc;
	}
	
	public void setRepairLoc(Location repairLoc) {
		this.repairLoc = repairLoc;
	}
	
	public Game getGame() {
		return game;
	}
	
	public void update() {

		Bukkit.broadcastMessage("barrier.update_0");
		if(ents.size() > 0) {
			if(!this.damage()) {
				for(int i = 0; i < ents.size(); i++) {
					Entity ent = ents.get(i);
					if(ent.isDead()) {
						ents.remove(ent);
						i--;
					}
				}
				ents.addAll(entsToAdd);
				entsToAdd.clear();
				Bukkit.getScheduler().scheduleSyncDelayedTask(COMZombiesMain.getInstance(), this, 3 * 20L);
			}
			else
				this.breaking = false;
		}
		else if(entsToAdd.size() > 0) {
			ents.addAll(entsToAdd);
			entsToAdd.clear();
			Bukkit.getScheduler().scheduleSyncDelayedTask(COMZombiesMain.getInstance(), this, 3 * 20L);
		}
		else
			this.breaking = false;
		Bukkit.broadcastMessage("barrier.update_1");
	}
	
	public void initBarrier(Entity ent) {
		entsToAdd.add(ent);
		if(this.stage < 6 && !breaking) {
			this.breaking = true;
			Bukkit.getScheduler().scheduleSyncDelayedTask(COMZombiesMain.getInstance(), this, 3 * 20L);
		}
	}
	
	@Override
	public void run() {
		update();
	}
}