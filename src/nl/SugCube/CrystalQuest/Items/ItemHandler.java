package nl.SugCube.CrystalQuest.Items;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import nl.SugCube.CrystalQuest.Broadcast;
import nl.SugCube.CrystalQuest.CrystalQuest;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ItemHandler {

	public static CrystalQuest plugin;
	private List<ItemStack> items;
	
	public ItemHandler(CrystalQuest instance) {
		plugin = instance;
		items = new ArrayList<ItemStack>();
	}
	
	/**
	 * Adds all items to the item-list
	 * @param void
	 * @return void
	 */
	public void addAllItems() {
		addCrystalShard();
		addSmallCrystal();
		addShinyCrystal();
		addGrenade();
		addHealthPotion();
		addRailgun();
		addSpeedPotion();
		addBlooper();
		addGoldenApple();
		addFireFlower();
		addStrengthPotion();
		addShield();
		addWither();
		addCreeperEgg();
		addHammer();
		addLandmine();
		addWolf();
		addGlue();
		addLightning();
	}
	
	/**
	 * Returns a list containing all items in the game.
	 * @param void
	 * @return (ItemStackList)
	 */
	public List<ItemStack> getAllItems() {
		return this.items;
	}
	
	/**
	 * Gets a random item from the item-list
	 * @param void
	 * @return (ItemStack) A random item.
	 */
	public ItemStack getRandomItem() {
		Random ran = new Random();
		ItemStack is = this.items.get(ran.nextInt(this.items.size()));
		if (is.getType() == Material.EGG) {
			is.setAmount(ran.nextInt(3) + 1);
		} else if (is.getType() == Material.IRON_HOE) {
			is.setAmount(ran.nextInt(2) + 1);
		} else if (is.getType() == Material.RED_ROSE) {
			is.setAmount(ran.nextInt(3) + 1);
		}
		return is;
	}
	
	/**
	 * Add the Lightning-item
	 * @param void
	 * @return void
	 */
	public void addLightning() {
		ItemStack is = new ItemStack(Material.FEATHER, 1);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(Broadcast.get("items.lightning-bolt"));
		is.setItemMeta(im);
		this.items.add(is);
	}
	
	/**
	 * Add the Glue-item
	 * @param void
	 * @return void
	 */
	public void addGlue() {
		ItemStack is = new ItemStack(Material.SLIME_BALL, 1);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(Broadcast.get("items.glue"));
		is.setItemMeta(im);
		this.items.add(is);
	}
	
	/**
	 * Add the Landmine-item
	 * @param void
	 * @return void
	 */
	public void addLandmine() {
		ItemStack is = new ItemStack(Material.STONE_PLATE, 1);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(Broadcast.get("items.landmine"));
		is.setItemMeta(im);
		this.items.add(is);
	}
	
	/**
	 * Add the Wolf-item
	 * @param void
	 * @return void
	 */
	public void addWolf() {
		ItemStack is = new ItemStack(Material.BONE, 1);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(Broadcast.get("items.wolf") + ChatColor.RED + " <3");
		is.setItemMeta(im);
		this.items.add(is);
	}
	
	/**
	 * Add the CreeperEgg-item to the items-list
	 * @param void
	 * @return void
	 */
	public void addCreeperEgg() {
		ItemStack is = new ItemStack(Material.MONSTER_EGG, 1, (short) 50);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(ChatColor.DARK_GREEN + "Creeper Egg");
		is.setItemMeta(im);
		this.items.add(is);
	}
	
	/**
	 * Adds the Hammer-item to the items-list
	 * @param void
	 * @return void
	 */
	public void addHammer() {
		ItemStack is = new ItemStack(Material.DIAMOND_AXE, 1, (short) 1561);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(Broadcast.get("items.hammer"));
		is.setItemMeta(im);
		is.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 3);
		is.addUnsafeEnchantment(Enchantment.KNOCKBACK, 10);
		this.items.add(is);
	}
	
	/**
	 * Adds the Wither-item to the items-list
	 * @param void
	 * @return void
	 */
	public void addWither() {
		ItemStack is = new ItemStack(Material.SKULL_ITEM, 1, (short) 1);
		SkullMeta im = (SkullMeta) is.getItemMeta();
		im.setDisplayName(Broadcast.get("items.wither"));
		is.setItemMeta(im);
		this.items.add(is);
	}
	
	/**
	 * Adds the Shield-item to the items-list
	 * @param void
	 * @return void
	 */
	public void addShield() {
		ItemStack is = new ItemStack(Material.CHAINMAIL_CHESTPLATE, 1);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(Broadcast.get("items.shield"));
		is.setItemMeta(im);
		this.items.add(is);
	}
	
	/**
	 * Adds the StrengthPotion-item to the items-list
	 * @param void
	 * @return void
	 */
	public void addStrengthPotion() {
		PotionEffect effect = new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 240, 0);
		ItemStack is = new ItemStack(Material.POTION, 1, (short) 8265);
		PotionMeta im = (PotionMeta) is.getItemMeta();
		im.addCustomEffect(effect, true);
		im.setDisplayName(Broadcast.get("items.strength"));
		is.setItemMeta(im);
		this.items.add(is);
	}
	
	/**
	 * Adds the FireFlower-item to the items-list
	 * @param void
	 * @return void
	 */
	public void addFireFlower() {
		ItemStack is = new ItemStack(Material.RED_ROSE, 1);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(Broadcast.get("items.flower"));
		is.setItemMeta(im);
		this.items.add(is);
	}
	
	/**
	 * Adds the GoldenApple-item to the items-list
	 * @param void
	 * @return void
	 */
	public void addGoldenApple() {
		ItemStack is = new ItemStack(Material.GOLDEN_APPLE, 1);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(Broadcast.get("items.apple"));
		is.setItemMeta(im);
		this.items.add(is);
	}
	
	/**
	 * Adds the Railgun-item to the items-list
	 * @param void
	 * @return void
	 */
	public void addRailgun() {
		ItemStack is = new ItemStack(Material.IRON_HOE, 1);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(Broadcast.get("items.railgun"));
		is.setItemMeta(im);
		this.items.add(is);
	}
	
	/**
	 * Adds the Blooper-item to the items-list
	 * @param void
	 * @return void 
	 */
	public void addBlooper() {
		ItemStack is = new ItemStack(Material.INK_SACK, 1, (short) 0);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(Broadcast.get("items.blooper"));
		is.setItemMeta(im);
		this.items.add(is);
	}
	
	/**
	 * Adds the SpeedPotion-item to the items-list
	 * @param void
	 * @return void
	 */
	public void addSpeedPotion() {
		PotionEffect effect = new PotionEffect(PotionEffectType.SPEED, 360, 1);
		ItemStack is = new ItemStack(Material.POTION, 1, (short) 8226);
		PotionMeta im = (PotionMeta) is.getItemMeta();
		im.addCustomEffect(effect, true);
		im.setDisplayName(Broadcast.get("items.speed"));
		is.setItemMeta(im);
		this.items.add(is);
	}
	
	/**
	 * Adds the HealthPotion-item to the items-list
	 * @param void
	 * @return void
	 */
	public void addHealthPotion() {
		ItemStack is = new ItemStack(Material.POTION, 1, (short) 16453);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(Broadcast.get("items.health"));
		is.setItemMeta(im);
		this.items.add(is);
	}
	
	/**
	 * Adds the Grenade-item to the items-list
	 * @param void
	 * @return void
	 */
	public void addGrenade() {
		ItemStack is = new ItemStack(Material.EGG, 1);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(Broadcast.get("items.grenade"));
		is.setItemMeta(im);
		items.add(is);
	}
	
	/**
	 * Adds the ShinyCrystal-item to the items-list
	 * @param void
	 * @return void
	 */
	public void addShinyCrystal() {
		ItemStack is = new ItemStack(Material.DIAMOND, 1);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(Broadcast.get("items.shiny-crystal"));
		is.setItemMeta(im);
		is.addUnsafeEnchantment(Enchantment.SILK_TOUCH, 1);
		items.add(is);
	}
	
	/**
	 * Adds the SmallCrystal-item to the items-list
	 * @param void
	 * @return void
	 */
	public void addSmallCrystal() {
		ItemStack is = new ItemStack(Material.DIAMOND, 1);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(Broadcast.get("items.small-crystal"));
		is.setItemMeta(im);
		items.add(is);
	}
	
	/**
	 * Adds the CrystalShard-item to the items-list
	 * @param void
	 * @return void
	 */
	public void addCrystalShard() {
		ItemStack is = new ItemStack(Material.EMERALD, 1);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(Broadcast.get("items.crystal-shard"));
		is.setItemMeta(im);
		items.add(is);
	}
	
	/**
	 * Gets a specific item using the name
	 * @param name (String) The name of the item
	 * @return (ItemStack) The itemstack the item represents
	 */
	public ItemStack getItemByName(String name) {
		for (ItemStack item : this.items) {
			if (item.getItemMeta().getDisplayName().contains(name)) {
				return item;
			}
		}
		return null;
	}
	
}