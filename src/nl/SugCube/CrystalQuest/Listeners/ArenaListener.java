package nl.SugCube.CrystalQuest.Listeners;

import nl.SugCube.CrystalQuest.Broadcast;
import nl.SugCube.CrystalQuest.CrystalQuest;
import nl.SugCube.CrystalQuest.Events.ArenaStartEvent;
import nl.SugCube.CrystalQuest.Game.Arena;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scoreboard.Team;

public class ArenaListener implements Listener {

	public static CrystalQuest plugin;
	
	public ArenaListener(CrystalQuest instance) {
		plugin = instance;
	}
	
	@EventHandler
	public void onArenaStart(ArenaStartEvent e) {
		Arena a = e.getArena();
		for (Team t : a.getTeams()) {
			if (t.getPlayers().size() == a.getPlayers().size()) {
				for (Player p : a.getPlayers()) {
					p.sendMessage(Broadcast.get("arena.not-start-teams"));
				}
				//TODO: REMOVE COMMENT: e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if (plugin.getConfig().getBoolean("arena.blood") && event.getEntity() instanceof LivingEntity) {
			if (plugin.prot.isInProtectedArena(event.getEntity().getLocation())) {
				if (event.getEntity() instanceof Player) {
					Player p = (Player) event.getEntity();
					if (!plugin.getArenaManager().isInGame(p) || plugin.getArenaManager().isSpectator(p)) {
						return;
					}
				}
				event.getEntity().getWorld().playEffect(event.getEntity().getLocation().add(0, 1.5, 0),
						Effect.STEP_SOUND, Material.REDSTONE_BLOCK);
			}
		}
	}
	
}
