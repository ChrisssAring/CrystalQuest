package nl.SugCube.CrystalQuest;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.OfflinePlayer;

public class Teams {

	public static final OfflinePlayer GREEN = Bukkit.getOfflinePlayer(ChatColor.GREEN + "Team Green");
	public static final OfflinePlayer ORANGE = Bukkit.getOfflinePlayer(ChatColor.GOLD + "Team Orange");
	public static final OfflinePlayer YELLOW = Bukkit.getOfflinePlayer(ChatColor.YELLOW + "Team Yellow");
	public static final OfflinePlayer RED = Bukkit.getOfflinePlayer(ChatColor.RED + "Team Red");
	public static final OfflinePlayer BLUE = Bukkit.getOfflinePlayer(ChatColor.AQUA + "Team Blue");
	public static final OfflinePlayer MAGENTA = Bukkit.getOfflinePlayer(ChatColor.LIGHT_PURPLE + "Team Magenta");
	
	public static final String GREEN_NAME = ChatColor.GREEN + "Team Green";
	public static final String ORANGE_NAME = ChatColor.GOLD + "Team Orange";
	public static final String YELLOW_NAME = ChatColor.YELLOW + "Team Yellow";
	public static final String RED_NAME = ChatColor.RED + "Team Red";
	public static final String BLUE_NAME = ChatColor.AQUA + "Team Blue";
	public static final String MAGENTA_NAME = ChatColor.LIGHT_PURPLE + "Team Magenta";
	
	public static int getDyeColourTeam(DyeColor color) {
		DyeColor[] colors = new DyeColor[6];
		colors[0] = (DyeColor.LIME);
		colors[1] = (DyeColor.ORANGE);
		colors[2] = (DyeColor.YELLOW);
		colors[3] = (DyeColor.RED);
		colors[4] = (DyeColor.LIGHT_BLUE);
		colors[5] = (DyeColor.MAGENTA);
		int count = 0;
		for (DyeColor c : colors) {
			if (c == color) {
				return count;
			} else {
				count++;
			}
		}
		return 1;
	}
	
	public static DyeColor getTeamDyeColour(int teamId) {
		DyeColor colour = null;
		switch (teamId) {
		case 0: colour = DyeColor.LIME; break;
		case 1: colour = DyeColor.ORANGE; break;
		case 2: colour = DyeColor.YELLOW; break;
		case 3: colour = DyeColor.RED; break;
		case 4: colour = DyeColor.LIGHT_BLUE; break;
		case 5: colour = DyeColor.MAGENTA; break;
		}
		return colour;
	}
	
	public static ChatColor getTeamChatColour(int teamId) {
		ChatColor colour = null;
		switch (teamId) {
		case 0: colour = ChatColor.GREEN; break;
		case 1: colour = ChatColor.GOLD; break;
		case 2: colour = ChatColor.YELLOW; break;
		case 3: colour = ChatColor.RED; break;
		case 4: colour = ChatColor.AQUA; break;
		case 5: colour = ChatColor.LIGHT_PURPLE; break;
		}
		return colour;
	}
	
	public static int getTeamIdFromNAME(String name) {
		try {
			int i = Integer.parseInt(name) - 1;
			return i;
		} catch (Exception e) {
			if (name == GREEN_NAME) {
				return 0;
			} else if (name == ORANGE_NAME) {
				return 1;
			} else if (name == YELLOW_NAME) {
				return 2;
			} else if (name == RED_NAME) {
				return 3;
			} else if (name == BLUE_NAME) {
				return 4;
			} else {
				return 5;
			}
		}
	}
	
	public static int getTeamId(String name) {
		try {
			int i = Integer.parseInt(name) - 1;
			return i;
		} catch (Exception e) {
			switch (name.toLowerCase()) {
			case "green": return 0;
			case "orange": return 1;
			case "yellow": return 2;
			case "red": return 3;
			case "blue": return 4;
			case "magenta": return 5;
			default:
				return -1;
			}
		}
	}
	
	public static String getTeamNameById(int teamId) {
		switch (teamId) {
		case 0: return GREEN_NAME;
		case 1: return ORANGE_NAME;
		case 2: return YELLOW_NAME;
		case 3: return RED_NAME;
		case 4: return BLUE_NAME;
		case 5: return MAGENTA_NAME;
		default:
			return null;
		}
	}
	
}