package nl.SugCube.CrystalQuest.Listeners;

import java.util.Random;

import nl.SugCube.CrystalQuest.Broadcast;
import nl.SugCube.CrystalQuest.CrystalQuest;
import nl.SugCube.CrystalQuest.Teams;
import nl.SugCube.CrystalQuest.Update;
import nl.SugCube.CrystalQuest.Events.PlayerJoinArenaEvent;
import nl.SugCube.CrystalQuest.Events.PlayerLeaveArenaEvent;
import nl.SugCube.CrystalQuest.Game.Arena;
import nl.SugCube.CrystalQuest.IO.LoadData;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
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
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

public class PlayerListener implements Listener {

	public static CrystalQuest plugin;
	
	public PlayerListener(CrystalQuest instance) {
		plugin = instance;	
	}
	
	 @EventHandler
	 public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
		 Player player = event.getPlayer();
		 if (plugin.getArenaManager().isInGame(player)) {
			 if (plugin.getArenaManager().getArena(player).canDoubleJump()) {
				 if (player.getGameMode() != GameMode.CREATIVE) {
					 event.setCancelled(true);
					 player.setAllowFlight(false);
					 player.setFlying(false);
					 player.setVelocity(new Vector(player.getVelocity().getX(), 0.9518, player.getVelocity().getZ()));
					 player.setVelocity(player.getVelocity().add(player.getLocation().getDirection().multiply(0.3018)));
					 player.playSound(player.getLocation(), Sound.SHOOT_ARROW, 1F, 1F);
				 }
			 }
		 }
	 }
	 
	 @EventHandler
	 public void onPlayerMove(PlayerMoveEvent event) {
		 Player player = event.getPlayer();
		 if (plugin.getArenaManager().isInGame(player)) {
			 if (plugin.getArenaManager().getArena(player).canDoubleJump()) {
				 if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
					 if (event.getPlayer().getLocation().getBlock() .getRelative(BlockFace.DOWN).getType() != Material.AIR) {
						 event.getPlayer().setAllowFlight(true);
					 }
				 }
			 }
		 }
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
		LoadData.loadSigns();
		if (plugin.getConfig().getBoolean("updates.check-for-updates")) {
			if (plugin.getConfig().getBoolean("updates.show-admin")) {
				Update uc = new Update(69421, plugin.getDescription().getVersion());
				if (uc.query()) {
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
					} else if (e.getCause() == DamageCause.LIGHTNING) {
						p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 218, 1));
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
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerRespawn(PlayerRespawnEvent e) {
		Player p = e.getPlayer();
		p.getInventory().clear();
		p.updateInventory();
		if (plugin.am.isInGame(p)) {
			plugin.im.setClassInventory(p);
			Random ran = new Random();
			Arena a = plugin.getArenaManager().getArena(p);
			
			/*
			 * Teleports to team spawn if there are team spawns set.
			 * Otherwise, the normal playerspawns count.
			 */
			if (a.getTeamSpawns().get(a.getTeamCount() - 1).size() > 0) {
				int team = a.getTeam(p);
				e.setRespawnLocation(a.getTeamSpawns().get(team).get(ran.nextInt(a.getTeamSpawns().get(team).size())));
			} else {
				e.setRespawnLocation((a.getPlayerSpawns().get(ran.nextInt(a.getPlayerSpawns().size()))));
			}
			
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
				e.getPlayer().hasPermission("crystalquest.arena." + e.getArena().getId()) ||
				e.getPlayer().hasPermission("crystalquest.arena.*") ||
				e.getPlayer().hasPermission("crystalquest.staff") ||
				e.getPlayer().hasPermission("crystalquest.admin")) {
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
		if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
			Player p = e.getPlayer();
			if (plugin.am.isInGame(p)) {
				e.setCancelled(true);
			}
		}
		
		if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Player p = e.getPlayer();
			if (plugin.am.isInGame(p)) {
				if (p.getItemInHand().getType() == Material.TNT) {
					Location loc = p.getLocation().add(0, 1, 0);
					TNTPrimed tnt = loc.getWorld().spawn(loc, TNTPrimed.class);
					tnt.setFuseTicks(40);
					tnt.setYield(0);
					e.getPlayer().getInventory().removeItem(new ItemStack(Material.TNT, 1));
				}
			}
		}
	}
}