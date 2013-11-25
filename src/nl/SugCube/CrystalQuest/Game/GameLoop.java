package nl.SugCube.CrystalQuest.Game;

import java.util.Random;

import nl.SugCube.CrystalQuest.Broadcast;
import nl.SugCube.CrystalQuest.CrystalQuest;
import nl.SugCube.CrystalQuest.Teams;

import org.bukkit.ChatColor;
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
							pl.sendMessage(Broadcast.TAG + "The quest will start in " + ChatColor.YELLOW + "2 minutes");
						}
					} else if (a.getCountdown() == 60) {
						for (Player pl : a.getPlayers()) {
							pl.sendMessage(Broadcast.TAG + "The quest will start in " + ChatColor.YELLOW + "1 minute");
						}
					} else if (a.getCountdown() == 30) {
						for (Player pl : a.getPlayers()) {
							pl.sendMessage(Broadcast.TAG + "The quest will start in " + ChatColor.YELLOW + "30 seconds");
						}
					} else if (a.getCountdown() == 10) {
						for (Player pl : a.getPlayers()) {
							pl.sendMessage(Broadcast.TAG + "The quest will start in " + ChatColor.YELLOW + "10 seconds");
						}
					} else if (a.getCountdown() <= 5 && a.getCountdown() > 0) {
						for (Player pl : a.getPlayers()) {
							pl.sendMessage(Broadcast.TAG + "The quest will start in " + ChatColor.YELLOW +
									a.getCountdown() + " seconds");
							pl.playSound(pl.getLocation(), Sound.CLICK, 20F, 20F);
						}
					} else if (a.getCountdown() <= 0) {
						for (Player pl : a.getPlayers()) {
							pl.sendMessage(Broadcast.TAG + "The quest has started!");
							pl.playSound(pl.getLocation(), Sound.LEVEL_UP, 20F, 20F);
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
													.equalsIgnoreCase(ChatColor.GREEN + "Crystal Shard")) {
												p.getInventory().remove(is);
											} else if (is.getItemMeta().getDisplayName()
													.equalsIgnoreCase(ChatColor.AQUA + "Small Crystal")) {
												p.getInventory().remove(is);
											} else if (is.getItemMeta().getDisplayName()
													.equalsIgnoreCase(ChatColor.AQUA + "Shiny Crystal")) {
												p.getInventory().remove(is);
											}
										}
									}
								}
							}
							
							if (p.getLevel() > 0) {
								a.addScore(plugin.getArenaManager().getTeam(p), p.getLevel());
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
						a.resetArena();
						a.setEndGame(false);
					} else { 
						a.setAfterCount(a.getAfterCount() - 1);
						
						for (Player p : a.getPlayers()) {
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
					}
				}
			}
		}
		
	}
	
}