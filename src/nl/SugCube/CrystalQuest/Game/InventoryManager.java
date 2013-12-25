package nl.SugCube.CrystalQuest.Game;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;

import nl.SugCube.CrystalQuest.CrystalQuest;
import nl.SugCube.CrystalQuest.SBA.SItem;

import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;

@SuppressWarnings("deprecation")
public class InventoryManager {

	public static CrystalQuest plugin;
	
	public final Float EXP_DEFAULT = 0F;
	public final Integer LEVEL_DEFAULT = 0;
	public final Double HEALTH_DEFAULT = 20D;
	public final Integer FOOD_DEFAULT = 20;
	public final Float SATURATION_DEFAULT = 12.8F;
	public final GameMode GAMEMODE_DEFAULT = GameMode.SURVIVAL;
	
	public HashMap<Player, ItemStack[]> invStorage = new HashMap<Player, ItemStack[]>();
	public HashMap<Player, ItemStack[]> armourStorage = new HashMap<Player, ItemStack[]>();
	public HashMap<Player, Integer> levelStorage = new HashMap<Player, Integer>();
	public HashMap<Player, Float> expStorage = new HashMap<Player, Float>();
	public HashMap<Player, Double> healthStorage = new HashMap<Player, Double>();
	public HashMap<Player, Integer> foodStorage = new HashMap<Player, Integer>();
	public HashMap<Player, Float> saturationStorage = new HashMap<Player, Float>();
	public HashMap<Player, GameMode> gamemodeStorage = new HashMap<Player, GameMode>();
	public HashMap<Player, Collection<PotionEffect>> potionStorage = new HashMap<Player, Collection<PotionEffect>>();
	
	public HashMap<Player, String> playerClass = new HashMap<Player, String>(); 
	
	public InventoryManager(CrystalQuest instance) {
		plugin = instance;
	}
	
	/**
	 * Removes a player from the playerClass HashMap.
	 * @param p (Player) The player to remove.
	 * @return void
	 */
	public void removePlayerClass(Player p) {
		this.playerClass.remove(p);
	}
	
	/**
	 * Adds a player to the playerClass HashMap.
	 * @param p (Player) The player to add.
	 * @param playerClass (String) The technical name of the class (defined in config.yml)
	 * @return void
	 */
	public void setPlayerClass(Player p, String playerClass) {
		this.playerClass.put(p, playerClass);
	}
	
	/**
	 * Saves the inventory contents.
	 * @param player (Player) The player from whom you want to save the inventory/
	 * @return void
	 */
	public void saveInventory(Player player) {
		invStorage.put(player, player.getInventory().getContents());
		armourStorage.put(player, player.getInventory().getArmorContents());
		levelStorage.put(player, player.getLevel());
		expStorage.put(player, player.getExp());
		healthStorage.put(player, player.getHealth());
		foodStorage.put(player, player.getFoodLevel());
		saturationStorage.put(player, player.getSaturation());
		gamemodeStorage.put(player, player.getGameMode());
		potionStorage.put(player, player.getActivePotionEffects());
	}
	
	/**
	 * Gives the player the kit of his/her class
	 * @param player (Player) The player to give the items to.
	 * @return void
	 */
	public void setClassInventory(Player player) {
		player.getInventory().clear();
		player.updateInventory();
		
		int count = 0;
		if (!playerClass.containsKey(player)) {
			boolean isNotOk = true;
			while (isNotOk && count < 10000) {
				Random ran = new Random();
				Set<String> set = plugin.getConfig().getConfigurationSection("kit").getKeys(false);
				List<String> list = new ArrayList<String>();
				for (String s : set) {
					list.add(s);
				}
				
				int random = ran.nextInt(list.size());
				String ranClass = list.get(random);
				if (Classes.hasPermission(player, ranClass)) {
					playerClass.put(player, ranClass);
					isNotOk = false;
				}
				count++;
			}
		}
		
		for (String s : plugin.getConfig().getStringList("kit." + playerClass.get(player) + ".items")) {
			
			//Checking if there is a piece of team-armour
			if (s.contains("team_helmet")) {
				ItemStack helmet = plugin.sh.toItemStack(s.replace("team_helmet", "leatherhelmet"));
				getColourLeather(helmet, plugin.am.getTeam(player));
				player.getInventory().setHelmet(helmet);
			} else if (s.contains("team_chestplate")) {
				ItemStack chestplate = plugin.sh.toItemStack(s.replace("team_chestplate", "leatherchestplate"));
				getColourLeather(chestplate, plugin.am.getTeam(player));
				player.getInventory().setChestplate(chestplate);
			} else if (s.contains("team_leggings")) {
				ItemStack leggings = plugin.sh.toItemStack(s.replace("team_leggings", "leatherleggings"));
				getColourLeather(leggings, plugin.am.getTeam(player));
				player.getInventory().setLeggings(leggings);
			} else if (s.contains("team_boots")) {
				ItemStack boots = plugin.sh.toItemStack(s.replace("team_boots", "leatherboots"));
				getColourLeather(boots, plugin.am.getTeam(player));
				player.getInventory().setBoots(boots);
			} else {
			
				ItemStack is = plugin.sh.toItemStack(s);
				
				//Auto-equip Armour
				int itemId = SItem.toId(is.getType());
				if (itemId >= 298 && itemId < 318) {
					if ((itemId + 2) % 4 == 0) {
						player.getInventory().setHelmet(is);
					} else if ((itemId + 1) % 4 == 0) {
						player.getInventory().setChestplate(is);
					} else if (itemId % 4 == 0) {
						player.getInventory().setLeggings(is);
					} else {
						player.getInventory().setBoots(is);
					}
				} else {
					//Add other items.
					player.getInventory().addItem(is);
				}
				
			}
		}
		
		if (player.hasPermission("crystalquest.admin") || player.hasPermission("crystalquest.staff") ||
				player.hasPermission("crystalquest.randomitem")) {
			boolean canCheck = true;
			ItemStack bonus = null;
			while (canCheck) {
				bonus = plugin.itemHandler.getRandomItem();
				if (bonus.getType() != Material.DIAMOND && bonus.getType() != Material.EMERALD &&
						bonus.getType() != Material.CHAINMAIL_CHESTPLATE) {
					canCheck = false;
				}
			}
			player.getInventory().addItem(bonus);
		}
		
		player.updateInventory();
		
	}
	
	/**
	 * Gives you the in-game inventory
	 * @param player (Player) The target player.
	 * @return void
	 */
	public void setInGameInventory(Player player) {
		saveInventory(player);
		player.getInventory().clear();
		player.getInventory().setArmorContents(null);
		
		player.setLevel(this.LEVEL_DEFAULT);
		player.setExp(this.EXP_DEFAULT);
		player.setHealth(this.HEALTH_DEFAULT);
		player.setFoodLevel(this.FOOD_DEFAULT);
		player.setSaturation(this.SATURATION_DEFAULT);
		player.setGameMode(this.GAMEMODE_DEFAULT);
		for (PotionEffect pe : player.getActivePotionEffects()) {
			player.removePotionEffect(pe.getType());
		}
	}
	
	/**
	 * Restores a player's inventory.
	 * @param player (Player) The target player.
	 * @return (boolean) True if all went ok, False if there was an error.
	 */
	public boolean restoreInventory(Player player) {
		try {
			player.getInventory().clear();
			player.getInventory().setContents(invStorage.get(player));
			player.getInventory().setArmorContents(armourStorage.get(player));
			player.setLevel(levelStorage.get(player));
			player.setExp(expStorage.get(player));
			player.setHealth(healthStorage.get(player));
			player.setFoodLevel(foodStorage.get(player));
			player.setSaturation(saturationStorage.get(player));
			player.setGameMode(gamemodeStorage.get(player));
			player.addPotionEffects(potionStorage.get(player));
			player.updateInventory();
			
			invStorage.remove(player);
			armourStorage.remove(player);
			levelStorage.remove(player);
			expStorage.remove(player);
			healthStorage.remove(player);
			foodStorage.remove(player);
			saturationStorage.remove(player);
			gamemodeStorage.remove(player);
			potionStorage.remove(player);
			playerClass.remove(player);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * Returns coloured leather armour (ItemStack)
	 * @param leatherArmour (ItemStack) The armour to dye.
	 * @param teamId (int) The ID of the player's team.
	 * @return (ItemStack) The coloured leather armour.
	 */
	public ItemStack getColourLeather(ItemStack leatherArmour, int teamId) {
		LeatherArmorMeta im = (LeatherArmorMeta) leatherArmour.getItemMeta();
		im.setColor(getTeamColour(teamId));
		leatherArmour.setItemMeta(im);
		return leatherArmour;
	}
	
	/**
	 * Returns the colour of the TeamID
	 * @param teamId (int) The ID of the team.
	 * @return (Color) The colour of the team.
	 */
	public Color getTeamColour(int teamId) {
		switch (teamId) {
		case 0: return Color.fromRGB(0, 255, 0);
		case 1: return Color.fromRGB(255, 140, 0);
		case 2: return Color.fromRGB(255, 255, 0);
		case 3: return Color.fromRGB(255, 0, 0);
		case 4: return Color.fromRGB(0, 255, 255);
		case 5: return Color.fromRGB(255, 0, 255);
		case 6: return Color.WHITE;
		case 7: return Color.BLACK;
		default: return null;
		}
	}
	
}