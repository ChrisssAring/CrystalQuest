package nl.SugCube.CrystalQuest.Listeners;

import java.util.Random;

import nl.SugCube.CrystalQuest.Broadcast;
import nl.SugCube.CrystalQuest.CrystalQuest;
import nl.SugCube.CrystalQuest.Teams;
import nl.SugCube.CrystalQuest.Game.Arena;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryListener implements Listener {

	public static CrystalQuest plugin;
	
	public InventoryListener(CrystalQuest instance) {
		plugin = instance;
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		
		if (plugin.am.isInGame((Player) e.getWhoClicked())) {
			e.setCancelled(true);
		}
		
		if (e.getInventory().getName() == "Pick Team") {
			if (e.getCurrentItem() != null) {
				if (e.getCurrentItem().getAmount() > 0) {
					try {
						e.setCancelled(true);
						Player player = (Player) e.getWhoClicked();
						player.closeInventory();
						
						Arena a = plugin.am.getArena(e.getCurrentItem().getAmount() - 1);
						int teamId = 0;
						String displayName = "";
						
						if (e.getCurrentItem().hasItemMeta()) {
							if (e.getCurrentItem().getItemMeta().hasDisplayName()) {
								displayName = e.getCurrentItem().getItemMeta().getDisplayName();
							}
						}
						
						if (displayName.contains(Teams.GREEN_NAME)) {
							teamId = 0;
						} else if (displayName.contains(Teams.ORANGE_NAME)) {
							teamId = 1;
						} else if (displayName.contains(Teams.YELLOW_NAME)) {
							teamId = 2;
						} else if (displayName.contains(Teams.RED_NAME)) {
							teamId = 3;
						} else if (displayName.contains(Teams.BLUE_NAME)) {
							teamId = 4;
						} else if (displayName.contains(Teams.MAGENTA_NAME)) {
							teamId = 5;
						} else if (displayName.contains("Random Team")) {
							try {
								if (a.getSmallestTeams().size() > 0) {
									Random ran = new Random();
									boolean isNotOk = true;
									while (isNotOk) {
										teamId = a.getSmallestTeams().get(ran.nextInt(a.getSmallestTeams().size()));
										if (a.getSmallestTeams().contains(teamId)) {
											isNotOk = false;
										}
									}
								}
							} catch (Exception exep) { exep.printStackTrace(); }
						}
						
						a.addPlayer(player, teamId);
					} catch (Exception exeption) { exeption.printStackTrace(); }
				}
			}
		}
		else if (e.getInventory().getName() == "Pick a Class") {
			if (e.getCurrentItem() != null) {
				e.setCancelled(true);
				
				Player player = (Player) e.getWhoClicked();
				player.closeInventory();
				
				if (e.getCurrentItem().hasItemMeta()) {
					if (e.getCurrentItem().getItemMeta().hasDisplayName()) {
						if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("Random Class")) {
							plugin.im.playerClass.remove(player);
							player.sendMessage(Broadcast.TAG + "You have chosen a random class!");
						} else {
							String techName = plugin.menuSC.getTechnicalClassName(
									e.getCurrentItem().getItemMeta().getDisplayName());
							if (player.hasPermission("crystalquest.admin") || player.hasPermission("crystalquest.staff") ||
									player.hasPermission("crystalquest.kit." + techName) ||
									player.hasPermission("crystalquest.kit.*")) {
								plugin.im.setPlayerClass(player, techName);
								player.sendMessage(Broadcast.TAG + "You have chosen the " + e.getCurrentItem().getItemMeta()
										.getDisplayName() + ChatColor.YELLOW + " class!");
							} else {
								player.sendMessage(ChatColor.RED + "[!!] Sorry, you don't have permision to select this kit!");
							}
						}
					} else {
						return;
					}
				} else {
					return;
				}
			}
		}
	}
}