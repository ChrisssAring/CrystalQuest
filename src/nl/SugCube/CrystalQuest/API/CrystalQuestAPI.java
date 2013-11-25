package nl.SugCube.CrystalQuest.API;

import nl.SugCube.CrystalQuest.CrystalQuest;
import nl.SugCube.CrystalQuest.Game.ArenaManager;
import nl.SugCube.CrystalQuest.Items.ItemHandler;

public class CrystalQuestAPI {

	public static CrystalQuest plugin;
	
	public static void setPlugin(CrystalQuest instance) {
		plugin = instance;
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