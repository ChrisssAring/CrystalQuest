package nl.SugCube.CrystalQuest.Items;

import nl.SugCube.CrystalQuest.Broadcast;
import nl.SugCube.CrystalQuest.SBA.SDura;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public enum WandType {
	
	MAGMA(0, Material.GOLD_HOE, "wand.magma", SDura.GOLD.getDurability(), "wand.regen-fire"),
	TELEPORT(1, Material.STONE_HOE, "wand.teleport", SDura.STONE.getDurability(), "wand.regen-teleport"),
	HEAL(2, Material.WOOD_HOE, "wand.heal", SDura.WOOD.getDurability(), "wand.regen-healing"),
	FREEZE(3, Material.DIAMOND_HOE, "wand.freeze", SDura.DIAMOND.getDurability(), "wand.regen-ice"),
	WITHER(4, Material.STONE_AXE, "wand.wither", SDura.STONE.getDurability(), "wand.regen-wither");
	
	private final int id;
	private final Material material;
	private final String name;
	private final short durability;
	private final String regenConfig;
	
	private WandType(int id, Material material, String name, short durability, String regenConfig) {
		this.id = id;
		this.material = material;
		this.name = name;
		this.durability = durability;
		this.regenConfig = regenConfig;
	}

	/**
	 * Gets the config-node from which the regen-time can be taken from.
	 * @return (String) config-node.
	 */
	public String getRegenConfig() {
		return this.regenConfig;
	}
	
	/**
	 * Sets the displayname of the wand
	 * @param is (ItemStack) The stack to apply the name to.
	 * @param type (WandType) The type of wand.
	 * @return (ItemStack) The renamed ItemStack.
	 */
	public ItemStack setDisplayName(ItemStack is, WandType type) {
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(Broadcast.get(type.getDisplayName()));
		is.setItemMeta(im);
		return is;
	}
	
	/**
	 * Gets the displayname of the WandType.
	 * @return (String) The Displayname
	 */
	public String getDisplayName() {
		return name;
	}
	
	/**
	 * Gets the ID of the WandType.
	 * @return (int) The ID
	 */
	public int getId() {
		return id;
	}

	/**
	 * Gets the material the wand is made of.
	 * @return (Material) The Material.
	 */
	public Material getMaterial() {
		return material;
	}

	/**
	 * Gets the maximum durability of the material used for the wand.
	 * @return (short) Max data value.
	 */
	public short getDurability() {
		return durability;
	}
	
}