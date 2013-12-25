package nl.SugCube.CrystalQuest.Listeners;

import java.util.Random;

import nl.SugCube.CrystalQuest.Broadcast;
import nl.SugCube.CrystalQuest.CrystalQuest;
import nl.SugCube.CrystalQuest.Economy.Multipliers;
import nl.SugCube.CrystalQuest.Game.Arena;
import nl.SugCube.CrystalQuest.Game.ArenaManager;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathMessages implements Listener {
	
	public static CrystalQuest plugin;
	public static boolean fired = false;
	
	public DeathMessages(CrystalQuest instance) {
		plugin = instance;
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		Player p = (Player) e.getEntity();
		
		if (plugin.getArenaManager().isInGame(p)) {
			e.setDeathMessage(null);
			e.setDroppedExp((int) p.getExp());
			e.getDrops().clear();
			
			Random ran = new Random();
			int crystals = ran.nextInt(5) + 2;
			for (int i = 0; i < crystals; i++) {
				if (ran.nextInt(3) > 0) {
					p.getWorld().dropItem(p.getLocation(), plugin.itemHandler.getItemByName(Broadcast.get("items.crystal-shard")));
				}
				plugin.getArenaManager().getArena(p).addScore(plugin.getArenaManager().getTeam(p), -1);
			}
	
			ArenaManager am = plugin.getArenaManager();
			if (am.isInGame(p)) {
				LivingEntity len = p.getKiller();
				Arena a = am.getArena(p);
				EntityDamageEvent damageEvent = p.getLastDamageCause();
				DamageCause cause = null;
				if (damageEvent != null) {
					 cause = damageEvent.getCause();
				}
				
				try {
					if (!fired && cause != null) {
						if (cause == DamageCause.DROWNING) {
							a.sendDeathMessage(p, " drowned");
						} else if (cause == DamageCause.ENTITY_EXPLOSION || cause == DamageCause.BLOCK_EXPLOSION) {
							a.sendDeathMessage(p, " exploded");
						} else if (cause == DamageCause.CONTACT) {
							a.sendDeathMessage(p, " has been pricked to death");
						} else if (cause == DamageCause.FIRE || cause == DamageCause.FIRE_TICK) {
							a.sendDeathMessage(p, " burnt away");
						} else if (cause == DamageCause.LAVA) {
							a.sendDeathMessage(p, " became obsidian");
						} else if (cause == DamageCause.LIGHTNING) {
							a.sendDeathMessage(p, " got electrocuted");
						} else if (cause == DamageCause.MAGIC) {
							a.sendDeathMessage(p, " disappeared");
						} else if (cause == DamageCause.PROJECTILE) {
							if (damageEvent.getEntity() instanceof Player) {
								Player shooter = (Player) p.getKiller();
								if (shooter != null) {
									a.sendDeathMessage(p, shooter, "shot");
									
									double chance = Multipliers.getMultiplier("blood",
											plugin.economy.getLevel(p, "blood", "crystals"), false);
									int multiplier = 1;
									
									if (ran.nextInt(100) <= chance * 100 && chance != 0) {
										multiplier = 2;
									}
									
									//Adds crystals to their balance
									int money = (int) (1 * plugin.getConfig().getDouble("shop.crystal-multiplier"));
									plugin.economy.getBalance().addCrystals(shooter, money * multiplier, false);
									String message = plugin.economy.getCoinMessage(shooter, money * multiplier);
									if (message != null) {
										shooter.sendMessage(message);
									}
								}
							}
						} else if (cause == DamageCause.SUFFOCATION) {
							a.sendDeathMessage(p, " suffocated");
						} else if (cause == DamageCause.THORNS) {
							if (damageEvent.getEntity() instanceof Player && len != null) {
								if (len instanceof Player) {
									a.sendDeathMessage(p, (Player) len, "pricked");
								}
							}
						} else if (cause == DamageCause.VOID) {
							a.sendDeathMessage(p, " fell out of the world");
						} else if (cause == DamageCause.WITHER) {
							a.sendDeathMessage(p, " withered away");
						} else if (cause == DamageCause.ENTITY_ATTACK) {
							if (len instanceof Player) {
								a.sendDeathMessage(p, (Player) len);
								
								double chance = Multipliers.getMultiplier("blood",
										plugin.economy.getLevel(p, "blood", "crystals"), false);
								int multiplier = 1;
								
								if (ran.nextInt(100) <= chance * 100 && chance != 0) {
									multiplier = 2;
								}
								
								//Adds crystals to their balance
								int money = (int) (1 * plugin.getConfig().getDouble("shop.crystal-multiplier"));
								plugin.economy.getBalance().addCrystals((Player) len, money * multiplier, false);
								String message = plugin.economy.getCoinMessage((Player) len, money * multiplier);
								if (message != null) {
									((Player) len).sendMessage(message);
								}
							} else {
								a.sendDeathMessage(p, " has been slain");
							}
						}
					} else {
						fired = false;
					}
				} catch (Exception ex) {
					fired = false;
				}
			}
		}
	}

}