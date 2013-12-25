package nl.SugCube.CrystalQuest.Items;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import nl.SugCube.CrystalQuest.Broadcast;
import nl.SugCube.CrystalQuest.CrystalQuest;
import nl.SugCube.CrystalQuest.ParticleHandler;
import nl.SugCube.CrystalQuest.Teams;
import nl.SugCube.CrystalQuest.Economy.Multipliers;
import nl.SugCube.CrystalQuest.Game.Arena;
import nl.SugCube.CrystalQuest.Game.ArenaManager;
import nl.SugCube.CrystalQuest.Listeners.DeathMessages;

import org.bukkit.Bukkit;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ItemListener implements Listener {
	
	public static CrystalQuest plugin;
	
	public ItemListener(CrystalQuest instance) {
		plugin = instance;
	}
	
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof Fireball && e.getEntity() instanceof LivingEntity) {
			LivingEntity lentity = (LivingEntity) e.getEntity();
			Fireball ball = (Fireball) e.getDamager();
			if (ball.getShooter() instanceof Player) {
				Player shooter = (Player) ball.getShooter();
				if (plugin.getArenaManager().isInGame(shooter)) {
					Arena a = plugin.getArenaManager().getArena(shooter);
					if (lentity instanceof Player) {
						Player target = (Player) lentity;
						if (a.getTeam(target) == a.getTeam(shooter)) {
							e.setCancelled(true);
						} else {
							target.setFireTicks(150);
						}
					} else {
						lentity.setFireTicks(150);
					}
				}
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if (plugin.getArenaManager().isInGame(p)) {
			if (!plugin.getArenaManager().getArena(p).getSpectators().contains(p)) {
				if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
					if (p.getInventory().getItemInHand() != null) {
						/*
						 * USE: RAILGUN
						 */
						if (p.getInventory().getItemInHand().getType() == Material.IRON_HOE) {
							ItemStack is = p.getInventory().getItemInHand();
							if (is.getAmount() == 1) {
								p.getInventory().remove(p.getInventory().getItemInHand());
							} else {
								is.setAmount(is.getAmount() - 1);
							}
							
							p.playSound(p.getLocation(), Sound.ANVIL_LAND, 20F, 20F);
							final Snowball ball = p.launchProjectile(Snowball.class);
							ball.setVelocity(p.getLocation().getDirection().multiply(15));
							ParticleHandler.balls.add(ball);
							Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
								public void run() {
									ParticleHandler.balls.remove(ball);
								}
							}, 60L);
						}
						/*
						 * USE: BLOOPER
						 */
						else if (p.getInventory().getItemInHand().getType() == Material.INK_SACK) {
							ItemStack is = p.getInventory().getItemInHand();
							if (is.getAmount() == 1) {
								p.getInventory().remove(p.getInventory().getItemInHand());
							} else {
								is.setAmount(is.getAmount() - 1);
							}
							
							double multiplier = Multipliers.getMultiplier("debuff",
									plugin.economy.getLevel(p, "debuff", "upgrade"), false);
							int duration = (int) (118 * multiplier);
							
							PotionEffect effect = new PotionEffect(PotionEffectType.BLINDNESS, duration, 14);
							PotionEffect effect2 = new PotionEffect(PotionEffectType.SPEED, duration, 0);
							
							Arena a = plugin.getArenaManager().getArena(p);
							Random ran = new Random();
							boolean canSelect = true;
							int targetTeam = 0;
							
							if (a.getPlayers().size() > 1) {
								do {
									targetTeam = ran.nextInt(a.getTeamCount());
									if (targetTeam != a.getTeam(p)) {
										for (Player player : a.getPlayers()) {
											if (a.getTeam(player) == targetTeam) {
												canSelect = false;
											}
										}
									}
								} while (canSelect);
								
								for (OfflinePlayer olTarget : a.getTeams()[targetTeam].getPlayers()) {
									for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
										if (onlinePlayer == olTarget) {
											onlinePlayer.addPotionEffect(effect);
											onlinePlayer.addPotionEffect(effect2);
											onlinePlayer.playSound(p.getLocation(), Sound.SLIME_WALK, 12F, 12F);
										}
									}
								}
							}
						}
						/*
						 * USE: FIRE FLOWER
						 */
						else if (p.getInventory().getItemInHand().getType() == Material.RED_ROSE) {
							ItemStack is = p.getInventory().getItemInHand();
							if (is.getAmount() == 1) {
								p.getInventory().remove(p.getInventory().getItemInHand());
							} else {
								is.setAmount(is.getAmount() - 1);
							}
							
							Fireball ball = p.launchProjectile(Fireball.class);
							ball.setVelocity(p.getLocation().getDirection().multiply(3));
							ball.setYield(0);
						}
						/*
						 * USE: WITHER
						 */
						else if (p.getInventory().getItemInHand().getType() == Material.SKULL_ITEM) {
							ItemStack is = p.getInventory().getItemInHand();
							if (is.getAmount() == 1) {
								p.getInventory().remove(p.getInventory().getItemInHand());
							} else {
								is.setAmount(is.getAmount() - 1);
							}
							
							Arena a = plugin.getArenaManager().getArena(p);
							Random ran = new Random();
							boolean canSelect = true;
							int targetTeam = 0;
							
							if (a.getPlayers().size() > 1) {
								do {
									targetTeam = ran.nextInt(a.getTeamCount());
									if (targetTeam != a.getTeam(p)) {
										for (Player player : a.getPlayers()) {
											if (a.getTeam(player) == targetTeam) {
												canSelect = false;
											}
										}
									}
								} while (canSelect);
								
								double multiplier = Multipliers.getMultiplier("debuff",
										plugin.economy.getLevel(p, "debuff", "upgrade"), false);
								int duration = (int) (120 * multiplier);
								p.playSound(p.getLocation(), Sound.WITHER_HURT, 10L, 10L);
								p.setHealth((p.getHealth() - 4 < 0 ? 0 : p.getHealth() - 4));
								
								for (OfflinePlayer olTarget : a.getTeams()[targetTeam].getPlayers()) {
									for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
										if (onlinePlayer == olTarget) {
											onlinePlayer.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, duration, 2));
											onlinePlayer.playEffect(EntityEffect.WOLF_SMOKE);
											onlinePlayer.playSound(onlinePlayer.getLocation(), Sound.WITHER_HURT, 10L, 10L);
										}
									}
								}
							}
						}
						/*
						 * USE: CREEPER EGG
						 */
						else if (p.getInventory().getItemInHand().getType() == Material.MONSTER_EGG) {
							if (Action.RIGHT_CLICK_BLOCK == e.getAction()) {
								e.setCancelled(true);
							}
							
							ItemStack is = p.getInventory().getItemInHand();
							if (is.getAmount() == 1) {
								p.getInventory().remove(p.getInventory().getItemInHand());
							} else {
								is.setAmount(is.getAmount() - 1);
							}
							
							Creeper c = p.getWorld().spawn(p.getTargetBlock(null, 64).getLocation().add(0, 1, 0), Creeper.class);
							p.playSound(p.getTargetBlock(null, 64).getLocation().add(0, 1, 0), Sound.CREEPER_HISS, 10L, 10L);
							
							Random ran = new Random();
							double chance = Multipliers.getMultiplier("creeper",
									plugin.economy.getLevel(p, "creepers", "upgrade"), false);
							
							if (ran.nextInt(1000) <= chance * 1000) {
								c.setPowered(true);
							}
							
							Arena a = plugin.getArenaManager().getArena(p);
							a.getGameCreepers().add(c);
						}
						/*
						 * USE: LANDMINE
						 */
						else if (p.getInventory().getItemInHand().getType() == Material.STONE_PLATE) {
							ItemStack is = p.getInventory().getItemInHand();
							if (is.getAmount() == 1) {
								p.getInventory().remove(p.getInventory().getItemInHand());
							} else {
								is.setAmount(is.getAmount() - 1);
							}
							
							Arena a = plugin.getArenaManager().getArena(p);
							
							if (p.getLocation().getBlock().getType() == Material.AIR) {
								Block block = p.getLocation().add(0, -1, 0).getBlock();
								if (block.getType() != Material.AIR) {
									Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new PlaceLandmine(
											p.getLocation().getBlock(), a, p), 40L);
									a.getLandmines().put(p.getLocation().getBlock().getLocation(), p);
								}
							} else if (p.getLocation().getBlock().getType() == Material.LONG_GRASS ||
									p.getLocation().getBlock().getType() == Material.STATIONARY_WATER ||
									p.getLocation().getBlock().getType() == Material.WATER ||
									p.getLocation().getBlock().getType() == Material.DEAD_BUSH ||
									p.getLocation().getBlock().getType() == Material.YELLOW_FLOWER ||
									p.getLocation().getBlock().getType() == Material.RED_ROSE ||
									p.getLocation().getBlock().getType() == Material.RAILS ||
									p.getLocation().getBlock().getType() == Material.DETECTOR_RAIL ||
									p.getLocation().getBlock().getType() == Material.POWERED_RAIL) {
								if (p.getLocation().add(0, 1, 0).getBlock().getType() == Material.AIR) {
									Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new PlaceLandmine(
											p.getLocation().add(0, 1, 0).getBlock(), a, p), 40L);
								}
							}
						}
						/*
						 * USE: Wolf ♥
						 */
						else if (p.getInventory().getItemInHand().getType() == Material.BONE) {
							ItemStack is = p.getInventory().getItemInHand();
							if (is.getAmount() == 1) {
								p.getInventory().remove(p.getInventory().getItemInHand());
							} else {
								is.setAmount(is.getAmount() - 1);
							}
							
							int strengthLevel = (int) Multipliers.getMultiplier("wolfstrength",
									plugin.economy.getLevel(p, "wolf", "upgrade"), false) - 1;
							int resistanceLevel = (int) Multipliers.getMultiplier("wolfresistance",
									plugin.economy.getLevel(p, "wolf", "upgrade"), false) - 1;
							
							ArenaManager am = plugin.getArenaManager();
							Wolf w = p.getWorld().spawn(p.getLocation(), Wolf.class);
							w.setOwner(p);
							w.setAdult();
							w.setCustomName(Teams.getTeamChatColour(am.getTeam(p)) + getWolfName());
							w.setCollarColor(Teams.getTeamDyeColour(am.getTeam(p)));
							w.setMaxHealth(20);
							w.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 9999, strengthLevel));
							
							if (resistanceLevel >= 0) {
								w.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 9999, resistanceLevel));
							}
							
							w.playEffect(EntityEffect.WOLF_HEARTS);
							w.getWorld().playSound(w.getLocation(), Sound.WOLF_BARK, 3L, 3L);
							am.getArena(p).getGameWolfs().add(w);
						}
						/*
						 * USE: GLUE
						 */
						else if (p.getInventory().getItemInHand().getType() == Material.SLIME_BALL) {
							ItemStack is = p.getInventory().getItemInHand();
							if (is.getAmount() == 1) {
								p.getInventory().remove(p.getInventory().getItemInHand());
							} else {
								is.setAmount(is.getAmount() - 1);
							}
							PotionEffect effect = new PotionEffect(PotionEffectType.SLOW, 118, 14);
							
							Arena a = plugin.getArenaManager().getArena(p);
							Random ran = new Random();
							boolean canSelect = true;
							int targetTeam = 0;
							
							if (a.getPlayers().size() > 1) {
								do {
									targetTeam = ran.nextInt(a.getTeamCount());
									if (targetTeam != a.getTeam(p)) {
										for (Player player : a.getPlayers()) {
											if (a.getTeam(player) == targetTeam) {
												canSelect = false;
											}
										}
									}
								} while (canSelect);
								
								for (OfflinePlayer olTarget : a.getTeams()[targetTeam].getPlayers()) {
									for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
										if (onlinePlayer == olTarget) {
											onlinePlayer.addPotionEffect(effect);
											onlinePlayer.playSound(p.getLocation(), Sound.SLIME_WALK2, 12F, 12F);
										}
									}
								}
							}
						}
						/*
						 * USE: LIGHTNING BOLT
						 */
						else if (p.getInventory().getItemInHand().getType() == Material.FEATHER) {
							ItemStack is = p.getInventory().getItemInHand();
							if (is.getAmount() == 1) {
								p.getInventory().remove(p.getInventory().getItemInHand());
							} else {
								is.setAmount(is.getAmount() - 1);
							}
							
							Arena a = plugin.getArenaManager().getArena(p);
							Random ran = new Random();
							boolean canSelect = true;
							int targetTeam = 0;
							
							if (a.getPlayers().size() > 1) {
								do {
									targetTeam = ran.nextInt(a.getTeamCount());
									if (targetTeam != a.getTeam(p)) {
										for (Player player : a.getPlayers()) {
											if (a.getTeam(player) == targetTeam) {
												canSelect = false;
											}
										}
									}
								} while (canSelect);
	
								double lightning = Multipliers.getMultiplier("lightning",
										plugin.economy.getLevel(p, "explosive", "upgrade"), false);
								
								double multiplier = Multipliers.getMultiplier("debuff",
										plugin.economy.getLevel(p, "debuff", "upgrade"), false);
								int duration = (int) (218 * multiplier);
								
								for (OfflinePlayer olTarget : a.getTeams()[targetTeam].getPlayers()) {
									for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
										if (onlinePlayer == olTarget) {
											if (lightning > 0) {
												World w = onlinePlayer.getLocation().getWorld();
												w.createExplosion(onlinePlayer.getLocation().getX(), onlinePlayer.getLocation().getY(),
														onlinePlayer.getLocation().getZ(), (float) lightning, false, false);
											}
											
											onlinePlayer.getWorld().strikeLightning(onlinePlayer.getLocation());
											onlinePlayer.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, duration, 1));
										}
									}
								}
							}
						}
					}
				}
			}
			/*
			 * CHECKS FOR LANDMINES
			 */
			else if (e.getAction() == Action.PHYSICAL) {
				Block b = e.getClickedBlock();
				Location loc = p.getLocation(); 
				b.getWorld().createExplosion(loc.getX(), loc.getY(), loc.getZ(), 2.0F, false, false);
				Arena a = plugin.getArenaManager().getArena(p);
				if (a != null) {
					a.getGameBlocks().remove(b);
					b.setType(Material.AIR);
					DeathMessages.fired = true;
					a.sendDeathMessage(p, " stood on a landmine");
					if (p.getHealth() > 0) {
						p.setHealth(0);
					}
					
					if (a.getLandmines().containsKey(b.getLocation())) {
						Player killer = a.getLandmines().get(b.getLocation());
						a.getLandmines().remove(b.getLocation());
						
						if (killer != null && !a.isEndGame() && killer != p) {
							Random ran = new Random();
							double chance = Multipliers.getMultiplier("blood",
									plugin.economy.getLevel(killer, "blood", "crystals"), false);
							int multiplier = 1;
							
							if (ran.nextInt(100) <= chance * 100 && chance != 0) {
								multiplier = 2;
							}
							
							//Adds crystals to their balance
							int money = (int) (1 * plugin.getConfig().getDouble("shop.crystal-multiplier"));
							plugin.economy.getBalance().addCrystals(killer, money * multiplier, false);
							String message = plugin.economy.getCoinMessage(killer, money * multiplier);
							if (message != null) {
								killer.sendMessage(message);
							}
						}
					}
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerPickupItem(PlayerPickupItemEvent e) {
		if (plugin.getArenaManager().isInGame(e.getPlayer())) {
			if (!plugin.getArenaManager().getArena(e.getPlayer()).getSpectators().contains(e.getPlayer())) {
				Player player = e.getPlayer();
				Arena a = plugin.getArenaManager().getArena(player);
				ItemStack is = e.getItem().getItemStack();
				if (is.hasItemMeta()) {
					if (is.getItemMeta().hasDisplayName()) {
						ItemMeta im = is.getItemMeta();
						String name = im.getDisplayName();
						Random ran = new Random();
						
						/*
						 * GRENADES-RAILGUN-HAMMER-LANDMINE
						 */
						if (name.contains(Broadcast.get("items.grenade")) || name.contains(Broadcast.get("items.railgun")) ||
								name.contains(Broadcast.get("items.landmine")) || name.contains(Broadcast.get("items.hammer"))) {
							double chance = Multipliers.getMultiplier("ammo",
									plugin.economy.getLevel(player, "weaponry", "upgrade"), false);
							if (chance > 0) {
								int random = ran.nextInt((int) (10 - (chance * 10)));
								if (random <= chance * 10) {
									e.setCancelled(true);
									e.getItem().remove();
									if (is.getType() == Material.DIAMOND_AXE) {
										ItemStack newIs = new ItemStack(is.getType(), 1, (short) (1561 - (ran.nextInt(3) + 1)));
										newIs.addUnsafeEnchantments(is.getEnchantments());
										player.getInventory().addItem(newIs);
									} else {
										ItemStack newIs = new ItemStack(is.getType(), is.getAmount() * 2);
										player.getInventory().addItem(newIs);
									}
								}
							}
						}
						/*
						 * SPEED POTION
						 */
						if (name.contains(Broadcast.get("items.speed"))) {
							double multiplier = Multipliers.getMultiplier("buff",
									plugin.economy.getLevel(player, "buff", "upgrade"), false);
							PotionMeta pm = (PotionMeta) is.getItemMeta();
							PotionEffect effect = new PotionEffect(PotionEffectType.SPEED, (int) (360 * multiplier), 1);
							pm.removeCustomEffect(PotionEffectType.SPEED);
							pm.addCustomEffect(effect, true);
							is.setItemMeta(pm);
						}
						/*
						 * STRENGTH POTION
						 */
						if (name.contains(Broadcast.get("items.strength"))) {
							double multiplier = Multipliers.getMultiplier("buff",
									plugin.economy.getLevel(player, "buff", "upgrade"), false);
							PotionMeta pm = (PotionMeta) is.getItemMeta();
							PotionEffect effect = new PotionEffect(PotionEffectType.INCREASE_DAMAGE, (int) (240 * multiplier), 1);
							pm.removeCustomEffect(PotionEffectType.INCREASE_DAMAGE);
							pm.addCustomEffect(effect, true);
							is.setItemMeta(pm);
						}
						/*
						 * STRENGTH POTION
						 */
						if (name.contains(Broadcast.get("items.health"))) {
							int lvl = plugin.economy.getLevel(player, "buff", "upgrade");
							PotionMeta pm = (PotionMeta) is.getItemMeta();
							PotionEffect effect = new PotionEffect(PotionEffectType.HEAL, 1, lvl);
							pm.removeCustomEffect(PotionEffectType.HEAL);
							pm.addCustomEffect(effect, true);
							is.setItemMeta(pm);
						}
						/*
						 * CRYSTAL SHARD
						 */
						if (name.contains(Broadcast.get("items.crystal-shard"))) {
							int teamId = plugin.getArenaManager().getTeam(player);
							a.setScore(teamId, a.getScore(teamId) + e.getItem().getItemStack().getAmount());
							player.playSound(player.getLocation(), Sound.LEVEL_UP, 20, 20);
						}
						/*
						 * SMALL CRYSTAL
						 */
						else if (name.contains(Broadcast.get("items.small-crystal"))) {
							int teamId = plugin.getArenaManager().getTeam(player);
							a.setScore(teamId, a.getScore(teamId) + e.getItem().getItemStack().getAmount() * 2);
							player.playSound(player.getLocation(), Sound.LEVEL_UP, 20, 20);
						}
						/*
						 * SHINY CRYSTAL
						 */
						else if (name.contains(Broadcast.get("items.shiny-crystal"))) {
							int teamId = plugin.getArenaManager().getTeam(player);
							a.setScore(teamId, a.getScore(teamId) + e.getItem().getItemStack().getAmount() * 3);
							player.playSound(player.getLocation(), Sound.LEVEL_UP, 20, 20);
						}
						/*
						 * SHIELD
						 */
						else if (name.contains(Broadcast.get("items.shield"))) {
							double multiplier = Multipliers.getMultiplier("buff",
									plugin.economy.getLevel(player, "buff", "upgrade"), false);
							player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
							player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, (int) (618 * multiplier),
									0));
							player.playSound(player.getLocation(), Sound.ANVIL_USE, 3L, 3L);
							e.getItem().remove();
							e.setCancelled(true);
						}
						else {
							e.getItem().remove();
						}
					} else {
						e.setCancelled(true);
						e.getItem().remove();
					}
				} else {
					e.setCancelled(true);
					e.getItem().remove();
				}
			}
		}
	}
	
	public String getWolfName() {
		List<String> names = new ArrayList<String>();
		names.add("Bertha");
		names.add("Jan");
		names.add("John");
		names.add("Ruben");
		names.add("Michael");
		names.add("Jonny");
		names.add("Wolfie");
		names.add("Berda");
		names.add("Nicole");
		names.add("Woofy");
		names.add("Jerry");
		names.add("Jeffrey");
		names.add("Maria");
		names.add("Willem 2nd");
		names.add("François");
		names.add("Jean-Pièrre");
		names.add("Biscuit");
		names.add("Fabel");
		names.add("Elisabeth");
		names.add("George");
		names.add("Harry");
		names.add("Henry");
		names.add("Ron");
		names.add("Gerard");
		names.add("Bonifatius");
		names.add("Snorlax");
		names.add("Henk");
		names.add("Superduperdocabocapoligismo");
		names.add("Holy Cow");
		names.add("Johnathan");
		names.add("Yoloswag");
		names.add("Sauce");
		names.add("Toad");
		names.add("Baby Luigi");
		names.add("Sinterklaas");
		names.add("Santa");;
		names.add("Jay");
		names.add("Mr. Woofles");
		names.add("Op");
		names.add("Pony Danza");
		names.add("Njol");
		names.add("Polished Crystal");
		names.add("Peter");
		names.add("Trijntje");
		names.add("Corry");
		names.add("Maartje");
		names.add("Anique");
		names.add("Sjoerd");
		names.add("Ølaf");
		
		return names.get((new Random()).nextInt(names.size()));
	}

}