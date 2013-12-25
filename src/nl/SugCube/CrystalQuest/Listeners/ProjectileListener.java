package nl.SugCube.CrystalQuest.Listeners;

import java.util.Random;

import nl.SugCube.CrystalQuest.CrystalQuest;
import nl.SugCube.CrystalQuest.ParticleHandler;
import nl.SugCube.CrystalQuest.Economy.Multipliers;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Fireball;
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
							
							Random ran = new Random();
							double chance = Multipliers.getMultiplier("blood",
									plugin.economy.getLevel(player, "blood", "crystals"), false);
							int multiplier = 1;
							
							if (ran.nextInt(100) <= chance * 100 && chance != 0) {
								multiplier = 2;
							}
							
							//Adds crystals to player's balance
							int money = (int) (1 * plugin.getConfig().getDouble("shop.crystal-multiplier"));
							plugin.economy.getBalance().addCrystals(player, money * multiplier, false);
							String message = plugin.economy.getCoinMessage(player, money * multiplier);
							if (message != null) {
								player.sendMessage(message);
							}
							
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
					double multiplier = Multipliers.getMultiplier("explosive",
							plugin.economy.getLevel(pl, "explosive", "upgrade"), false);
					
					Location loc = egg.getLocation();
					loc.getWorld().createExplosion(loc.getX(), loc.getY(), loc.getZ(), (float) (3.0 * multiplier), false, false);
				}
			}
		} else if (e.getEntity() instanceof Snowball) {
			ParticleHandler.balls.remove(e.getEntity());
		} else if (e.getEntity() instanceof Fireball) {
			if (e.getEntity().getShooter() instanceof Player) {
				Player pl = (Player) e.getEntity().getShooter();
				if (plugin.getArenaManager().isInGame(pl)) {
					double multiplier = Multipliers.getMultiplier("lightning",
							plugin.economy.getLevel(pl, "explosive", "upgrade"), false);
					World w = e.getEntity().getLocation().getWorld();
					w.createExplosion(e.getEntity().getLocation().getX(), e.getEntity().getLocation().getY(),
							e.getEntity().getLocation().getZ(), (float) (1 + (multiplier - 0.5)), false, false);
				}
			}
		}
	}
}