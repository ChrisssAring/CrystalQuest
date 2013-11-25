package nl.SugCube.CrystalQuest;

import nl.SugCube.CrystalQuest.SBA.SMeth;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Broadcast {
	
	public static CrystalQuest plugin;
	public static final String TAG = SMeth.setColours("&7[&dCrystalQuest&7]: &e");
	public static final String HELP = SMeth.setColours("&7[&dCQ-?&7]: ");
	public static final String HOWDEY = "Howdey!";
	public static final String NO_PERMISSION = ChatColor.RED + "[!!] Sorry, you don't have permission to perform this command!";
	public static final String ONLY_IN_GAME = "Only in-game players can perform this command!";
	
	public Broadcast(CrystalQuest instance) {
		plugin = instance;
	}
	
	public static void showAbout(CommandSender sender) {
		if (sender instanceof Player) {
			sender.sendMessage(
				" " + TAG + "Plugin made by " + ChatColor.GREEN + plugin.getDescription().getAuthors().toString() + "\n" +
				TAG + "Current version: " + plugin.getDescription().getVersion() + " (Up-to-date)\n" +
				TAG + "Use " + ChatColor.LIGHT_PURPLE + "/cq help " + ChatColor.YELLOW + "to get a list of commands.");
		} else {
			UpdateChecker uc = new UpdateChecker(plugin, "http://dev.bukkit.org/bukkit-plugins/crystalquest/files.rss");
			String update = "";
			
			if (uc.updateAvaiable()) {
				update = "(New version avaiable!)";
			} else {
				update = "(Up-to-date)";
			}
			
			plugin.getLogger().info("Plugin made by " + plugin.getDescription().getAuthors().toString());
			plugin.getLogger().info("Current version: " + plugin.getDescription().getVersion() + " " + update);
			plugin.getLogger().info("Use '/cq help' to get a list of commands.");
		}
	}

}