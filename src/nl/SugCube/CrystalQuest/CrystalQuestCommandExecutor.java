package nl.SugCube.CrystalQuest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import nl.SugCube.CrystalQuest.Game.Arena;
import nl.SugCube.CrystalQuest.SBA.SMeth;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CrystalQuestCommandExecutor implements CommandExecutor {

	public static CrystalQuest plugin;
	public boolean askedHardReset = false;
	
	public CrystalQuestCommandExecutor(CrystalQuest instance) {
		plugin = instance;
	}
	
	/*
	 * Fired if someone uses the /cq command
	 */
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (args.length >= 1) {
			/*
			 * LEAVE/QUIT
			 */
			if (args[0].equalsIgnoreCase("leave") || args[0].equalsIgnoreCase("quit")) {
				if (sender instanceof Player) {
					Player player = (Player) sender;
					if (plugin.am.isInGame(player)) {
						plugin.am.getArena(player).removePlayer(player);
						player.sendMessage(Broadcast.TAG + "You left the game.");
					} else {
						player.sendMessage(ChatColor.RED + "[!!] You are not in-game!");
					}
				} else {
					plugin.getLogger().info(Broadcast.ONLY_IN_GAME);
				}
				plugin.signHandler.updateSigns();
			}
			/*
			 * CREATEARENA
			 */
			else if (args[0].equalsIgnoreCase("createarena")) {
				if (sender.hasPermission("crystalquest.admin")) {
					int arenaId = plugin.am.createArena() + 1;
					sender.sendMessage(Broadcast.TAG + "Arena " + ChatColor.GRAY + arenaId +
							ChatColor.YELLOW + " has been created!");
				} else {
					sender.sendMessage(Broadcast.NO_PERMISSION);
				}
			}
			/*
			 * ENABLE <ARENA#|ARENAID>
			 */
			else if (args[0].equalsIgnoreCase("enable")) {
				if (sender.hasPermission("crystalquest.admin") || sender.hasPermission("crystalquest.staff") ||
						sender.hasPermission("crystalquest.enable")) {
					if (args.length >= 2) {
						try {
							Arena a;
							try {
								a = plugin.am.getArena(Integer.parseInt(args[1]) - 1);
							} catch (Exception e) {
								a = plugin.am.getArena(args[1]);
							}
							a.setEnabled(true);
							sender.sendMessage(Broadcast.TAG + "Enabled Arena " + args[1]);
						} catch (Exception e) {
							sender.sendMessage(ChatColor.RED + "[!!] Could not enable the arena!");
							e.printStackTrace();
							plugin.getLogger().info("NullPointerException? Make sure the arena exists!");
						}
					} else {
						sender.sendMessage(ChatColor.RED + "[!!] Usage: /cq enable <arena#|arenaname>");
					}
				} else {
					sender.sendMessage(Broadcast.NO_PERMISSION);
				}
				plugin.signHandler.updateSigns();
			}
			/*
			 * DISABLE <ARENA#|ARENAID>
			 */
			else if (args[0].equalsIgnoreCase("disable")) {
				if (sender.hasPermission("crystalquest.admin") || sender.hasPermission("crystalquest.staff") ||
						sender.hasPermission("crystalquest.disable")) {
					if (args.length >= 2) {
						try {
							Arena a;
							try {
								a = plugin.am.getArena(Integer.parseInt(args[1]) - 1);
							} catch (Exception e) {
								a = plugin.am.getArena(args[1]);
							}
							a.setEnabled(false);
							sender.sendMessage(Broadcast.TAG + "Disabled Arena " + args[1]);
						} catch (Exception e) {
							sender.sendMessage(ChatColor.RED + "[!!] Could not disable the arena!");
							e.printStackTrace();
							plugin.getLogger().info("NullPointerException? Make sure the arena exists!");
						}
					} else {
						sender.sendMessage(ChatColor.RED + "[!!] Usage: /cq disable <arena#|arenaname>");
					}
				} else {
					sender.sendMessage(Broadcast.NO_PERMISSION);
				}
				plugin.signHandler.updateSigns();
			}
			/*
			 * SETLOBBY
			 */
			else if (args[0].equalsIgnoreCase("setlobby")) {
				if (sender instanceof Player) {
					if (sender.hasPermission("crystalquest.admin")) {
						plugin.am.setLobby(((Player) sender).getLocation());
						sender.sendMessage(Broadcast.TAG + "The lobbyspawn has been set!");
					} else {
						sender.sendMessage(Broadcast.NO_PERMISSION);
					}
				} else {
					plugin.getLogger().info(Broadcast.ONLY_IN_GAME);
				}
			}
			/*
			 * TEAMLOBBY <ARENA#|ARENANAME> <TEAMID+1|TEAMNAME>
			 */
			else if (args[0].equalsIgnoreCase("teamlobby")) {
				if (sender instanceof Player) {
					if (sender.hasPermission("crystalquest.admin")) {
						if (args.length >= 3) {
							try {
								Arena a;
								try {
									a = plugin.am.getArena(Integer.parseInt(args[1]) - 1);
								} catch (Exception e) {
									a = plugin.am.getArena(args[1]);
								}
								Location[] spawns = a.getLobbySpawns();
								int teamId = Teams.getTeamId(args[2]);
								if (teamId < a.getTeamCount()) {
									spawns[Teams.getTeamId(args[2])] = ((Player) sender).getLocation();
									a.setLobbySpawns(spawns);
									sender.sendMessage(Broadcast.TAG + "Set the lobby of Team " +
												args[2] + " for Arena " + args[1]);
								} else {
									sender.sendMessage(ChatColor.RED + "[!!] This team does not exist in this arena!");
								}
							} catch (Exception e) {
								sender.sendMessage(ChatColor.RED + "[!!] Could not set the Teamlobby");
								e.printStackTrace();
								plugin.getLogger().info("NullPointerException? Make sure the arena exists!");
							}
						} else {
							sender.sendMessage(ChatColor.RED + "[!!] Usage: /cq teamlobby <arena#|arenaname> <team>");
						}
					} else {
						sender.sendMessage(Broadcast.NO_PERMISSION);
					}
				} else {
					plugin.getLogger().info(Broadcast.ONLY_IN_GAME);
				}
			}
			/*
			 * FORCESTART <ARENA#|ARENANAME>
			 */
			else if (args[0].equalsIgnoreCase("forcestart")) {
				if (sender.hasPermission("crystalquest.admin") || sender.hasPermission("crystalquest.staff") ||
						sender.hasPermission("crystalquest.forcestart")) {
					if (args.length >= 2) {
						try {
							Arena a;
							try {
								a = plugin.am.getArena(Integer.parseInt(args[1]) - 1);
							} catch (Exception e) {
								a = plugin.am.getArena(args[1]);
							}
							a.setIsCounting(true);
							sender.sendMessage(Broadcast.TAG + "Started Arena " + args[1]);
						} catch (Exception e) {
							sender.sendMessage(ChatColor.RED + "[!!] Could not start the arena!");
							e.printStackTrace();
							plugin.getLogger().info("NullPointerException? Make sure the arena exists!");
						}
					} else {
						sender.sendMessage(ChatColor.RED + "[!!] Usage: /cq forcestart <arena#|arenaname>");
					}
				} else {
					sender.sendMessage(Broadcast.NO_PERMISSION);
				}
				plugin.signHandler.updateSigns();
			}
			/*
			 * SETNAME <ARENA#|ARENANAME> <NEWNAME>
			 */
			else if (args[0].equalsIgnoreCase("setname")) { 
				if (sender.hasPermission("crystalquest.admin")) {
					if (args.length >= 3) {
						try {
							Arena a;
							try {
								a = plugin.am.getArena(Integer.parseInt(args[1]) - 1);
							} catch (Exception e) {
								a = plugin.am.getArena(args[1]);
							}
							
							String oldname = a.getName();
							boolean bool = a.setName(args[2]);

							if (bool) {
								for (Sign s : plugin.signHandler.getSigns()) {
									if (s.getLine(1).equalsIgnoreCase(oldname)) {
										s.setLine(1, a.getName());
										s.update(true);
									}
								}
							} else {
								sender.sendMessage(ChatColor.RED + "[!!] Wrong name!");
							}
							
							sender.sendMessage(Broadcast.TAG + "Arena " + (a.getId() + 1) + "'s name is now " +
									ChatColor.GRAY + a.getName());
								
						} catch (Exception e) {
							sender.sendMessage(ChatColor.RED + "[!!] Could not name the arena!");
							e.printStackTrace();
							plugin.getLogger().info("NullPointerException? Make sure the arena exists!");
						}
					} else {
						sender.sendMessage(ChatColor.RED + "[!!] Usage: /cq setname <arena#|arenaname> <newname>");
					}
				} else {
					sender.sendMessage(Broadcast.NO_PERMISSION);
				}
			}
			/*
			 * HARDRESET
			 */
			else if (args[0].equalsIgnoreCase("hardreset")) {
				if (sender instanceof ConsoleCommandSender) {
					plugin.getLogger().info("Would you really like to delete ALL data (with exception of the data.yml)? " +
							"Please mind that there is no way back! If you really would like to remove all data," +
							" please use the command 'cq yes' to confirm.");
					this.askedHardReset = true;
				} else {
					plugin.getLogger().info(Broadcast.NO_PERMISSION);
				}
			}
			/*
			 * YES
			 */
			else if (args[0].equalsIgnoreCase("yes")) {
				if (sender instanceof ConsoleCommandSender) {
					if (this.askedHardReset) {
						File file = new File(plugin.getDataFolder() + File.separator + "data.yml");
						try {
							PrintWriter pw = new PrintWriter(file);
							pw.print("");
							pw.close();
							plugin.reloadData();
							plugin.getLogger().info("All data has been destructed!");
							plugin.am.arena.clear();
						} catch (FileNotFoundException e) {
							plugin.getLogger().info("Could not destroy data!");
						}
						this.askedHardReset = false;
					} else {
						plugin.getLogger().info("No effect!");
					}
				} else {
					sender.sendMessage(Broadcast.NO_PERMISSION);
				}
			}
			/*
			 * SPAWN <ARENA> [CLEAR]
			 */
			else if (args[0].equalsIgnoreCase("spawn")) {
				if (sender instanceof Player) {
					if (sender.hasPermission("crystalquest.admin")) {
						if (args.length >= 2) {
							if (args.length >= 3) {
								if (args[2].equalsIgnoreCase("clear")) {
									try {
										Arena a;
										try {
											a = plugin.am.getArena(Integer.parseInt(args[1]) - 1);
										} catch (Exception e) {
											a = plugin.am.getArena(args[1]);
										}
										a.clearPlayerSpawns();
										sender.sendMessage(Broadcast.TAG + "Removed all player-spawns from Arena " + args[1]);
									} catch (Exception e) {
										sender.sendMessage(ChatColor.RED + "[!!] Could not remove the playerspawns!");
										e.printStackTrace();
										plugin.getLogger().info("NullPointerException? Make sure the arena exists!");
									}
								}
							}
							if (args.length == 2) {
								try {
									Arena a;
									try {
										a = plugin.am.getArena(Integer.parseInt(args[1]) - 1);
									} catch (Exception e) {
										a = plugin.am.getArena(args[1]);
									}
									a.addPlayerSpawn(((Player) sender).getLocation());
									sender.sendMessage(Broadcast.TAG + "Added spawn " + ChatColor.GRAY + "#" + 
										a.getPlayerSpawns().size() + ChatColor.YELLOW + " to Arena " + args[1]);
								} catch (Exception e) {
									sender.sendMessage(ChatColor.RED + "[!!] Could not add the playerspawn!");
									e.printStackTrace();
									plugin.getLogger().info("NullPointerException? Make sure the arena exists!");
								}
							}
						} else {
							sender.sendMessage(ChatColor.RED + "[!!] Usage: /cq spawn <arena#|arenaname> [clear]");
						}
					} else {
						sender.sendMessage(Broadcast.NO_PERMISSION);
					}
				} else {
					plugin.getLogger().info(Broadcast.ONLY_IN_GAME);
				}
			}
			/*
			 * CRYSTALSPAWN 
			 */
			else if (args[0].equalsIgnoreCase("crystalspawn")) {
				if (sender instanceof Player) {
					if (sender.hasPermission("crystalquest.admin")) {
						if (args.length >= 2) {
							if (args.length >= 3) {
								if (args[2].equalsIgnoreCase("clear")) {
									try {
										Arena a;
										try {
											a = plugin.am.getArena(Integer.parseInt(args[1]) - 1);
										} catch (Exception e) {
											a = plugin.am.getArena(args[1]);
										}
										a.clearCrystalSpawns();
										sender.sendMessage(Broadcast.TAG + "Removed all crystalspawns from Arena " + args[1]);
									} catch (Exception e) {
										sender.sendMessage(ChatColor.RED + "[!!] Could not remove the crystalspawns!");
										e.printStackTrace();
										plugin.getLogger().info("NullPointerException? Make sure the arena exists!");
									}
								}
							}
							if (args.length == 2) {
								try {
									Arena a;
									try {
										a = plugin.am.getArena(Integer.parseInt(args[1]) - 1);
									} catch (Exception e) {
										a = plugin.am.getArena(args[1]);
									}
									a.addCrystalSpawn(((Player) sender).getLocation().add(0, -0.6, 0));
									sender.sendMessage(Broadcast.TAG + "Added crystalspawn " + ChatColor.GRAY + "#" + 
										a.getCrystalSpawns().size() + ChatColor.YELLOW + " to Arena " + args[1]);
								} catch (Exception e) {
									sender.sendMessage(ChatColor.RED + "[!!] Could not add the crystalspawn!");
									e.printStackTrace();
									plugin.getLogger().info("NullPointerException? Make sure the arena exists!");
								}
							}
						} else {
							sender.sendMessage(ChatColor.RED + "[!!] Usage: /cq crystalspawn <arena#|arenaname> [clear]");
						}
					} else {
						sender.sendMessage(Broadcast.NO_PERMISSION);
					}
				} else {
					plugin.getLogger().info(Broadcast.ONLY_IN_GAME);
				}
			}
			/*
			 * ITEMSPAWN
			 */
			else if (args[0].equalsIgnoreCase("itemspawn")) {
				if (sender instanceof Player) {
					if (sender.hasPermission("crystalquest.admin")) {
						if (args.length >= 2) {
							if (args.length >= 3) {
								if (args[2].equalsIgnoreCase("clear")) {
									try {
										Arena a;
										try {
											a = plugin.am.getArena(Integer.parseInt(args[1]) - 1);
										} catch (Exception e) {
											a = plugin.am.getArena(args[1]);
										}
										a.clearItemSpawns();
										sender.sendMessage(Broadcast.TAG + "Removed all itemspawns from Arena " + args[1]);
									} catch (Exception e) {
										sender.sendMessage(ChatColor.RED + "[!!] Could not remove the itemspawns!");
										e.printStackTrace();
										plugin.getLogger().info("NullPointerException? Make sure the arena exists!");
									}
								}
							}
							if (args.length == 2) {
								try {
									Arena a;
									try {
										a = plugin.am.getArena(Integer.parseInt(args[1]) - 1);
									} catch (Exception e) {
										a = plugin.am.getArena(args[1]);
									}
									a.addItemSpawn(((Player) sender).getLocation().add(0, 2, 0));
									sender.sendMessage(Broadcast.TAG + "Added itemspawn " + ChatColor.GRAY + "#" + 
										a.getItemSpawns().size() + ChatColor.YELLOW + " to Arena " + args[1]);
								} catch (Exception e) {
									sender.sendMessage(ChatColor.RED + "[!!] Could not add the itemspawn!");
									e.printStackTrace();
									plugin.getLogger().info("NullPointerException? Make sure the arena exists!");
								}
							}
						} else {
							sender.sendMessage(ChatColor.RED + "[!!] Usage: /cq itemspawn <arena#|arenaname> [clear]");
						}
					} else {
						sender.sendMessage(Broadcast.NO_PERMISSION);
					}
				} else {
					plugin.getLogger().info(Broadcast.ONLY_IN_GAME);
				}
			}
			/*
			 * TEAMSPAWN <arena> <team> [clear]
			 */
			else if (args[0].equalsIgnoreCase("teamspawn")) {
				if (sender instanceof Player) {
					if (sender.hasPermission("crystalquest.admin")) {
						if (args.length >= 3) {
							Arena a;
							try {
								a = plugin.am.getArena(Integer.parseInt(args[1]) - 1);
							} catch (Exception e) {
								a = plugin.am.getArena(args[1]);
							}
							
							boolean canContinue = true;
							if (args.length >= 4) {
								try {
									if (args[3].equalsIgnoreCase("clear")) {
										int teamId = Teams.getTeamId(args[2]);
										if (teamId < a.getTeamCount()) {
											a.getTeamSpawns().get(teamId).clear();
											sender.sendMessage(Broadcast.TAG + "All spawns have been reset in Arena " +
											args[1] + " for team " + args[2]);
										} else {
											sender.sendMessage(ChatColor.RED + "[!!] This team does not exist in this arena!");
										}
									}
								} catch (Exception e) { 
									sender.sendMessage(ChatColor.RED + "[!!] Usage: /cq teamspawn <arena> <team> [clear]");
								}
								finally {
									canContinue = false;
								}
							}
							
							if (canContinue) {
								int teamId = Teams.getTeamId(args[2]);
								if (teamId < a.getTeamCount()) {
									a.getTeamSpawns().get(teamId).add(((Player) sender).getLocation());
									sender.sendMessage(Broadcast.TAG + "Added Teamspawn " + ChatColor.GRAY + "#" +
											a.getTeamSpawns().get(teamId).size() + ChatColor.YELLOW + " to arena " + args[1] +
											" for team " + args[2]);
								} else {
									sender.sendMessage(ChatColor.RED + "[!!] This team does not exist in this arena!");
								}
							}
						} else {
							sender.sendMessage(ChatColor.RED + "[!!] Usage: /cq teamspawn <arena> <team> [clear]");
						}
					} else {
						sender.sendMessage(Broadcast.NO_PERMISSION);
					}
				} else {
					plugin.getLogger().info(Broadcast.ONLY_IN_GAME);
				}
			}
			/*
			 * KICK <PLAYER>
			 */
			else if (args[0].equalsIgnoreCase("kick")) {
				if (sender.hasPermission("crystalquest.admin") || sender.hasPermission("crystalquest.staff") ||
						sender.hasPermission("crystalquest.kick")) {
					if (args.length >= 2) {
						Player player = Bukkit.getPlayer(args[1]);
						if (player != null) {
							if (plugin.am.isInGame(player)) {
								plugin.am.getArena(player).removePlayer(player);
								player.sendMessage(Broadcast.TAG + "You have been kicked from the arena!");
								sender.sendMessage(Broadcast.TAG + "You kicked " + ChatColor.GRAY + player.getName() + 
										" from the game.");
							} else {
								sender.sendMessage(ChatColor.RED + "[!!] Player " + player.getName() + " is not in-game!");
							}
						} else {
							sender.sendMessage(ChatColor.RED + "[!!] Player " + args[1] + " is not online!");
						}
					} else {
						sender.sendMessage(ChatColor.RED + "[!!] Usage: /cq kick <player>");
					}
				} else {
					sender.sendMessage(Broadcast.NO_PERMISSION);
				}
			}
			/*
			 * MINPLAYERS <ARENA> <PLAYERS>
			 */
			else if (args[0].equalsIgnoreCase("minplayers")) {
				if (sender.hasPermission("crystalquest.admin")) {
					if (args.length >= 3) {
						try {
							Arena a;
							try {
								a = plugin.am.getArena(Integer.parseInt(args[1]) - 1);
							} catch (Exception e) {
								a = plugin.am.getArena(args[1]);
							}
							a.setMinPlayers(Integer.parseInt(args[2]));
							sender.sendMessage(Broadcast.TAG + "Set minimum amount of players to " + args[2]);
						} catch (Exception e) {
							sender.sendMessage(ChatColor.RED + "[!!] Could not set the minimum of players!");
							e.printStackTrace();
							plugin.getLogger().info("NullPointerException? Make sure the arena exists!");
						}
					} else {
						sender.sendMessage(ChatColor.RED + "[!!] Usage: /cq minplayers <arena> <amount>");
					}
				} else {
					sender.sendMessage(Broadcast.NO_PERMISSION);
				}
			}
			/*
			 * MAXPLAYERS <ARENA> <PLAYERS>
			 */
			else if (args[0].equalsIgnoreCase("maxplayers")) {
				if (sender.hasPermission("crystalquest.admin")) {
					if (args.length >= 3) {
						try {
							Arena a;
							try {
								a = plugin.am.getArena(Integer.parseInt(args[1]) - 1);
							} catch (Exception e) {
								a = plugin.am.getArena(args[1]);
							}
							a.setMaxPlayers(Integer.parseInt(args[2]));
							sender.sendMessage(Broadcast.TAG + "Set maximum amount of players to " + args[2]);
						} catch (Exception e) {
							sender.sendMessage(ChatColor.RED + "[!!] Could not set the maximum of players!");
							e.printStackTrace();
							plugin.getLogger().info("NullPointerException? Make sure the arena exists!");
						}
					} else {
						sender.sendMessage(ChatColor.RED + "[!!] Usage: /cq maxplayers <arena> <amount>");
					}
				} else {
					sender.sendMessage(Broadcast.NO_PERMISSION);
				}
			}
			/*
			 * SETTEAMS <ARENA> <PLAYERS>
			 */
			else if (args[0].equalsIgnoreCase("setteams")) {
				if (sender.hasPermission("crystalquest.admin")) {
					if (args.length >= 3) {
						try {
							Arena a;
							try {
								a = plugin.am.getArena(Integer.parseInt(args[1]) - 1);
							} catch (Exception e) {
								a = plugin.am.getArena(args[1]);
							}
							a.setTeams(Integer.parseInt(args[2]));
							sender.sendMessage(Broadcast.TAG + "Set amount of teams to " + args[2]);
						} catch (Exception e) {
							sender.sendMessage(ChatColor.RED + "[!!] Could not set the amount of teams!");
							e.printStackTrace();
							plugin.getLogger().info("NullPointerException? Make sure the arena exists!");
						}
					} else {
						sender.sendMessage(ChatColor.RED + "[!!] Usage: /cq setteams <arena> <amount>");
					}
				} else {
					sender.sendMessage(Broadcast.NO_PERMISSION);
				}
			}
			/*
			 * LIST
			 */
			else if (args[0].equalsIgnoreCase("list")) {
				if (sender.hasPermission("crystalquest.admin") || sender.hasPermission("crystalquest.staff")) {
					String arenas = "";
					boolean first = false;
					for (Arena a : plugin.am.arena) {
						if (!first) {
							arenas += a.getName() + "(" + (a.getId() + 1) + ")";
							first = true;
						} else {
							arenas += ", " + a.getName() + "(" + (a.getId() + 1) + ")";
						}
					}
					sender.sendMessage(Broadcast.TAG + arenas);
				} else {
					sender.sendMessage(Broadcast.NO_PERMISSION);
				}
			}
			/*
			 * HOWDEY (just for fun)
			 */
			else if (args[0].equalsIgnoreCase("howdey")) {
				sender.sendMessage(Broadcast.HOWDEY);
			}
			/*
			 * CHECK <ARENA#|ARENANAME>
			 */
			else if (args[0].equalsIgnoreCase("check")) {
				if (sender.hasPermission("crystalquest.admin")) {
					if (args.length >= 2) {
						Arena a;
						try {
							a = plugin.am.getArena(Integer.parseInt(args[1]) - 1);
						} catch (Exception e) {
							a = plugin.am.getArena(args[1]);
						}
						
						if (a.getName().isEmpty()) {
							sender.sendMessage(Broadcast.TAG + SMeth.setColours("Name: &dNot set&e | ID: &d" + (a.getId() + 1)));
						} else {
							sender.sendMessage(Broadcast.TAG + SMeth.setColours("Name: &d" + a.getName() + "&e | ID: &d" + (a.getId() + 1)));
						}
						
						if (a.getTeamCount() < 2) {
							sender.sendMessage(Broadcast.TAG + SMeth.setColours("Team Amount: &dNot set"));
						} else {
							sender.sendMessage(Broadcast.TAG + SMeth.setColours("Team Amount: &d" + a.getTeamCount()));
						}
						
						if (a.getMinPlayers() < 2) {
							sender.sendMessage(Broadcast.TAG + SMeth.setColours("Minimum Players: &dNot set"));
						} else {
							sender.sendMessage(Broadcast.TAG + SMeth.setColours("Minimum Players: &d" + a.getMinPlayers()));
						}
						
						if (a.getMaxPlayers() < 2) {
							sender.sendMessage(Broadcast.TAG + SMeth.setColours("Maximum Players: &dNot set"));
						} else {
							sender.sendMessage(Broadcast.TAG + SMeth.setColours("Maximum Players: &d" + a.getMaxPlayers()));
						}
						
						if (a.getLobbySpawns().length > 0) {
							if (a.getLobbySpawns()[0] == null) {
								sender.sendMessage(Broadcast.TAG + SMeth.setColours("Team Lobby Spawns: &dNot set"));
							} else {
								sender.sendMessage(Broadcast.TAG + SMeth.setColours("Team Lobby Spawns: Set!"));
							}
						} else {
							sender.sendMessage(Broadcast.TAG + SMeth.setColours("Team Lobby Spawns: &dNot set"));
						}
						
						if (a.isEnabled()) {
							sender.sendMessage(Broadcast.TAG + SMeth.setColours("State: &aEnabled"));
						} else {
							sender.sendMessage(Broadcast.TAG + SMeth.setColours("State: &cDisabled"));
						}
						
						if (a.getTeamSpawns().get(a.getTeamCount() - 1).size() > 0) {
							sender.sendMessage(Broadcast.TAG + SMeth.setColours("Player Spawns: &dTeam Spawns"));
						} else if (a.getPlayerSpawns().size() < 1) {
							sender.sendMessage(Broadcast.TAG + SMeth.setColours("Player Spawns: &dNot set"));
						} else {
							sender.sendMessage(Broadcast.TAG + SMeth.setColours("Player Spawns: &d" + a.getPlayerSpawns().size()));
						}
						
						if (a.getCrystalSpawns().size() < 1) {
							sender.sendMessage(Broadcast.TAG + SMeth.setColours("Crystal Spawns: &dNot set"));
						} else {
							sender.sendMessage(Broadcast.TAG + SMeth.setColours("Crystal Spawns: &d" + a.getCrystalSpawns().size()));
						}
						
						if (a.getItemSpawns().size() < 1) {
							sender.sendMessage(Broadcast.TAG + SMeth.setColours("Item Spawns: &dNot set"));
						} else {
							sender.sendMessage(Broadcast.TAG + SMeth.setColours("Item Spawns: &d" + a.getItemSpawns().size()));
						}
					} else {
						sender.sendMessage(ChatColor.RED + "[!!] Usage: /cq check <arena#|arenaname>");
					}
				} else {
					sender.sendMessage(Broadcast.NO_PERMISSION);
				}
			}
			/*
			 * LOBBY
			 */
			else if (args[0].equalsIgnoreCase("lobby")) {
				if (sender instanceof Player) {
					Player player = (Player) sender;
					if (!plugin.am.isInGame(player)) {
						player.teleport(plugin.am.getLobby());
					} else {
						sender.sendMessage(ChatColor.RED + "[!!] You are already in-game!");
					}
				} else {
					sender.sendMessage(Broadcast.ONLY_IN_GAME);
				}
			}
			/*
			 * RELOAD
			 */
			else if (args[0].equalsIgnoreCase("reload")) {
				if (sender.hasPermission("crystalquest.admin")) {
					plugin.reloadConfig();
					for (Arena a : plugin.getArenaManager().getArenas()) {
						for (Player p : a.getPlayers()) {
							p.sendMessage(ChatColor.RED + "[!!] You have been kicked because CrystalQuest has been reloaded!");
						}
						a.declareWinner();
						a.resetArena();
					}
					sender.sendMessage(Broadcast.TAG + "Config.yml has been reloaded!");
				} else {
					sender.sendMessage(Broadcast.NO_PERMISSION);
				}
			}
			/*
			 * RESET ARENA
			 */
			else if (args[0].equalsIgnoreCase("reset")) {
				if (sender.hasPermission("crystalquest.admin")) {
					if (args.length >= 2) {
						try {
							Arena a;
							try {
								a = plugin.am.getArena(Integer.parseInt(args[1]) - 1);
							} catch (Exception e) {
								a = plugin.am.getArena(args[1]);
							}
							a.resetArena();
							sender.sendMessage(Broadcast.TAG + "The arena has been reset!");
						} catch (Exception e) {
							sender.sendMessage(ChatColor.RED + "[!!] Could not reset the arena!");
							e.printStackTrace();
							plugin.getLogger().info("NullPointerException? Make sure the arena exists!");
						}
					} else {
						sender.sendMessage(ChatColor.RED + "[!!] Usage: /cq reset <arena#|arenaname>");
					}
				} else {
					sender.sendMessage(Broadcast.NO_PERMISSION);
				}
			}
			/*
			 * HELP
			 */
			else if (args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?")) {
				if (args.length >= 2) {
					if (args[1].equalsIgnoreCase("setup") && sender.hasPermission("crystalquest.admin")) {
						Help.showSetup(sender);
					} else {
						Help.showDefault(sender);
					}
				} else {
					Help.showDefault(sender);
				}
			}
			/*
			 * WAND
			 */
			else if (args[0].equalsIgnoreCase("wand")) {
				if (sender instanceof Player) {
					if (sender.hasPermission("crystalquest.admin")) {
						Player p = (Player) sender;
						ItemStack is = new ItemStack(Material.STICK, 1);
						ItemMeta im = is.getItemMeta();
						im.setDisplayName(Broadcast.TAG + "Wand");
						List<String> lore = new ArrayList<String>();
						lore.add("Left click to set position 1");
						lore.add("Right click to set position 2");
						im.setLore(lore);
						is.setItemMeta(im);
						p.getInventory().addItem(is);
						p.sendMessage(Broadcast.TAG + "Here is your wand! Left click = Pos1, Right Click = Pos2");
					} else {
						sender.sendMessage(Broadcast.NO_PERMISSION);
					}
				} else {
					sender.sendMessage(ChatColor.RED + "[!!] " + Broadcast.ONLY_IN_GAME);
				}
			}
			/*
			 * PROTECT
			 */
			else if (args[0].equalsIgnoreCase("protect")) {
				if (sender.hasPermission("crystalquest.admin")) {
					boolean canContinue = true;
					if (args.length >= 3) {
						if (args[2].equalsIgnoreCase("remove")) {
							try {
								Arena a;
								try {
									a = plugin.am.getArena(Integer.parseInt(args[1]) - 1);
								} catch (Exception e) {
									a = plugin.am.getArena(args[1]);
								}
								a.setProtection(null);
								sender.sendMessage(Broadcast.TAG + "Removed the protection from " +
										ChatColor.GRAY + "Arena " + args[1]);
								canContinue = false;
							} catch (Exception ex) { }
						}
					}
					if (args.length >= 2 && canContinue) {
						try {
							Arena a;
							try {
								a = plugin.am.getArena(Integer.parseInt(args[1]) - 1);
							} catch (Exception e) {
								a = plugin.am.getArena(args[1]);
							}
							plugin.prot.protectArena(a);
							sender.sendMessage(Broadcast.TAG + "Protected " + ChatColor.GRAY + "Arena " + args[1]);
						} catch (Exception e) {
							sender.sendMessage(ChatColor.RED + "[!!] Could not protect the arena!");
							e.printStackTrace();
							plugin.getLogger().info("NullPointerException? Make sure the arena exists!");
						}
					} else if (canContinue) {
						sender.sendMessage(ChatColor.RED + "[!!] Usage: /cq protect <arena> [remove]");
					}
				} else {
					sender.sendMessage(Broadcast.NO_PERMISSION);
				}
			}
			/*
			 * POS
			 */
			else if (args[0].equalsIgnoreCase("pos")) {
				if (sender instanceof Player) {
					if (sender.hasPermission("crystalquest.admin")) {
						if (args.length >= 2) {
							try {
								if (Integer.parseInt(args[1]) == 1) {
									plugin.prot.pos1 = ((Player) sender).getLocation();
									sender.sendMessage(Broadcast.TAG + "Position " + args[1] + " is set to " +
											ChatColor.GRAY + plugin.prot.pos1.getX() + ", " + plugin.prot.pos1.getY() +
											", " + plugin.prot.pos1.getX());
								} else {
									plugin.prot.pos2 = ((Player) sender).getLocation();
									sender.sendMessage(Broadcast.TAG + "Position " + args[1] + " is set to " +
											ChatColor.GRAY + plugin.prot.pos2.getX() + ", " + plugin.prot.pos2.getY() +
											", " + plugin.prot.pos2.getX());
								}
							} catch (Exception ex) {
								sender.sendMessage(ChatColor.RED + "[!!] Could not set position " + args[1]);
							}
						} else {
							sender.sendMessage(ChatColor.RED + "[!!] Usage: /cq pos <1|2>");
						}
					} else {
						sender.sendMessage(Broadcast.NO_PERMISSION);
					}
				} else {
					sender.sendMessage(ChatColor.RED + "[!!]" + Broadcast.ONLY_IN_GAME);
				}
			}
			/*
			 * DOUBLEJUMP
			 */
			else if (args[0].equalsIgnoreCase("doublejump")) {
				if (sender.hasPermission("crystalquest.admin")) {
					if (args.length >= 2) {
						try {
							Arena a;
							try {
								a = plugin.am.getArena(Integer.parseInt(args[1]) - 1);
							} catch (Exception e) {
								a = plugin.am.getArena(args[1]);
							}
							boolean canDJ = a.canDoubleJump();
							if (canDJ) {
								a.setDoubleJump(false);
							} else {
								a.setDoubleJump(true);
							}
							sender.sendMessage(Broadcast.TAG + "Set DoubleJump in Arena " + args[1] +
									" to " + ChatColor.GRAY +  a.canDoubleJump());
						} catch (Exception e) {
							sender.sendMessage(ChatColor.RED + "[!!] Could not disable the arena!");
							e.printStackTrace();
							plugin.getLogger().info("NullPointerException? Make sure the arena exists!");
						}
					} else {
						sender.sendMessage(ChatColor.RED + "[!!] Usage: /cq doublejump <arena>");
					}
				} else {
					sender.sendMessage(Broadcast.NO_PERMISSION);
				}
				plugin.signHandler.updateSigns();
			}
			/*
			 * If not given a valid command.
			 */
			else {
				if (sender instanceof Player) {
					sender.sendMessage(ChatColor.RED + "[!!] Invalid command! Use /cq help for a list of commands.");
				} else {
					plugin.getLogger().info("Invalid command! Use 'cq help' for a list of commands.");
				}
			}
			
		} else {
			Broadcast.showAbout(sender);
		}
		
		plugin.signHandler.updateSigns();
		
		return false;
	}
	
}