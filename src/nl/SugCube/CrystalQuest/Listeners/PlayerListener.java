package nl.SugCube.CrystalQuest.Listeners;

import java.util.Random;

import nl.SugCube.CrystalQuest.Broadcast;
import nl.SugCube.CrystalQuest.CrystalQuest;
import nl.SugCube.CrystalQuest.Teams;
import nl.SugCube.CrystalQuest.Update;
import nl.SugCube.CrystalQuest.Economy.Multipliers;
import nl.SugCube.CrystalQuest.Events.PlayerJoinArenaEvent;
import nl.SugCube.CrystalQuest.Events.PlayerLeaveArenaEvent;
import nl.SugCube.CrystalQuest.Events.TeamWinGameEvent;
import nl.SugCube.CrystalQuest.Game.Arena;
import nl.SugCube.CrystalQuest.Game.ArenaManager;
import nl.SugCube.CrystalQuest.IO.LoadData;

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
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

public class PlayerListener implements Listener {

	public static CrystalQuest plugin;
	
	public PlayerListener(CrystalQuest instance) {
		plugin = instance;	
	}
	
	/*
	 * SPECTATOR STUFF
	 */
	@EventHandler
	public void onSpectatorDamage(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			ArenaManager am = plugin.getArenaManager();
			if (am.isInGame(p)) {
				if (am.getArena(p).getSpectators().contains(p)) {
					p.setFireTicks(0);
					p.setHealth(20);
					p.setFoodLevel(20);
					p.setExp(0);
					p.setLevel(0);
					p.setSaturation(20);
					e.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler
	public void onEntityDamageBySpectator(EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			if (plugin.getArenaManager().isInGame(p)) {
				if (plugin.getArenaManager().getArena(p).getSpectators().contains(p)) {
					e.setCancelled(true);
				}
			}
		} else if (e.getDamager() instanceof Player) {
			Player p = (Player) e.getDamager();
			if (plugin.getArenaManager().isInGame(p)) {
				if (plugin.getArenaManager().getArena(p).getSpectators().contains(p)) {
					e.setCancelled(true);
				}
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onSpectatorInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if (plugin.getArenaManager().isInGame(p)) {
			if (plugin.getArenaManager().getArena(p).getSpectators().contains(p)) {
				e.setCancelled(true);
				p.getInventory().clear();
				p.updateInventory();
			}
		}
	}
	
	@EventHandler
	public void onSpectatorPickupItem(PlayerPickupItemEvent e) {
		Player p = e.getPlayer();
		if (plugin.getArenaManager().isInGame(p)) {
			if (plugin.getArenaManager().getArena(p).getSpectators().contains(p)) {
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onPlayerSpectate(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		if (plugin.getArenaManager().isInGame(p)) {
			if (plugin.getArenaManager().getArena(p).getSpectators().contains(p)) {
				Location loc = e.getTo();
				if (!plugin.prot.isInProtectedArena(loc)) {
					e.setCancelled(true);
				}
			}
		}
	}
	/*
	 * END SPECTATOR STUFF
	 */
	
	@EventHandler
	public void onTeamWinGame(TeamWinGameEvent e) {
		Team[] teams = e.getTeams();
		Arena a = e.getArena();
		int score = a.getScore(Teams.getTeamIdFromNAME(e.getTeamName()));
		int verschil = 9999999;
		
		int i = 0;
		for (Team t : teams) {
			if (i == e.getTeamCount()) {
				break;
			}
			
			if (t != null) {
				if (a.getScore(i) >= 0) {
					if (Math.abs(score - a.getScore(i)) < verschil) {
						verschil = Math.abs(score - a.getScore(i));
					}
				}
			}
			i++;
		}
		
		int crystals = 25;
		int extrac = 0;			
		extrac = (int) ((((double) score) - ((double) verschil)) / ((double) score)) * 25;
		if (extrac > 25) {
			extrac = 25;
		}
		crystals += extrac;
		
		for (Player p : e.getPlayers()) {
			int money = (int) (crystals * plugin.getConfig().getDouble("shop.crystal-multiplier"));
			
			double extra = Multipliers.getMultiplier("win",
					plugin.economy.getLevel(p, "win", "crystals"), false);
			
			plugin.economy.getBalance().addCrystals(p, (int) (money * extra), false);
			String message = plugin.economy.getCoinMessage(p, (int) (money * extra));
			if (message != null) {
				p.sendMessage(message);
			}
		}
	}
	
	 @EventHandler(priority = EventPriority.HIGHEST)
	 public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
		 Player player = event.getPlayer();
		 if (plugin.getArenaManager().isInGame(player)) {
			 if (plugin.getArenaManager().getArena(player).canDoubleJump()) {
				 if (!plugin.getArenaManager().getArena(player).getSpectators().contains(player)) {
					 if (player.getGameMode() != GameMode.CREATIVE) {
						 player.setVelocity(new Vector(player.getVelocity().getX(), 0.9518, player.getVelocity().getZ()));
						 player.setVelocity(player.getVelocity().add(player.getLocation().getDirection().multiply(0.3018)));
						 player.playSound(player.getLocation(), Sound.SHOOT_ARROW, 1F, 1F);
						 player.setAllowFlight(false);
						 player.setFlying(false);
						 event.setCancelled(true);
					 }
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
						e.getPlayer().sendMessage(Broadcast.TAG + "A new version of CrystalQuest is available!");
						e.getPlayer().sendMessage(Broadcast.TAG + "Get it at the BukkitDev-page!");
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
				if (!plugin.am.getArena(p).isInGame() || plugin.am.getArena(p).isEndGame()) {
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
			if (e.getArena().getPlayers().size() == e.getArena().getMaxPlayers() && !e.isSpectating()) {
				e.setCancelled(true);
				e.getPlayer().sendMessage(Broadcast.get("arena.full"));
				return;
			}
			
			if (e.getArena().isInGame() && !e.isSpectating()) {
				e.setCancelled(true);
				e.getPlayer().sendMessage(Broadcast.get("arena.already-started"));
				return;
			}
			
			if (e.getArena().getPlayers().size() + 1 == e.getArena().getMinPlayers() && !e.isSpectating()) {
				e.getArena().setIsCounting(true);
				e.getArena().setCountdown(plugin.getConfig().getInt("arena.countdown"));
				return;
			}
			
		} else {
			e.getPlayer().sendMessage(Broadcast.get("arena.no-permission"));
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
					e.getPlayer().sendMessage(Broadcast.get("arena.no-commands"));
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