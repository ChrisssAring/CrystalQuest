package nl.SugCube.CrystalQuest.InventoryMenu;

import java.util.ArrayList;
import java.util.List;

import nl.SugCube.CrystalQuest.CrystalQuest;
import nl.SugCube.CrystalQuest.Game.Arena;
import nl.SugCube.CrystalQuest.Game.ArenaManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SpectateArena {
	
	public static CrystalQuest plugin;
	private Inventory menu;
	
	public SpectateArena(CrystalQuest instance) {
		plugin = instance;
	}
	
	/**
	 * Opens the spectate-menu
	 * @param p (Player) The player to show the menu to
	 */
	public void showMenu(Player p) {
		this.updateMenu();
		p.openInventory(menu);
	}
	
	/**
	 *	Updates the menu 
	 */
	public void updateMenu() {
		ArenaManager am = plugin.getArenaManager();
		int arenas = am.getArenas().size();
		
		if (menu == null) {
			menu = Bukkit.createInventory(null, 9, "Spectate an arena");
		}
		
		if (arenas > menu.getSize()) {
			int invSize = 9;
			for (int i = 54; i >= 9; i -= 9) {
				if (arenas <= i) {
					invSize = i;
				}
			}
			
			if (menu != null) {
				if (menu.getSize() < arenas) {
					menu = Bukkit.createInventory(null, invSize, "Spectate an arena");
				}
			} else {
				menu = Bukkit.createInventory(null, invSize, "Spectate an arena");
			}
		}
		
		menu.clear();
		for (Arena a : am.getArenas()) {
			if (a.isEnabled()) {
				menu.addItem(this.getItem(a));
			}
		}
	}
	
	/**
	 * Gives you the item stack representing the arena.
	 * @param a (Arena) The arena to retrieve the data from.
	 * @return (ItemStack) The wool representing the arena.
	 */
	public ItemStack getItem(Arena a) {
		ItemStack is;
		String status;
		
		if (a.isEndGame()) {
			is = new ItemStack(Material.WOOL, 1, (short) 2);
			status = ChatColor.DARK_PURPLE + "Restarting";
		} else if (a.isInGame()) {
			is = new ItemStack(Material.WOOL, 1, (short) 14);
			status = ChatColor.DARK_RED + "In Game";
		} else if (a.isCounting()) {
			is = new ItemStack(Material.WOOL, 1, (short) 1);
			status = ChatColor.GOLD + "Starting";
		} else {
			is = new ItemStack(Material.WOOL, 1, (short) 5);
			status = ChatColor.GREEN + "In Lobby";
		}
		
		String displayName = ChatColor.AQUA + "Spectate " + a.getName();
		List<String> lore = new ArrayList<String>();
		lore.add(ChatColor.YELLOW + "Currently " + status);
		lore.add(ChatColor.YELLOW + "Players: " + a.getPlayers().size() + "/" + a.getMaxPlayers());
		
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(displayName);
		im.setLore(lore);
		is.setItemMeta(im);
		
		return is;
	}

}
