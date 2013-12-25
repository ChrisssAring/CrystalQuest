package nl.SugCube.CrystalQuest.API;

import nl.SugCube.CrystalQuest.CrystalQuest;
import nl.SugCube.CrystalQuest.Economy.Economy;
import nl.SugCube.CrystalQuest.Game.ArenaManager;
import nl.SugCube.CrystalQuest.Items.ItemHandler;

import org.bukkit.Location;

public class CrystalQuestAPI {

	public static CrystalQuest plugin;
	
	public static void setPlugin(CrystalQuest instance) {
		plugin = instance;
	}

	/**
	 * Gets the class managing all what has to do with the
	 * shop-system. Get the balances of the players, open menus
	 * and do what you want :)
	 * @return (Economy)
	 */
	public Economy getEconomy() {
		return plugin.economy;
	}
	
	/**
	 * Checks if the given location is protected
	 * @param loc (Location) The location to check for
	 * @return (boolean) True if within, false if not
	 */
	public boolean isInProtectedArena(Location loc) {
		return plugin.prot.isInProtectedArena(loc);
	}
	
	/**
	 * Gets the class managing the items
	 * @return (ItemHandler)
	 */
	public ItemHandler getItemHandler() {
		return plugin.itemHandler;
	}
	
	/**
	 * Gets the class handling the arenas
	 * @return (ArenaManager)
	 */
	public ArenaManager getArenaManager() {
		return plugin.getArenaManager();
	}
	
}