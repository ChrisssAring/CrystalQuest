package nl.SugCube.CrystalQuest.Listeners;

import java.util.Random;

import nl.SugCube.CrystalQuest.Broadcast;
import nl.SugCube.CrystalQuest.CrystalQuest;
import nl.SugCube.CrystalQuest.Teams;
import nl.SugCube.CrystalQuest.UpdateChecker;
import nl.SugCube.CrystalQuest.Events.PlayerJoinArenaEvent;
import nl.SugCube.CrystalQuest.Events.PlayerLeaveArenaEvent;
import nl.SugCube.CrystalQuest.Game.Arena;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scoreboard.Team;

public class PlayerListener implements Listener {

	public static CrystalQuest plugin;
	
	public PlayerListener(CrystalQuest instance) {
		plugin = instance;	
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		if (plugin.getArenaManager().isInGame(p)) {
			plugin.getArenaManager().getArena(p).removePlayer(p);
		}
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		if (plugin.getConfig().getBoolean("updates.check-for-updates")) {
			if (plugin.getConfig().getBoolean("updates.show-admin")) {
				UpdateChecker uc = new UpdateChecker(plugin, "http://dev.bukkit.org/bukkit-plugins/crystalquest/files.rss");
				if (uc.updateAvaiable()) {
					if (e.getPlayer().hasPermission("crystalquest.admin")) {
						e.getPlayer().sendMessage(Broadcast.TAG + "A new version of CrystalQuest is available! Get" +
								"it at the BukkitDev page!");
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent e) {
		if (plugin.getConfig().getBoolean("arena.team-colour-prefix")) {
			Player p = e.getPlayer();
			if (plugin.getArenaManager().isInGame(p)) {
				e.setMessage(Teams.getTeamChatColour(plugin.getArenaManager().getTeam(p)) + e.getMessage());
			}
		}
	}
	
	@EventHandler
	public void onPlayerLeaveArena(PlayerLeaveArenaEvent e) {
		Player p = e.getPlayer();
		Arena a = e.getArena();
		
		for (Wolf w : a.getGameWolfs()) {
			if (w.getOwner() == p) {
				w.setHealth(0);
			}
		}
		
		if (a.isInGame()) {
			for (Team t : a.getTeams()) {
				if (t.getPlayers().size() == a.getPlayers().size()) {
					a.declareWinner();
					a.setEndGame(true);
				}
			}
		} else {
			if (a.isCounting()) {
				if (a.getPlayers().size() < a.getMinPlayers()) {
					a.setIsCounting(false);
					a.setCountdown(plugin.getConfig().getInt("arena.countdown"));
				}
			}
		}
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			if (plugin.am.isInGame(p)) {
				if (!plugin.am.getArena(p).isInGame()) {
					e.setCancelled(true);
				} else {
					if (e.getCause() == DamageCause.FALL) {
						e.setCancelled(true);
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent e) {
		if (e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			if (plugin.am.isInGame(p)) {
				p.setFoodLevel(20);
				p.setSaturation(20F);
			}
		}
	}
	
	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent e) {
		Player p = e.getPlayer();
		if (plugin.am.isInGame(p)) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent e) {
		Player p = e.getPlayer();
		if (plugin.am.isInGame(p)) {
			plugin.im.setClassInventory(p);
			Random ran = new Random();
			Arena a = plugin.getArenaManager().getArena(p);
			e.setRespawnLocation((a.getPlayerSpawns().get(ran.nextInt(a.getPlayerSpawns().size()))));
			p.setLevel(0);
			p.setExp(0);
		}
	}
	
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
			Player target = (Player) e.getDamager();
			if (plugin.am.isInGame((Player) e.getDamager())) {
				if (plugin.getArenaManager().getArena(target).isEndGame()) {
					e.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerJoinArena(PlayerJoinArenaEvent e) {
		if (e.getPlayer().hasPermission("crystalquest.arena." + e.getArena().getName()) ||
				e.getPlayer().hasPermission("crystalquest.arena." + e.getArena().getId())) {
			if (e.getArena().getPlayers().size() == e.getArena().getMaxPlayers()) {
				e.setCancelled(true);
				e.getPlayer().sendMessage(ChatColor.RED + "[!!] Couldn't join the arena, the arena is full!");
				return;
			}
			
			if (e.getArena().isInGame()) {
				e.setCancelled(true);
				e.getPlayer().sendMessage(ChatColor.RED + "[!!] Couldn't join the arena, the game has already started!");
				return;
			}
			
			if (e.getArena().getPlayers().size() + 1 == e.getArena().getMinPlayers()) {
				e.getArena().setIsCounting(true);
				e.getArena().setCountdown(plugin.getConfig().getInt("arena.countdown"));
				return;
			}
			
		} else {
			e.getPlayer().sendMessage(ChatColor.RED + "[!!] You don't have permission to join this arena!");
			e.setCancelled(true);
			return;
		}
	}
	
	/*
	 * Block commands while in-game.
	 */
	@EventHandler
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent e) {
		if (!e.getPlayer().hasPermission("crystalquest.admin") && !e.getPlayer().hasPermission("crystalquest.staff")) {
			if (!e.getMessage().equalsIgnoreCase("/cq quit") && !e.getMessage().equalsIgnoreCase("/cq leave")) {
				if (plugin.am.isInGame(e.getPlayer())) {
					e.setCancelled(true);
					e.getPlayer().sendMessage(ChatColor.RED + "[!!] You can't execute commands while in-game!");
				}
			}
		}
	}
	
	/*
	 * Block in-game block breaking
	 */
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		if (plugin.am.isInGame(e.getPlayer())) {
			e.setCancelled(true);
		}
	}
	
	/*
	 * Block in-game block placement.
	 */
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		if (plugin.am.isInGame(e.getPlayer())) {
			e.setCancelled(true);
		}
	}
	
	/*
	 * Allow certain items in-game
	 */
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		if (plugin.am.isInGame(e.getPlayer())) {
			if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (e.getPlayer().getInventory().getItemInHand().getType() != Material.BOW) {
			if (e.getPlayer().getInventory().getItemInHand().getType() != Material.POTION) {
			if (e.getPlayer().getInventory().getItemInHand().getType() != Material.MILK_BUCKET) {
			if (e.getPlayer().getInventory().getItemInHand().getType() != Material.EGG) {
			if (e.getPlayer().getInventory().getItemInHand().getType() != Material.POTION) {
			if (e.getPlayer().getInventory().getItemInHand().getType() != Material.GOLDEN_APPLE) {
			if (e.getPlayer().getInventory().getItemInHand().getType() != Material.RED_ROSE) {
			if (e.getPlayer().getInventory().getItemInHand().getType() != Material.INK_SACK) {
			if (e.getPlayer().getInventory().getItemInHand().getType() != Material.SKULL) {
			if (e.getPlayer().getInventory().getItemInHand().getType() != Material.MONSTER_EGG) {
			if (e.getPlayer().getInventory().getItemInHand().getType() != Material.STONE_PLATE) {
															if (plugin.am.isInGame(e.getPlayer())) {
																e.setCancelled(true);
															}
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		
		if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
			Player p = e.getPlayer();
			if (plugin.am.isInGame(p)) {
				e.setCancelled(true);
			}
		}
	}
}