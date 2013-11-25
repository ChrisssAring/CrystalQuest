package nl.SugCube.CrystalQuest.IO;

import nl.SugCube.CrystalQuest.CrystalQuest;
import nl.SugCube.CrystalQuest.Game.Arena;
import nl.SugCube.CrystalQuest.Game.ArenaManager;
import nl.SugCube.CrystalQuest.SBA.SMeth;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;

public class LoadData {

	public static CrystalQuest plugin;
	
	public LoadData(CrystalQuest instance) {
		plugin = instance;
	}
	
	/**
	 * Loads the lobby-signs
	 * @param void
	 * @return void
	 */
	public static void loadSigns() {
		plugin.signHandler.getSigns().clear();
		for (String s : plugin.getData().getStringList("signs")) {
			Location loc = SMeth.toLocation(s);
			if (loc.getBlock().getType() == Material.WALL_SIGN || loc.getBlock().getType() == Material.SIGN_POST) {
				Sign sign = (Sign) loc.getBlock().getState();
				plugin.signHandler.getSigns().add(sign);
			}
		}
		plugin.signHandler.updateSigns();
	}
	
	/**
	 * Loads the lobby-spawn
	 * @param void
	 * @return void
	 */
	public static void loadLobbySpawn() {
		if (!plugin.getData().getString("lobby-spawn").isEmpty()) {
			plugin.am.setLobby(SMeth.toLocation(plugin.getData().getString("lobby-spawn")));
		}
	}
	
	/**
	 * Loads all the arenas to the ArenaManager
	 * @param void
	 * @return void
	 */
	@SuppressWarnings("unused")
	public static void loadArenas() {
		try {
			FileConfiguration data = plugin.getData();
	
			for (String s : data.getConfigurationSection("arena").getKeys(false)) {
				ArenaManager am = plugin.getArenaManager();
				int id = am.createArena();
				Arena a = am.getArena(id);
				String pfx = "arena." + id + ".";
	
				a.setName(data.getString(pfx + "name"));
				a.setTeams(data.getInt(pfx + "teams"));
				a.setMinPlayers(data.getInt(pfx + "min-players"));
				a.setMaxPlayers(data.getInt(pfx + "max-players"));
				
				int count = 0;
				Location[] loc = new Location[6];
				for (String str : data.getStringList(pfx + "team-lobby")) {
					loc[count] = SMeth.toLocation(str);
					count++;
				}
				a.setLobbySpawns(loc);
				
				a.setEnabled(data.getBoolean(pfx + "state"));
				
				for (String str : data.getStringList(pfx + "player-spawns")) {
					a.addPlayerSpawn(SMeth.toLocation(str));
				}
				for (String str : data.getStringList(pfx + "crystal-spawns")) {
					a.addCrystalSpawn(SMeth.toLocation(str));
				}
				for (String str : data.getStringList(pfx + "item-spawns")) {
					a.addItemSpawn(SMeth.toLocation(str));
				}
			}
		} catch (Exception e) { }
	}
	
}