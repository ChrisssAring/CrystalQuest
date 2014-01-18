package nl.SugCube.CrystalQuest;

import java.util.ArrayList;
import java.util.List;

import nl.SugCube.CrystalQuest.Game.Arena;
import nl.SugCube.CrystalQuest.Game.ArenaManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Sign;

public class SignHandler {

	public static CrystalQuest plugin;
	public static ArenaManager am;
	
	private List<Location> signs = new ArrayList<Location>();
	
	public SignHandler(CrystalQuest instance, ArenaManager manager) {
		plugin = instance;
		am = manager;
	}
	
	/**
	 * Start a sign updater. When signs get stuck, this will fix it.
	 * @param seconds (Integer) The amount of seconds between the updates.
	 * @return void
	 */
	public void startSignUpdater(int seconds) {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			public void run() {
				updateSigns();
			}
		}, seconds * 20, seconds * 20);
	}
	
	/**
	 * Updates all signs in the sign list
	 * @param void
	 * @return void
	 */
	public void updateSigns() {
		if (this.signs.size() == 0)
			return;

		for (Location loc : this.signs) {
			try {
				Sign s = (Sign) loc.getBlock().getState();
				Arena a = am.getArena(s.getLine(1));
				ChatColor color = null;
				if (a.isEnabled()) {
					if (a.isCounting()) {
						color = ChatColor.GOLD;
						s.setLine(3, "Starting");
					} else if (!a.isEndGame() && a.isInGame()) {
						color = ChatColor.DARK_RED;
						s.setLine(3, "In Game");
					} else if (a.isInGame()) {
						color = ChatColor.DARK_PURPLE;
						s.setLine(3, "Restarting");
					} else {
						color = ChatColor.GREEN;
						s.setLine(3, "Lobby");
					}
					s.setLine(2, color + "" + a.getPlayers().size() + "/" + a.getMaxPlayers());
					s.update();
				} else {
					s.setLine(2, "");
					s.setLine(3, ChatColor.DARK_RED + "Disabled");
					s.update();
				}
			} catch (Exception e) { }
		}
		
		plugin.menuSA.updateMenu();
		plugin.menuPT.updateMenus();
	}
	
	/**
	 * Gets a list of all the lobby-signs
	 * @param void
	 * @return (SignList) All Lobby-signs
	 */
	public List<Location> getSigns() {
		return this.signs;
	}
	
}