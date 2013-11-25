package nl.SugCube.CrystalQuest.Game;

import java.util.Random;

import nl.SugCube.CrystalQuest.CrystalQuest;

import org.bukkit.Location;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.EntityType;

public class CrystalSpawner implements Runnable {

	public static CrystalQuest plugin;
	
	Random ran = new Random();
	
	public CrystalSpawner(CrystalQuest instance) {
		plugin = instance;
	}

	/*
	 *	Ticks every 2 gameticks 
	 */
	public void run() {

		for (Arena a : plugin.getArenaManager().getArenas()) {
			if (a.isInGame()) {
				if (a.getTimeLeft() > 0) {
					for (Location loc : a.getCrystalSpawns()) {
						if (!a.isEndGame() && !a.getGameCrystalMap().containsValue(loc)) {
							int chance = plugin.getConfig().getInt("arena.crystal-spawn-chance");
							if (ran.nextInt(chance * 10) == 0) {
								try {
									EnderCrystal ec = (EnderCrystal) a.getPlayers().get(0).getWorld()
											.spawnEntity(loc, EntityType.ENDER_CRYSTAL);
										a.getGameCrystals().add(ec);
										a.getGameCrystalMap().put(ec, loc);
								} catch (Exception ex) { }
							}
						}
					}
				}
			}
		}
		
	}

}