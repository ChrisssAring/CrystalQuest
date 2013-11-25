package nl.SugCube.CrystalQuest.Game;

import java.util.Random;

import nl.SugCube.CrystalQuest.CrystalQuest;

import org.bukkit.Location;
import org.bukkit.entity.ExperienceOrb;

public class ItemSpawner implements Runnable {

	public static CrystalQuest plugin;
	Random ran = new Random();
	
	public ItemSpawner(CrystalQuest instance) {
		plugin = instance;
	}
	
	public void run() {

		for (Arena a : plugin.getArenaManager().getArenas()) {
			if (a.isInGame()) {
				if (!a.isEndGame()) {
					if (a.getTimeLeft() > 0) {
						for (Location loc : a.getItemSpawns()) {
							if (ran.nextInt(10 * plugin.getConfig().getInt("arena.item-spawn-chance")) == 0) {
								loc.getWorld().dropItem(loc, plugin.itemHandler.getRandomItem());
							}
							if (ran.nextInt(10 * plugin.getConfig().getInt("arena.item-spawn-chance")) == 0) {
								for (int i = 0; i <= ran.nextInt(2); i++) {
									ExperienceOrb orb = loc.getWorld().spawn(loc.add(0, 0.2, 0), ExperienceOrb.class);
									orb.setExperience(ran.nextInt(3) + 1);
								}
							}
						}
					}
				}
			}
		}
		
	}
	
}