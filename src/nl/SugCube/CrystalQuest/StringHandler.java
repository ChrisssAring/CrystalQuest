package nl.SugCube.CrystalQuest;

import nl.SugCube.CrystalQuest.SBA.SEnch;
import nl.SugCube.CrystalQuest.SBA.SItem;

import org.bukkit.inventory.ItemStack;

public class StringHandler {

	public static CrystalQuest plugin;
	
	/**
	 * CONSTRUCT
	 * Passes through the actual plugin.
	 * @param instance (CrystalQuest) The instance of the plugin.
	 */
	public StringHandler(CrystalQuest instance) {
		plugin = instance;
	}
	
	/**
	 * Get the name of a block without the capitals and underscores
	 * @param string (String) Inputstring (Material-name)
	 * @return (String) The friendly name
	 */
	public String getFriendlyItemName(String string) {
		String first = string.substring(0, 1).toUpperCase();
		String last = string.substring(1, string.length()).toLowerCase();
		string = first + last;
		return string.replaceAll("_", " ");
	}
	
	/**
	 * Turns a string into an actual ItemStack
	 * @param s (String) The string to convert
	 * @return (ItemStack) The item the string represents. Returns null if the string couldn't be parsed.
	 */
	public ItemStack toItemStack(String s) {
		ItemStack is = null;
		
		try {			
			String[] item = s.split(",");
			
			if (item.length == 1) {
				is = new ItemStack(SItem.toMaterial(item[0]), 1);
			} else if (item.length == 2) {
				is = new ItemStack(SItem.toMaterial(item[0]), Integer.parseInt(item[1]));
			} else if (item.length >= 3) {
				is = new ItemStack(SItem.toMaterial(item[0]), Integer.parseInt(item[1]), Short.parseShort(item[2]));
			}
			if (item.length >= 5) {
				is.addUnsafeEnchantment(SEnch.toEnchantment(item[3]), Integer.parseInt(item[4]));
			}
			if (item.length >= 7) {
				is.addUnsafeEnchantment(SEnch.toEnchantment(item[5]), Integer.parseInt(item[6]));
			}
			if (item.length >= 9) {
				is.addUnsafeEnchantment(SEnch.toEnchantment(item[7]), Integer.parseInt(item[8]));
			}
			if (item.length >= 11) {
				is.addUnsafeEnchantment(SEnch.toEnchantment(item[9]), Integer.parseInt(item[10]));
			}
			if (item.length >= 13) {
				is.addUnsafeEnchantment(SEnch.toEnchantment(item[11]), Integer.parseInt(item[12]));
			}
			if (item.length >= 15) {
				is.addUnsafeEnchantment(SEnch.toEnchantment(item[13]), Integer.parseInt(item[14]));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return is;
	}
	
}