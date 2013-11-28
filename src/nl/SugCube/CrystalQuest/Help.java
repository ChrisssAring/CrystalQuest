package nl.SugCube.CrystalQuest;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Help {

	public static void showSetup(CommandSender s) {
		
		if (s.hasPermission("crystalquest.admin")) {
			s.sendMessage(Broadcast.HELP + ChatColor.YELLOW + "<>--------------" + ChatColor.LIGHT_PURPLE + 
					"SETUP-HELP" + ChatColor.YELLOW + "--------------<>");
			s.sendMessage(Broadcast.HELP + "/cq setlobby" + ChatColor.YELLOW + " Set the main Lobbyspawn");
			s.sendMessage(Broadcast.HELP + "/cq createarena" + ChatColor.YELLOW + " Create a new Arena");
			s.sendMessage(Broadcast.HELP + "/cq setname <arena> <name>" + ChatColor.YELLOW + " Give a name to an Arena");
			s.sendMessage(Broadcast.HELP + "/cq teamlobby <arena> <teamID>" + ChatColor.YELLOW + " Set the Team's Lobby");
			s.sendMessage(Broadcast.HELP + "/cq setteams <arena> <amount>" + ChatColor.YELLOW +
					" Set the amount of teams in an Arena");
			s.sendMessage(Broadcast.HELP + "/cq minplayers <arena> <amount>" + ChatColor.YELLOW +
					" Sets the minimum amount of players for the Arena to start");
			s.sendMessage(Broadcast.HELP + "/cq maxplayers <arena> <amount>" + ChatColor.YELLOW +
					" Sets the maximum amount of players in an Arena");
			s.sendMessage(Broadcast.HELP + "/cq spawn <arena> [clear]" + ChatColor.YELLOW +
					" Set a Playerspawn in the arena.");
			s.sendMessage(Broadcast.HELP + "/cq crystalspawn <arena> [clear]" + ChatColor.YELLOW +
					" Set a Crystalspawn in the arena.");
			s.sendMessage(Broadcast.HELP + "/cq itemspawn <arena> [clear]" + ChatColor.YELLOW +
					" Set an Itemspawn in the arena.");
			s.sendMessage(Broadcast.HELP + "/cq doublejump <arena>" + ChatColor.YELLOW + " Toggle if people can double jump");
			s.sendMessage(Broadcast.HELP + "/cq check <arena>" + ChatColor.YELLOW + " See info about the Arena");
			s.sendMessage(Broadcast.HELP + "/cq reset <arena>" + ChatColor.YELLOW + " Resets an Arena");
			s.sendMessage(Broadcast.HELP + "/cq wand" + ChatColor.YELLOW + " Gives you the wand");
			s.sendMessage(Broadcast.HELP + "/cq protect <arena> [remove]" + ChatColor.YELLOW + " (Un)Protects an Arena");
			s.sendMessage(Broadcast.HELP + "/cq pos <1|2>" + ChatColor.YELLOW + " Sets position 1 or 2.");
		}
		if (!(s instanceof Player)) {
			s.sendMessage(Broadcast.HELP + "/cq hardreset" + ChatColor.YELLOW + " Resets ALL data");
		}
		
	}
	
	public static void showDefault(CommandSender s) {

		s.sendMessage(Broadcast.HELP + ChatColor.YELLOW + "<>--------------" + ChatColor.LIGHT_PURPLE + 
				"CQ-HELP" + ChatColor.YELLOW + "--------------<>");
		s.sendMessage(Broadcast.HELP + "/cq lobby" + ChatColor.YELLOW + " Join the CQ-Lobby");
		s.sendMessage(Broadcast.HELP + "/cq quit" + ChatColor.YELLOW + " Leave a CQ-Game");
		
		if (s.hasPermission("crystalquest.enable") || s.hasPermission("crystalquest.staff") ||
				s.hasPermission("crystalquest.admin")) {
			s.sendMessage(Broadcast.HELP + "/cq enable <arena>" + ChatColor.YELLOW + " Enables an Arena");
		}
		if (s.hasPermission("crystalquest.disable") || s.hasPermission("crystalquest.staff") ||
				s.hasPermission("crystalquest.admin")) {
			s.sendMessage(Broadcast.HELP + "/cq disable <arena>" + ChatColor.YELLOW + " Disables an Arena");
		}
		if (s.hasPermission("crystalquest.forcestart") || s.hasPermission("crystalquest.staff") ||
				s.hasPermission("crystalquest.admin")) {
			s.sendMessage(Broadcast.HELP + "/cq forcestart <arena>" + ChatColor.YELLOW + " Force an Arena to start");
		}
		if (s.hasPermission("crystalquest.kick") || s.hasPermission("crystalquest.staff") ||
				s.hasPermission("crystalquest.admin")) {
			s.sendMessage(Broadcast.HELP + "/cq kick <player>" + ChatColor.YELLOW + " Kicks a player from an Arena");
		}
		if (s.hasPermission("crystalquest.admin")) {
			s.sendMessage(Broadcast.HELP + "/cq reload" + ChatColor.YELLOW + " Reloads the config.yml");
			s.sendMessage(Broadcast.HELP + ChatColor.YELLOW + "Use " + ChatColor.LIGHT_PURPLE + "/cq help setup" +
					ChatColor.YELLOW + " to see all set-up commands.");
		}
		
	}
	
}