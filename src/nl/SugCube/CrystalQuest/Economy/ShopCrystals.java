package nl.SugCube.CrystalQuest.Economy;

import java.util.ArrayList;
import java.util.List;

import nl.SugCube.CrystalQuest.CrystalQuest;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ShopCrystals implements Listener {

	public static CrystalQuest plugin;
	public static Economy economy;
	
	public ShopCrystals(CrystalQuest instance, Economy eco) {
		plugin = instance;
		economy = eco;
	}
	
	/**
	 * Updates the item's names and lores.
	 * @param p (Player) The player who has opened the shop.
	 * @param inv (Inventory) The inventory to update.
	 */
	public void updateMenu(Player p, Inventory inv) {
		ItemStack[] contents = inv.getContents();
		
		//STATUS BAR
		contents[8] = getItemStatusExp(p);
		contents[17] = getItemStatusSmash(p);
		contents[35] = getItemStatusWin(p);
		contents[44] = getItemStatusKill(p);
		
		//ITEMS TO BUY
		contents[11] = getItemBuyExp(p);
		contents[13] = getItemBuySmash(p);
		contents[29] = getItemBuyWin(p);
		contents[31] = getItemBuyKill(p);
		
		//NAVIGATION
		contents[45] = getItemMainMenu();
		contents[49] = economy.getItemBalance(p);
		
		inv.setContents(contents);
	}
	
	/**
	 * Shows the crystal menu of the CrystalQuest-Shop.
	 * @param p (Player) The player to show the menu to.
	 */
	public void showMenu(Player p) {
		p.closeInventory();
		
		Inventory inv = Bukkit.createInventory(null, 54, ChatColor.LIGHT_PURPLE + "CrystalQuest Shop:" + ChatColor.GOLD +
				" Crystals");
		
		updateMenu(p, inv);
		p.openInventory(inv);
	}
	
	/*
	 * Inventory handling for the main menu
	 */
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		Inventory inv = e.getInventory();
		if (inv.getName().equalsIgnoreCase(ChatColor.LIGHT_PURPLE + "CrystalQuest Shop:" + ChatColor.GOLD + " Crystals")) {
			
			if (e.getCurrentItem() != null) {
				ItemStack item = e.getCurrentItem();
				
				if (item.hasItemMeta()) {
					ItemMeta im = item.getItemMeta();
					if (im.hasDisplayName()) {
						String name = im.getDisplayName();
						Player p = (Player) e.getWhoClicked();
						
						/*
						 * MAIN MENU
						 */
						if (name.equalsIgnoreCase(ChatColor.GREEN + "Main Menu")) {
							economy.getMainMenu().showMenu((Player) e.getWhoClicked());
						}
						/*
						 * BUY EXP
						 */
						else if (name.equalsIgnoreCase(ChatColor.GREEN + "[Buy] " + ChatColor.GREEN + "Xp-Bonus")) {
							buyClass(p, "xp", e.getInventory());
						}
						/*
						 * BUY SMASH
						 */
						else if (name.equalsIgnoreCase(ChatColor.GREEN + "[Buy] " + ChatColor.LIGHT_PURPLE + "Smash-Bonus")) {
							buyClass(p, "smash", e.getInventory());
						}
						/*
						 * BUY WIN
						 */
						else if (name.equalsIgnoreCase(ChatColor.GREEN + "[Buy] " + ChatColor.YELLOW + "Win-Cash")) {
							buyClass(p, "win", e.getInventory());
						}
						/*
						 * BUY BLOOD
						 */
						else if (name.equalsIgnoreCase(ChatColor.GREEN + "[Buy] " + ChatColor.RED + "Blood Diamonds")) {
							buyClass(p, "blood", e.getInventory());
						}
						
						e.setCancelled(true);
					}
				}
			}
			
			e.setCancelled(true);
		}
	}
	
	/**
	 * Let the player buy the chosen crystal-upgrade.
	 * @param p (Player) The buyer.
	 * @param Class (String) The upgrade the player buys.
	 * @param inv (Inventory) The inventory-instance of the shop.
	 * @return (boolean) True if able to, false if he/she couldn't buy the upgrade.
	 */
	public boolean buyClass(Player p, String Class, Inventory inv) {
		int level = economy.getLevel(p, Class, "crystals") + 1;
		Balance bal = economy.getBalance();
		
		if (bal.canAfford(p, economy.getCosts(level))) {
			bal.addCrystals(p, -economy.getCosts(level), false);
			updateMenu(p, inv);
			plugin.getData().set("economy.crystals." + p.getName() + "." + Class, level);
			showMenu(p);
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Gets the item showing BLOOD buyable item
	 * @return (ItemStack)
	 */
	public ItemStack getItemBuyKill(Player p) {
		ItemStack is = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		ItemMeta im = is.getItemMeta();
		if (economy.getLevel(p, "blood", "crystals") < 5) {
			im.setDisplayName(ChatColor.GREEN + "[Buy] " + ChatColor.RED + "Blood Diamonds");
		} else {
			im.setDisplayName(ChatColor.RED + "[MAX] " + ChatColor.RED + "Blood Diamonds");
		}
		List<String> lore = new ArrayList<String>();
		int level = 0;
		if (plugin.getData().isSet("economy.crystals." + p.getName() + ".blood")) {
			level = plugin.getData().getInt("economy.crystals." + p.getName() + ".blood");
		} else {
			plugin.getData().set("economy.crystals." + p.getName() + ".blood", 0);
		}
		lore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "Upgrade to: " + ChatColor.GREEN + "Lvl " + (level + 1));
		
		lore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "Double chance: " + ChatColor.GREEN + "+20%");
		lore.add("");
		lore.add(ChatColor.RED + "Price: " + ChatColor.GOLD + economy.getCosts(level + 1));
		im.setLore(lore);
		is.setItemMeta(im);
		return is;
	}
	
	/**
	 * Gets the item showing WIN buyable item
	 * @return (ItemStack)
	 */
	public ItemStack getItemBuyWin(Player p) {
		ItemStack is = new ItemStack(Material.GOLD_INGOT, 1);
		ItemMeta im = is.getItemMeta();
		if (economy.getLevel(p, "win", "crystals") < 5) {
			im.setDisplayName(ChatColor.GREEN + "[Buy] " + ChatColor.YELLOW + "Win-Cash");
		} else {
			im.setDisplayName(ChatColor.RED + "[MAX] " + ChatColor.YELLOW + "Win-Cash");
		}
		List<String> lore = new ArrayList<String>();
		int level = 0;
		if (plugin.getData().isSet("economy.crystals." + p.getName() + ".win")) {
			level = plugin.getData().getInt("economy.crystals." + p.getName() + ".win");
		} else {
			plugin.getData().set("economy.crystals." + p.getName() + ".win", 0);
		}
		lore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "Upgrade to: " + ChatColor.GREEN + "Lvl " + (level + 1));
		
		lore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "Cash on win: " + ChatColor.GREEN + "+10%");
		lore.add("");
		lore.add(ChatColor.RED + "Price: " + ChatColor.GOLD + economy.getCosts(level + 1));
		im.setLore(lore);
		is.setItemMeta(im);
		return is;
	}
	
	/**
	 * Gets the item showing SMASH buyable item
	 * @return (ItemStack)
	 */
	public ItemStack getItemBuySmash(Player p) {
		ItemStack is = new ItemStack(Material.NETHER_STAR, 1);
		ItemMeta im = is.getItemMeta();
		if (economy.getLevel(p, "smash", "crystals") < 5) {
			im.setDisplayName(ChatColor.GREEN + "[Buy] " + ChatColor.LIGHT_PURPLE + "Smash-Bonus");
		} else {
			im.setDisplayName(ChatColor.RED + "[MAX] " + ChatColor.LIGHT_PURPLE + "Smash-Bonus");
		}
		List<String> lore = new ArrayList<String>();
		int level = 0;
		if (plugin.getData().isSet("economy.crystals." + p.getName() + ".smash")) {
			level = plugin.getData().getInt("economy.crystals." + p.getName() + ".smash");
		} else {
			plugin.getData().set("economy.crystals." + p.getName() + ".smash", 0);
		}
		lore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "Upgrade to: " + ChatColor.GREEN + "Lvl " + (level + 1));
		
		lore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "Double chance: " + ChatColor.GREEN + "+10%");
		lore.add("");
		lore.add(ChatColor.RED + "Price: " + ChatColor.GOLD + economy.getCosts(level + 1));
		im.setLore(lore);
		is.setItemMeta(im);
		return is;
	}
	
	/**
	 * Gets the item showing XP buyable item
	 * @return (ItemStack)
	 */
	public ItemStack getItemBuyExp(Player p) {
		ItemStack is = new ItemStack(Material.EXP_BOTTLE, 1);
		ItemMeta im = is.getItemMeta();
		if (economy.getLevel(p, "xp", "crystals") < 5) {
			im.setDisplayName(ChatColor.GREEN + "[Buy] " + ChatColor.GREEN + "Xp-Bonus");
		} else {
			im.setDisplayName(ChatColor.RED + "[MAX] " + ChatColor.GREEN + "Xp-Bonus");
		}
		List<String> lore = new ArrayList<String>();
		int level = 0;
		if (plugin.getData().isSet("economy.crystals." + p.getName() + ".xp")) {
			level = plugin.getData().getInt("economy.crystals." + p.getName() + ".xp");
		} else {
			plugin.getData().set("economy.crystals." + p.getName() + ".xp", 0);
		}
		lore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "Upgrade to: " + ChatColor.GREEN + "Lvl " + (level + 1));
		
		lore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "Crystals: " + ChatColor.GREEN + "+1");
		lore.add("");
		lore.add(ChatColor.RED + "Price: " + ChatColor.GOLD + economy.getCosts(level + 1));
		im.setLore(lore);
		is.setItemMeta(im);
		return is;
	}
	
	/**
	 * Gets the item showing Kill status
	 * @return (ItemStack)
	 */
	public ItemStack getItemStatusKill(Player p) {
		ItemStack is = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(ChatColor.RED + "Blood Diamonds");
		List<String> lore = new ArrayList<String>();
		int level = 0;
		if (plugin.getData().isSet("economy.crystals." + p.getName() + ".blood")) {
			level = plugin.getData().getInt("economy.crystals." + p.getName() + ".blood");
		} else {
			plugin.getData().set("economy.crystals." + p.getName() + ".blood", 0);
		}
		String multiplier = "" + Multipliers.getMultiplier("blood", level, true);
		lore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "Current level: " + ChatColor.GREEN + "Lvl " + level);
		lore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "Chance double cash: " + ChatColor.GREEN + 
				multiplier.replace(".0", "") + "%");
		lore.add("");
		lore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "Increase the chance of getting");
		lore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "double crystals (balance) when you");
		lore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "kill someone");
		im.setLore(lore);
		is.setItemMeta(im);
		return is;
	}
	
	/**
	 * Gets the item showing Crystal-Win status
	 * @return (ItemStack)
	 */
	public ItemStack getItemStatusWin(Player p) {
		ItemStack is = new ItemStack(Material.GOLD_INGOT, 1);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(ChatColor.YELLOW + "Win-Cash");
		List<String> lore = new ArrayList<String>();
		int level = 0;
		if (plugin.getData().isSet("economy.crystals." + p.getName() + ".win")) {
			level = plugin.getData().getInt("economy.crystals." + p.getName() + ".win");
		} else {
			plugin.getData().set("economy.crystals." + p.getName() + ".win", 0);
		}
		String multiplier = "" + Multipliers.getMultiplier("win", level, true);
		lore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "Current level: " + ChatColor.GREEN + "Lvl " + level);
		lore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "Win-Cash multiplier: " + ChatColor.GREEN + 
				multiplier.substring(0, 3) + "%");
		lore.add("");
		lore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "Increase the amount of crystals");
		lore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "you get when you win a quest.");
		im.setLore(lore);
		is.setItemMeta(im);
		return is;
	}
	
	/**
	 * Gets the item showing Crystal-Smash status
	 * @return (ItemStack)
	 */
	public ItemStack getItemStatusSmash(Player p) {
		ItemStack is = new ItemStack(Material.NETHER_STAR, 1);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(ChatColor.LIGHT_PURPLE + "Smash-Bonus");
		List<String> lore = new ArrayList<String>();
		int level = 0;
		if (plugin.getData().isSet("economy.crystals." + p.getName() + ".smash")) {
			level = plugin.getData().getInt("economy.crystals." + p.getName() + ".smash");
		} else {
			plugin.getData().set("economy.crystals." + p.getName() + ".smash", 0);
		}
		String multiplier = "" + Multipliers.getMultiplier("smash", level, true);
		lore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "Current level: " + ChatColor.GREEN + "Lvl " + level);
		lore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "Chance double crystals: " + ChatColor.GREEN + 
				multiplier.replace(".0", "") + "%");
		lore.add("");
		lore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "Increase the chance of");
		lore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "doubling the crystals you");
		lore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "get when you smash a crystal.");
		im.setLore(lore);
		is.setItemMeta(im);
		return is;
	}
	
	/**
	 * Gets the item showing Crystal-XP status
	 * @return (ItemStack)
	 */
	public ItemStack getItemStatusExp(Player p) {
		ItemStack is = new ItemStack(Material.EXP_BOTTLE, 1);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(ChatColor.GREEN + "Xp-Bonus");
		List<String> lore = new ArrayList<String>();
		int level = 0;
		if (plugin.getData().isSet("economy.crystals." + p.getName() + ".xp")) {
			level = plugin.getData().getInt("economy.crystals." + p.getName() + ".xp");
		} else {
			plugin.getData().set("economy.crystals." + p.getName() + ".xp", 0);
		}
		String multiplier = "" + Multipliers.getMultiplier("xp", level, false);
		lore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "Current level: " + ChatColor.GREEN + "Lvl " + level);
		lore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "Crystals per XP-Level: " + ChatColor.GREEN + 
				multiplier.replace(".0", ""));
		lore.add("");
		lore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "Increase the amount of crystals");
		lore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "you get when you reach 1 LVL.");
		im.setLore(lore);
		is.setItemMeta(im);
		return is;
	}
	
	/**
	 * Gets the item linking to the Main Menu
	 * @return (ItemStack)
	 */
	public ItemStack getItemMainMenu() {
		ItemStack is = new ItemStack(Material.ARROW, 1);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(ChatColor.GREEN + "Main Menu");
		is.addUnsafeEnchantment(Enchantment.SILK_TOUCH, 1);
		List<String> lore = new ArrayList<String>();
		lore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "Go back to the Main Menu!");
		im.setLore(lore);
		is.setItemMeta(im);
		return is;
	}
}