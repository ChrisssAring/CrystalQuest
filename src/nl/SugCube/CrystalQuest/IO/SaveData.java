package nl.SugCube.CrystalQuest.IO;

import java.util.ArrayList;
import java.util.List;

import nl.SugCube.CrystalQuest.CrystalQuest;
import nl.SugCube.CrystalQuest.Game.Arena;
import nl.SugCube.CrystalQuest.SBA.SMeth;

import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;

public class SaveData {

	public static CrystalQuest plugin;
	
	public SaveData(CrystalQuest instance) {
		plugin = instance;
	}
	
	/**
	 * Saves all the "arena-join-signs" to the data.yml
	 * @param void
	 * @return void
	 */
	public static void saveSigns() {
		FileConfiguration data = plugin.getData();
		
		data.set("signs", toStringListSign(plugin.signHandler.getSigns()));
		
		plugin.saveData();
	}
	
	/**
	 * Saves the lobby-spawn
	 * @param void
	 * @return  void
	 */
	public static void saveLobbySpawn() {
		try {
			plugin.getData().set("lobby-spawn", SMeth.toLocationString(plugin.am.getLobby()));
			plugin.saveData();
		} catch (Exception e) { }
	}
	
	/**
	 * Saves all the arena-data to the data.yml
	 * @param void
	 * @return void
	 */
	public static void saveArenas() {
		FileConfiguration data = plugin.getData();
		
		for (Arena a : plugin.am.arena) {
			String pfx = "arena." + a.getId() + ".";
			data.set(pfx + "name", a.getName());
			data.set(pfx + "teams", a.getTeamCount());
			data.set(pfx + "min-players", a.getMinPlayers());
			data.set(pfx + "max-players", a.getMaxPlayers());
			data.set(pfx + "team-lobby", toStringList(a.getLobbySpawns()));
			data.set(pfx + "state", a.isEnabled());
			data.set(pfx + "player-spawns", toStringList(a.getPlayerSpawns()));
			data.set(pfx + "crystal-spawns", toStringList(a.getCrystalSpawns()));
			data.set(pfx + "item-spawns", toStringList(a.getItemSpawns()));
		}
		
		plugin.saveData();
	}
	
	/**
	 * Converts a Location-array into a Stringarray to prepare it for data-storage.
	 * @param list (Location[]) Array of Locations
	 * @return (StringList) The list containing the location-strings
	 */
	public static List<String> toStringList(Location[] list) {
		List<String> stringList = new ArrayList<String>();
		if (list != null) {
			for (Location loc : list) {
				if (loc != null) {
					stringList.add(SMeth.toLocationString(loc));
				}
			}
		}
		return stringList;
	}
	
	/**
	 * Converts a Location-list into a Stringarray to prepare it for data-storage.
	 * @param list (LocationList) List of Locations
	 * @return (StringList) The list containing the location-strings
	 */
	public static List<String> toStringList(List<Location> list) {
		List<String> stringList = new ArrayList<String>();
		for (Location loc : list) {
			stringList.add(SMeth.toLocationString(loc));
		}
		return stringList;
	}
	
	/**
	 * Converts a Sign-list into a Stringarray to prepare it for data-storage.
	 * @param list (SignList) List of Locations
	 * @return (StringList) The list containing the location-strings
	 */
	public static List<String> toStringListSign(List<Sign> list) {
		List<String> stringList = new ArrayList<String>();
		for (Sign s : list) {
			if (s != null) {
				Location loc = s.getLocation();
				stringList.add(SMeth.toLocationStringSign(loc));
			}
		}
		return stringList;
	}
	
}