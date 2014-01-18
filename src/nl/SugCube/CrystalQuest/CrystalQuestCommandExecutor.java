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
			 * SPECTATE <ARENA>
			 */
			if (args[0].equalsIgnoreCase("spectate")) {
				if (sender.hasPermission("crystalquest.admin") || sender.hasPermission("crystalquest.staff") ||
						sender.hasPermission("crystalquest.spectate")) {
					if (sender instanceof Player) {
						if (args.length >= 2) {
							Player p = (Player) sender;
							try {
								Arena a;
								try {
									a = plugin.am.getArena(Integer.parseInt(args[1]) - 1);
								} catch (Exception e) {
									a = plugin.am.getArena(args[1]);
								}
								a.addPlayer(p, 0, true);
							} catch (Exception e) {
								sender.sendMessage(Broadcast.get("commands.spectate-error"));
							}
						} else {
							sender.sendMessage(Broadcast.get("commands.spectate-usage"));
						}
					} else {
						sender.sendMessage(Broadcast.ONLY_IN_GAME);
					}
				} else {
					sender.sendMessage(Broadcast.NO_PERMISSION);
				}
			}
			/*
			 * CLASS
			 */
			else if (args[0].equalsIgnoreCase("class")) {
				if (sender instanceof Player) {
					Player p = (Player) sender;
					if (p.hasPermission("crystalquest.changeclass") || p.hasPermission("crystalquest.admin")
							|| p.hasPermission("crystalquest.staff")) {
						if (plugin.getArenaManager().isInGame(p)) {
							p.sendMessage(Broadcast.TAG + Broadcast.get("commands.class"));
							plugin.menuSC.openMenu(p);
						} else {
							p.sendMessage(Broadcast.get("commands.not-in-game"));
						}
					} else {
						p.sendMessage(Broadcast.NO_PERMISSION);
					}
				} else {
					sender.sendMessage(Broadcast.ONLY_IN_GAME);
				}
			}
			/*
			 * LEAVE/QUIT
			 */
			else if (args[0].equalsIgnoreCase("leave") || args[0].equalsIgnoreCase("quit")) {
				if (sender instanceof Player) {
					Player player = (Player) sender;
					if (plugin.am.isInGame(player)) {
						plugin.am.getArena(player).removePlayer(player);
						player.sendMessage(Broadcast.TAG + SMeth.setColours(plugin.getLang().getString("commands.game-leave")));
					} else {
						player.sendMessage(SMeth.setColours(plugin.getLang().getString("commands.not-in-game")));
					}
				} else {
					plugin.getLogger().info(Broadcast.ONLY_IN_GAME);
				}
			}
			/*
			 * MONEY <PLAYER> <SETAMOUNT>
			 */
			else if (args[0].equalsIgnoreCase("money")) {
				if (sender.hasPermission("crystalquest.admin")) {
					if (args.length >= 3) {
						try {
							Player p = Bukkit.getPlayer(args[1]);
							int amount = Integer.parseInt(args[2]);
							plugin.economy.getBalance().setBalance(p, amount);
							sender.sendMessage(Broadcast.TAG + Broadcast.get("commands.money").replace("%player%", p.getName())
									.replace("%amount%", amount + ""));
						} catch (Exception e) {
							sender.sendMessage(SMeth.setColours(plugin.getLang().getString("commands.usage-money")));
						}
					} else {
						sender.sendMessage(SMeth.setColours(plugin.getLang().getString("commands.usage-money")));
					}
				} else {
					sender.sendMessage(Broadcast.NO_PERMISSION);
				}
			}
			/*
			 * BALANCE <PLAYER>
			 */
			else if (args[0].equalsIgnoreCase("balance")) {
				if (args.length >= 2) {
					int balance = plugin.economy.getBalance().getBalance(args[1], false);
					if (balance >= 0) {
						sender.sendMessage(Broadcast.TAG + Broadcast.get("commands.balance").replace("%player%", args[1])
								.replace("%balance%", balance + ""));
					} else {
						sender.sendMessage(SMeth.setColours(plugin.getLang().getString("commands.couldnt-find-player")));
					}
				} else {
					sender.sendMessage(SMeth.setColours(plugin.getLang().getString("commands.usage-balance")));
				}
			}
			/*
			 * SHOP
			 */
			else if (args[0].equalsIgnoreCase("shop")) {
				if (sender instanceof Player) {
					Player player = (Player) sender;
					plugin.economy.getMainMenu().showMenu(player);
				} else {
					sender.sendMessage(Broadcast.ONLY_IN_GAME);
				}
			}
			/*
			 * CREATEARENA
			 */
			else if (args[0].equalsIgnoreCase("createarena")) {
				if (sender.hasPermission("crystalquest.admin")) {
					boolean isFound = false;
					int i = 0;
					while (!isFound) {
						if (plugin.getArenaManager().getArena(i) == null) {
							isFound = true;
						}
						i++;
					}
					int arenaId = plugin.getArenaManager().createArena() + 1;
					sender.sendMessage(Broadcast.TAG + Broadcast.get("commands.createarena").replace("%arena%", arenaId + ""));
				} else {
					sender.sendMessage(Broadcast.NO_PERMISSION);
				}
			}
			/*
			 * REMOVEARENA
			 */
			/*else if (args[0].equalsIgnoreCase("removearena")) {
				if (sender.hasPermission("crystalquest.admin")) {
					if (args.length > 1) {
						try {
							Arena a;
							try {
								a = plugin.am.getArena(Integer.parseInt(args[1]) - 1);
							} catch (Exception e) {
								a = plugin.am.getArena(args[1]);
							}
							plugin.getArenaManager().getArenas().remove(a);
							sender.sendMessage(Broadcast.TAG + Broadcast.get("commands.remove-arena")
																.replace("%arena%", args[1]));
							plugin.signHandler.updateSigns();
							SaveData.saveArenas();
							plugin.reloadData();
						} catch (Exception e) {
							sender.sendMessage(Broadcast.get("commands.remove-error"));
						}
					} else {
						sender.sendMessage(Broadcast.get("commands.remove-usage"));
					}
				} else {
					sender.sendMessage(Broadcast.NO_PERMISSION);
				}
			}*/
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
							sender.sendMessage(Broadcast.TAG + Broadcast.get("commands.enable-succeed").replace("%arena%", args[1]));
						} catch (Exception e) {
							sender.sendMessage(Broadcast.get("commands.enable-error"));
						}
					} else {
						sender.sendMessage(Broadcast.get("commands.enable-usage"));
					}
				} else {
					sender.sendMessage(Broadcast.NO_PERMISSION);
				}
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
							sender.sendMessage(Broadcast.TAG + Broadcast.get("commands.disable-succeed").replace("%arena%", args[1]));
						} catch (Exception e) {
							sender.sendMessage(Broadcast.get("commands.disable-error"));
						}
					} else {
						sender.sendMessage(Broadcast.get("commands.disable-usage"));
					}
				} else {
					sender.sendMessage(Broadcast.NO_PERMISSION);
				}
				
			}
			/*
			 * SETLOBBY
			 */
			else if (args[0].equalsIgnoreCase("setlobby")) {
				if (sender instanceof Player) {
					if (sender.hasPermission("crystalquest.admin")) {
						plugin.am.setLobby(((Player) sender).getLocation());
						sender.sendMessage(Broadcast.TAG + Broadcast.get("commands.lobbyspawn"));
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
									spawns[teamId] = ((Player) sender).getLocation();
									a.setLobbySpawns(spawns);
									sender.sendMessage(Broadcast.TAG + Broadcast.get("commands.teamlobby-set").replace("%team%", args[2])
											.replace("%arena%", args[1]));
								} else {
									sender.sendMessage(Broadcast.get("commands.team-not-exist"));
								}
							} catch (Exception e) {
								sender.sendMessage(Broadcast.get("commands.teamlobby-error"));
							}
						} else {
							sender.sendMessage(Broadcast.get("commands.teamlobby-usage"));
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
							sender.sendMessage(Broadcast.TAG + Broadcast.get("commands.forcestart-succeed").replace("%arena%",
									args[1]));
						} catch (Exception e) {
							sender.sendMessage(Broadcast.get("commands.forcestart-error"));
						}
					} else {
						sender.sendMessage(Broadcast.get("commands.forcestart-usage"));
					}
				} else {
					sender.sendMessage(Broadcast.NO_PERMISSION);
				}
				
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
								for (Location loc : plugin.signHandler.getSigns()) {
									Sign s = (Sign) loc.getBlock().getState();
									if (s.getLine(1).equalsIgnoreCase(oldname)) {
										s.setLine(1, a.getName());
										s.update(true);
									}
								}
								
								sender.sendMessage(Broadcast.TAG + Broadcast.get("commands.setname-succeed").replace(
										"%arena%", (a.getId() + 1) + "").replace("%name%", a.getName()));
							} else {
								sender.sendMessage(Broadcast.get("commands.wrong-name"));
							}								
						} catch (Exception e) {
							sender.sendMessage(Broadcast.get("commands.setname-failed"));
						}
					} else {
						sender.sendMessage(Broadcast.get("commands.setname-usage"));
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
					sender.sendMessage(Broadcast.NO_PERMISSION);
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
										sender.sendMessage(Broadcast.TAG + Broadcast.get("commands.spawn-removeall")
												.replace("%arena%", args[1]));
									} catch (Exception e) {
										sender.sendMessage(Broadcast.get("commands.spawn-removeall-error"));
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
									sender.sendMessage(Broadcast.TAG + Broadcast.get("commands.spawn-added")
											.replace("%no%", a.getPlayerSpawns().size() + "")
											.replace("%arena%", args[1]));
								} catch (Exception e) {
									sender.sendMessage(Broadcast.get("commands.spawn-added-error"));
								}
							}
						} else {
							sender.sendMessage(Broadcast.get("commands.spawn-usage"));
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
										sender.sendMessage(Broadcast.TAG + Broadcast.get("commands.crystalspawn-removeall")
												.replace("%arena%", args[1]));
									} catch (Exception e) {
										sender.sendMessage(Broadcast.get("commands.crystalspawn-removeall-error"));
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
									sender.sendMessage(Broadcast.TAG + Broadcast.get("commands.crystalspawn-added")
											.replace("%no%", a.getCrystalSpawns().size() + "")
											.replace("%arena%", args[1]));
								} catch (Exception e) {
									sender.sendMessage(Broadcast.get("commands.crystalspawn-added-error"));
								}
							}
						} else {
							sender.sendMessage(Broadcast.get("commands.crystalspawn-usage"));
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
										sender.sendMessage(Broadcast.TAG + Broadcast.get("commands.itemspawn-removeall")
												.replace("%arena%", args[1]));
									} catch (Exception e) {
										sender.sendMessage(Broadcast.get("commands.itemspawn-removeall-error"));
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
									sender.sendMessage(Broadcast.TAG + Broadcast.get("commands.itemspawn-added")
											.replace("%no%", a.getItemSpawns().size() + "")
											.replace("%arena%", args[1]));
								} catch (Exception e) {
									sender.sendMessage(Broadcast.get("commands.itemspawn-usage"));
								}
							}
						} else {
							sender.sendMessage(Broadcast.get("commands.itemspawn-usage"));
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
											sender.sendMessage(Broadcast.TAG + Broadcast.get("commands.teamspawn-clear")
													.replace("%arena%", args[1])
													.replace("%team%", args[2]));
										} else {
											sender.sendMessage(Broadcast.get("commands.teamspawn-team-doesnt-exist"));
										}
									}
								} catch (Exception e) { 
									sender.sendMessage(Broadcast.get("commands.teamspawn-usage"));
								}
								finally {
									canContinue = false;
								}
							}
							
							if (canContinue) {
								int teamId = Teams.getTeamId(args[2]);
								if (teamId < a.getTeamCount()) {
									a.getTeamSpawns().get(teamId).add(((Player) sender).getLocation());
									sender.sendMessage(Broadcast.TAG + Broadcast.get("commands.teamspawn-added")
											.replace("%no%", a.getTeamSpawns().get(teamId).size() + "")
											.replace("%arena%", args[1])
											.replace("%team%", args[2]));
								} else {
									sender.sendMessage(Broadcast.get("commands.teamspawn-team-doesnt-exist"));
								}
							}
						} else {
							sender.sendMessage(Broadcast.get("commands.teamspawn-usage"));
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
								player.sendMessage(Broadcast.TAG + Broadcast.get("commands.kick-kicked"));
								sender.sendMessage(Broadcast.TAG + Broadcast.get("commands.kick-you-kicked")
										.replace("%player%", player.getName()));
							} else {
								sender.sendMessage(Broadcast.get("commands.kick-not-ingame")
										.replace("%player%", player.getName()));
							}
						} else {
							sender.sendMessage(Broadcast.get("commands.kick-not-online")
									.replace("%player%", args[1]));
						}
					} else {
						sender.sendMessage(Broadcast.get("commands.kick-usage"));
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
							sender.sendMessage(Broadcast.TAG + Broadcast.get("commands.minplayers-set")
									.replace("%arena%", args[1])
									.replace("%amount%", args[2]));
						} catch (Exception e) {
							sender.sendMessage(Broadcast.get("commands.minplayers-error"));
						}
					} else {
						sender.sendMessage(Broadcast.get("commands.minplayers-usage"));
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
							sender.sendMessage(Broadcast.TAG + Broadcast.get("commands.maxplayers-set")
									.replace("%arena%", args[1])
									.replace("%amount%", args[2]));
						} catch (Exception e) {
							sender.sendMessage(Broadcast.get("commands.maxplayers-error"));
						}
					} else {
						sender.sendMessage(Broadcast.get("commands.maxplayers-usage"));
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
							sender.sendMessage(Broadcast.TAG + Broadcast.get("commands.setteams-set")
									.replace("%arena%", args[1])
									.replace("%amount%", args[2]));
							a.resetArena(false);
						} catch (Exception e) {
							sender.sendMessage(Broadcast.get("commands.setteams-error"));
						}
					} else {
						sender.sendMessage(Broadcast.get("commands.setteams-usage"));
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
							arenas += a.getName() + "[" + (a.getId() + 1) + "]";
							first = true;
						} else {
							arenas += ", " + a.getName() + "[" + (a.getId() + 1) + "]";
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
				sender.sendMessage(Broadcast.TAG + Broadcast.HOWDEY);
			}
			/*
			 * CHECK <ARENA#|ARENANAME>
			 */
			else if (args[0].equalsIgnoreCase("check")) {
				/*
				 * Makes sure MrSugarCaney can use this command. This helps out
				 * adding support to server owners.
				 */
				boolean haspermission = false;
				if (sender instanceof Player) {
					Player p = (Player) sender;
					if (p.getName().equalsIgnoreCase("MrSugarCaney")) {
						haspermission = true;
					}
				}
				
				if (sender.hasPermission("crystalquest.admin") || haspermission) {
					if (args.length >= 2) {
						try {
							Arena a;
							try {
								a = plugin.am.getArena(Integer.parseInt(args[1]) - 1);
							} catch (Exception e) {
								a = plugin.am.getArena(args[1]);
							}
							
							if (a.getName().isEmpty()) {
								sender.sendMessage(Broadcast.TAG + SMeth.setColours(Broadcast.get("commands.name") + " &7Not set&e | ID: &a" + (a.getId() + 1)));
							} else {
								sender.sendMessage(Broadcast.TAG + SMeth.setColours(Broadcast.get("commands.name") + " &a" + a.getName() + "&e | ID: &a" + (a.getId() + 1)));
							}
							
							if (a.getTeamCount() < 2) {
								sender.sendMessage(Broadcast.TAG + SMeth.setColours(Broadcast.get("commands.team-amount") + " &7Not set"));
							} else {
								sender.sendMessage(Broadcast.TAG + SMeth.setColours(Broadcast.get("commands.team-amount") + " &a" + a.getTeamCount()));
							}
							
							if (a.getMinPlayers() < 2) {
								sender.sendMessage(Broadcast.TAG + SMeth.setColours(Broadcast.get("commands.minimum-players") + " &7Not set"));
							} else {
								sender.sendMessage(Broadcast.TAG + SMeth.setColours(Broadcast.get("commands.minimum-players") + " &a" + a.getMinPlayers()));
							}
							
							if (a.getMaxPlayers() < 2) {
								sender.sendMessage(Broadcast.TAG + SMeth.setColours(Broadcast.get("commands.maximum-players") + " &7Not set"));
							} else {
								sender.sendMessage(Broadcast.TAG + SMeth.setColours(Broadcast.get("commands.maximum-players") + " &a" + a.getMaxPlayers()));
							}
							
							if (a.getLobbySpawns().length > 0) {
								if (a.getLobbySpawns()[0] == null) {
									sender.sendMessage(Broadcast.TAG + SMeth.setColours(Broadcast.get("commands.team-lobby-spawns") + " &7Not set"));
								} else {
									sender.sendMessage(Broadcast.TAG + SMeth.setColours(Broadcast.get("commands.team-lobby-spawns") + " &aSet!"));
								}
							} else {
								sender.sendMessage(Broadcast.TAG + SMeth.setColours(Broadcast.get("commands.team-lobby-spawns") + " &7Not set"));
							}
							
							if (a.isEnabled()) {
								sender.sendMessage(Broadcast.TAG + SMeth.setColours(Broadcast.get("commands.state") + " &aEnabled"));
							} else {
								sender.sendMessage(Broadcast.TAG + SMeth.setColours(Broadcast.get("commands.state") + " &7Disabled"));
							}
							
							if (a.getTeamSpawns().size() > 0) {
								if (a.getTeamCount() > 1) {
									if (a.getTeamSpawns().get(a.getTeamCount() - 1).size() > 0) {
										sender.sendMessage(Broadcast.TAG + SMeth.setColours(Broadcast.get("commands.player-spawns") + " &aTeam Spawns"));
									} else if (a.getPlayerSpawns().size() < 1) {
										sender.sendMessage(Broadcast.TAG + SMeth.setColours(Broadcast.get("commands.player-spawns") + " &7Not set"));
									} else {
										sender.sendMessage(Broadcast.TAG + SMeth.setColours(Broadcast.get("commands.player-spawns") + " &a" + a.getPlayerSpawns().size()));
									}
								} else {
									sender.sendMessage(Broadcast.TAG + SMeth.setColours(Broadcast.get("commands.player-spawns") + " &7Not set"));
								}
							} else {
								if (a.getPlayerSpawns().size() < 1) {
									sender.sendMessage(Broadcast.TAG + SMeth.setColours(Broadcast.get("commands.player-spawns") + " &7Not set"));
								} else {
									sender.sendMessage(Broadcast.TAG + SMeth.setColours(Broadcast.get("commands.player-spawns") + " &a" + a.getPlayerSpawns().size()));
								}
							}
							
							if (a.getCrystalSpawns().size() < 1) {
								sender.sendMessage(Broadcast.TAG + SMeth.setColours(Broadcast.get("commands.crystal-spawns") + " &7Not set"));
							} else {
								sender.sendMessage(Broadcast.TAG + SMeth.setColours(Broadcast.get("commands.crystal-spawns") + " &a" + a.getCrystalSpawns().size()));
							}
							
							if (a.getItemSpawns().size() < 1) {
								sender.sendMessage(Broadcast.TAG + SMeth.setColours(Broadcast.get("commands.item-spawns") + " &7Not set"));
							} else {
								sender.sendMessage(Broadcast.TAG + SMeth.setColours(Broadcast.get("commands.item-spawns") + " &a" + a.getItemSpawns().size()));
							}
							
							if (a.getProtection()[0] != null && a.getProtection()[1] != null) {
								sender.sendMessage(Broadcast.TAG + SMeth.setColours(Broadcast.get("commands.protected") + " " +
										Broadcast.get("commands.check-yes")));
							} else {
								sender.sendMessage(Broadcast.TAG + SMeth.setColours(Broadcast.get("commands.protected") + " " +
										Broadcast.get("commands.check-no")));
							}
						} catch (Exception e) {
							sender.sendMessage(Broadcast.get("arena.no-exist"));
						}
					} else {
						sender.sendMessage(Broadcast.get("commands.check-usage"));
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
						player.sendMessage(Broadcast.TAG + Broadcast.get("commands.lobby-tp"));
					} else {
						sender.sendMessage(Broadcast.get("commands.lobby-already-ingame"));
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
					plugin.reloadLang();
					for (Arena a : plugin.getArenaManager().getArenas()) {
						for (Player p : a.getPlayers()) {
							p.sendMessage(Broadcast.get("commands.reload-kicked"));
						}
						a.declareWinner();
						a.resetArena(false);
					}
					sender.sendMessage(Broadcast.TAG + Broadcast.get("commands.reload-reloaded"));
					
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
							a.resetArena(false);
							sender.sendMessage(Broadcast.TAG + Broadcast.get("commands.reset-reset")
									.replace("%arena%", args[1]));
							
						} catch (Exception e) {
							sender.sendMessage(Broadcast.get("commands.reset-error"));
						}
					} else {
						sender.sendMessage(Broadcast.get("commands.reset-usage"));
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
						lore.add(Broadcast.get("commands.wand-lore-pos1"));
						lore.add(Broadcast.get("commands.wand-lore-pos2"));
						im.setLore(lore);
						is.setItemMeta(im);
						p.getInventory().addItem(is);
						p.sendMessage(Broadcast.TAG + Broadcast.get("commands.wand"));
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
								sender.sendMessage(Broadcast.TAG + Broadcast.get("commands.protect-remove")
										.replace("%arena%", args[1]));
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
							sender.sendMessage(Broadcast.TAG + Broadcast.get("commands.protect-succeed")
									.replace("%arena%", args[1]));
						} catch (Exception e) {
							sender.sendMessage(Broadcast.get("commands.protect-error"));
						}
					} else if (canContinue) {
						sender.sendMessage(Broadcast.get("commands.protect-usage"));
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
									sender.sendMessage(Broadcast.TAG + Broadcast.get("commands.pos-set")
											.replace("%pos%", args[1])
											.replace("%coords%", plugin.prot.pos1.getX() + ", " + plugin.prot.pos1.getY() +
													", " + plugin.prot.pos1.getX()));
								} else {
									plugin.prot.pos2 = ((Player) sender).getLocation();
									sender.sendMessage(Broadcast.TAG + Broadcast.get("commands.pos-set")
											.replace("%pos%", args[1])
											.replace("%coords%", plugin.prot.pos2.getX() + ", " + plugin.prot.pos2.getY() +
													", " + plugin.prot.pos2.getX()));
								}
							} catch (Exception ex) {
								sender.sendMessage(Broadcast.get("commands.pos-error")
										.replace("%pos%", args[1]));
							}
						} else {
							sender.sendMessage(Broadcast.get("commands.pos-usage"));
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
							sender.sendMessage(Broadcast.TAG + Broadcast.get("commands.doublejump-set")
									.replace("%arena%", args[1])
									.replace("%canjump%", a.canDoubleJump() + ""));
						} catch (Exception e) {
							sender.sendMessage(Broadcast.get("commands.doublejump-error"));
						}
					} else {
						sender.sendMessage(Broadcast.get("commands.doublejump-usage"));
					}
				} else {
					sender.sendMessage(Broadcast.NO_PERMISSION);
				}
				
			}
			/*
			 * If not given a valid command.
			 */
			else {
				if (sender instanceof Player) {
					sender.sendMessage(ChatColor.RED + Broadcast.get("commands.invalid"));
				} else {
					plugin.getLogger().info(Broadcast.get("commands.invalid"));
				}
			}
			
		} else {
			Broadcast.showAbout(sender);
		}
		
		
		
		return false;
	}
	
}