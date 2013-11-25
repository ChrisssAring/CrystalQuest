package nl.SugCube.CrystalQuest.Listeners;

import nl.SugCube.CrystalQuest.CrystalQuest;
import nl.SugCube.CrystalQuest.ParticleHandler;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Egg;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

public class ProjectileListener implements Listener {

	public static CrystalQuest plugin;
	
	public ProjectileListener(CrystalQuest instance) {
		plugin = instance;
	}
	
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof Snowball) {
			Snowball ball = (Snowball) e.getDamager();
			if (ball.getShooter() instanceof Player) {
				Player player = (Player) ball.getShooter();
				if (plugin.getArenaManager().isInGame(player)) {
					if (e.getEntity() instanceof LivingEntity) {
						LivingEntity len = (LivingEntity) e.getEntity();
						if (len != null) {
							len.setHealth(0);
							len.getWorld().playSound(len.getLocation(), Sound.BLAZE_DEATH, 20F, 20F);
							if (len instanceof Player) {
								Player pl = (Player) len;
								FireworkEffect fe = FireworkEffect.builder().with(Type.CREEPER).withColor(plugin.im.getTeamColour(
										plugin.getArenaManager().getTeam(pl))).build();
								try {
									plugin.particleHandler.playFirework(len.getWorld(), len.getLocation().add(0, 4, 0), fe);
								} catch (Exception e1) { }
							} else {
								FireworkEffect fe = FireworkEffect.builder().with(Type.CREEPER).withColor(Color.WHITE).build();
								try {
									plugin.particleHandler.playFirework(len.getWorld(), len.getLocation().add(0, 4, 0), fe);
								} catch (Exception e1) { }
							}
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onProjectileHit(ProjectileHitEvent e) {
		if (e.getEntity() instanceof Egg) {
			Egg egg = (Egg) e.getEntity();
			if (egg.getShooter() instanceof Player) {
				Player pl = (Player) egg.getShooter();
				if (plugin.getArenaManager().isInGame(pl)) {
					Location loc = egg.getLocation();
					loc.getWorld().createExplosion(loc.getX(), loc.getY(), loc.getZ(), 3.0F, false, false);
				}
			}
		} else if (e.getEntity() instanceof Snowball) {
			ParticleHandler.balls.remove(e.getEntity());
		}
	}
}