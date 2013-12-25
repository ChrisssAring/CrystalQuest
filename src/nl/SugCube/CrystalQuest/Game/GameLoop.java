package nl.SugCube.CrystalQuest.Game;

import java.util.Random;

import nl.SugCube.CrystalQuest.Broadcast;
import nl.SugCube.CrystalQuest.CrystalQuest;
import nl.SugCube.CrystalQuest.Teams;
import nl.SugCube.CrystalQuest.Economy.Multipliers;
import nl.SugCube.CrystalQuest.SBA.SMeth;

import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

public class GameLoop implements Runnable {

	public CrystalQuest plugin;
	public ArenaManager am;
	public Random ran;
	private String winningTeam;
	
	public GameLoop(CrystalQuest instance, ArenaManager arenaManager) {
		this.plugin = instance;
		this.am = arenaManager;
		this.ran = new Random();
	}
	
	public void run() {
		
		for (Arena a : am.arena) {
			if (a.isEnabled()) {
				
				if (a.isCounting()) {
					/*
					 * Arena Countdown
					 */
					for (Player pl : a.getPlayers()) {
						pl.setLevel(a.getCountdown());
					}
					
					if (a.getCountdown() == 120) {
						for (Player pl : a.getPlayers()) {
							pl.sendMessage(Broadcast.TAG + Broadcast.get("arena.start")
									.replace("%time%", "2 " + Broadcast.get("arena.minutes")));
						}
					} else if (a.getCountdown() == 60) {
						for (Player pl : a.getPlayers()) {
							pl.sendMessage(Broadcast.TAG + Broadcast.get("arena.start")
									.replace("%time%", "1 " + Broadcast.get("arena.minute")));
						}
					} else if (a.getCountdown() == 30) {
						for (Player pl : a.getPlayers()) {
							pl.sendMessage(Broadcast.TAG + Broadcast.get("arena.start")
									.replace("%time%", "30 " + Broadcast.get("arena.seconds")));
						}
					} else if (a.getCountdown() == 10) {
						for (Player pl : a.getPlayers()) {
							pl.sendMessage(Broadcast.TAG + Broadcast.get("arena.start")
									.replace("%time%", "10 " + Broadcast.get("arena.seconds")));
						}
					} else if (a.getCountdown() <= 5 && a.getCountdown() > 0) {
						for (Player pl : a.getPlayers()) {
							pl.sendMessage(Broadcast.TAG + Broadcast.get("arena.start")
									.replace("%time%", a.getCountdown() + " " + Broadcast.get("arena.seconds")));
							pl.playSound(pl.getLocation(), Sound.CLICK, 20F, 20F);
						}
					} else if (a.getCountdown() <= 0) {
						for (Player pl : a.getPlayers()) {
							plugin.im.setClassInventory(pl);
							pl.sendMessage(Broadcast.TAG + Broadcast.get("arena.started"));
							pl.playSound(pl.getLocation(), Sound.LEVEL_UP, 20F, 20F);
							pl.sendMessage(Broadcast.TAG + Broadcast.get("arena.using-class")
									.replace("%class%", SMeth.setColours(plugin.getConfig().getString(
									"kit." + plugin.im.playerClass.get(pl) + ".name"))));
						}
						a.setIsCounting(false);
						a.setCountdown(plugin.getConfig().getInt("arena.countdown") + 1);
						a.startGame();
					}
					
					a.setCountdown(a.getCountdown() - 1);
				} else if (a.isInGame() && !a.isEndGame()) {

				/*
				 * GAME LOOP DURING THE GAME
				 */
					
					if (a.getTimeLeft() > 0) {
						
						for (Player p : a.getPlayers()) {
							for (ItemStack is : p.getInventory().getContents()) {
								if (is != null) {
									if (is.getType() == Material.GLASS_BOTTLE) {
										p.getInventory().remove(is);
									}
									if (is.hasItemMeta()) {
										if (is.getItemMeta().hasDisplayName()) {
											if (is.getItemMeta().getDisplayName()
													.equalsIgnoreCase(Broadcast.get("items.crystal-shard"))) {
												p.getInventory().remove(is);
											} else if (is.getItemMeta().getDisplayName()
													.equalsIgnoreCase(Broadcast.get("items.small-crystal"))) {
												p.getInventory().remove(is);
											} else if (is.getItemMeta().getDisplayName()
													.equalsIgnoreCase(Broadcast.get("items.shiny-crystal"))) {
												p.getInventory().remove(is);
											}
										}
									}
								}
							}
							
							if (p.getLevel() > 0) {
								int extraPoints = (int) Multipliers.getMultiplier("xp",
										plugin.economy.getLevel(p, "xp", "crystals"), false) - 1;
								
								a.addScore(plugin.getArenaManager().getTeam(p), p.getLevel() + extraPoints);
								p.setLevel(0);
							}
						}
						
						a.setTimeLeft(a.getTimeLeft() - 1);
						a.updateTimer();
					} else {
						this.winningTeam = a.declareWinner();
						a.setAfterCount(plugin.getConfig().getInt("arena.after-count"));
						a.setEndGame(true);
						a.setIsCounting(false);
						a.setTimeLeft(plugin.getConfig().getInt("arena.game-length"));
					}
					
				}
				
				/*
				 * AFTER GAME
				 */
				if (a.isEndGame()) {
					if (a.getAfterCount() <= 0) {
						a.setEndGame(false);
						a.resetArena();
					} else { 
						a.setAfterCount(a.getAfterCount() - 1);
						
						for (Player p : a.getPlayers()) {
							try {
								if (!a.getSpectators().contains(p)) {
									if (a.getTeam(p) == Teams.getTeamIdFromNAME(this.winningTeam)) {
										Firework f = p.getLocation().getWorld().spawn(p.getLocation().add(0, 2, 0), Firework.class);
										FireworkMeta fm = f.getFireworkMeta();
										fm.setPower(1);
										FireworkEffect fe = FireworkEffect.builder()
																.flicker(true)
																.withColor(plugin.im.getTeamColour(a.getTeam(p)))
																.with(Type.STAR)
																.build();
										fm.clearEffects();
										fm.addEffect(fe);
										f.setFireworkMeta(fm);
									}
								}
							} catch (Exception e) { }
						}
					}
				}
			}
		}
		
	}
	
}