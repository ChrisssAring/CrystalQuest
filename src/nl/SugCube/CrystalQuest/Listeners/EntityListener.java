package nl.SugCube.CrystalQuest.Listeners;

import nl.SugCube.CrystalQuest.CrystalQuest;
import nl.SugCube.CrystalQuest.Teams;
import nl.SugCube.CrystalQuest.Game.Arena;

import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.meta.FireworkMeta;

public class EntityListener implements Listener {

	public static CrystalQuest plugin;
	
	public EntityListener(CrystalQuest instance) {
		plugin = instance;
	}
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent e) {
		Entity ent = e.getEntity();
		if (ent instanceof Creeper) {
			Creeper c = (Creeper) ent;
			boolean isGameCreeper = false;
			for (Arena a : plugin.getArenaManager().getArenas()) {
				for (Creeper creep : a.getGameCreepers()) {
					if (creep == c) {
						isGameCreeper = true;
					}
				}
			}
			
			if (isGameCreeper) {
				c.getWorld().dropItem(c.getLocation(), plugin.itemHandler.getItemByName("Crystal Shard"));
			}
		}
		
		if (plugin.prot.isInProtectedArena(ent.getLocation())) {
			e.getDrops().clear();
		}
	}
	
	@EventHandler
	public void onEntityExplode(EntityExplodeEvent e) {
		if (e.getEntity() instanceof EnderCrystal) {			
			if (plugin.prot.isInProtectedArena(e.getEntity().getLocation())) {
				e.setCancelled(true);
				Location loc = e.getLocation();
				loc.getWorld().createExplosion(loc.getX(), loc.getY() + 2, loc.getZ(), 5.0F, false, false);
				Entity toRemove = null;
				for (Arena a : plugin.getArenaManager().getArenas()) {
					for (Entity ent : a.getGameCrystals()) {
						if (ent == e.getEntity()) {
							toRemove = ent;
						}
					}
					if (toRemove != null) {
						a.getGameCrystals().remove(toRemove);
					}
				}
			}
			
			for (Arena a : plugin.getArenaManager().getArenas()) {
				Entity toRemove = null;
				for (Entity ent : a.getGameCrystals()) {
					if (ent == e.getEntity()) {
						e.setCancelled(true);
						Location loc = e.getLocation();
						loc.getWorld().createExplosion(loc.getX(), loc.getY(), loc.getZ(), 5.0F, false, false);
						toRemove = ent;
					}
				}
				if (toRemove != null) {
					a.getGameCrystals().remove(toRemove);
				}
			}
		} else if (e.getEntity() instanceof Creeper) {
			if (plugin.prot.isInProtectedArena(e.getEntity().getLocation())) {
				e.setCancelled(true);
				Location loc = e.getLocation();
				loc.getWorld().createExplosion(loc.getX(), loc.getY(), loc.getZ(), 4.0F, false, false);
				Creeper toRemove = null;
				for (Arena a : plugin.getArenaManager().getArenas()) {
					for (Creeper c : a.getGameCreepers()) {
						if (c == e.getEntity()) {
							toRemove = c;
						}
					}
					if (toRemove != null) {
						a.getGameCreepers().remove(toRemove);
					}
				}
			}
			
			for (Arena a : plugin.getArenaManager().getArenas()) {
				Creeper toRemove = null;
				for (Creeper c : a.getGameCreepers()) {
					if (c == e.getEntity() || plugin.prot.isInProtectedArena(c.getLocation())) {
						e.setCancelled(true);
						Location loc = e.getLocation();
						loc.getWorld().createExplosion(loc.getX(), loc.getY(), loc.getZ(), 4.0F, false, false);
						toRemove = c;
					}
				}
				if (toRemove != null) {
					a.getGameCreepers().remove(toRemove);
				}
			}
		}
	}
	
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
		/*
		 * Block friendly Dog-fire
		 */
		if (e.getEntity() instanceof Wolf) {
			Wolf w = (Wolf) e.getEntity();
			DyeColor collar = w.getCollarColor();
			int wolfTeam = Teams.getDyeColourTeam(collar);
			if (e.getDamager() instanceof Player) {
				int damagerTeam = plugin.getArenaManager().getTeam((Player) e.getDamager());
				if (wolfTeam == damagerTeam) {
					e.setCancelled(true);
				}
			} else if (e.getDamager() instanceof Arrow) {
				Arrow arrow = (Arrow) e.getDamager();
				if (arrow.getShooter() instanceof Player) {
					int damagerTeam = plugin.getArenaManager().getTeam((Player) arrow.getShooter());
					if (wolfTeam == damagerTeam) {
						e.setCancelled(true);
					}
				}
			}
		}
		
		if (e.getEntity() instanceof EnderCrystal) {
			boolean isForGame = false;
			boolean isValid = true;
			
			if (e.getDamager() instanceof Projectile) {
				if (((Projectile) e.getDamager()).getShooter() instanceof Player) {
					Projectile proj = (Projectile) e.getDamager();
					Player player = (Player) proj.getShooter();
					if (plugin.am.isInGame(player)) {
						isForGame = true;
						isValid = false;
					}
				}
			} else if (e.getDamager() instanceof Player) {
				if (plugin.am.isInGame(((Player) e.getDamager()))) {
					isForGame = true;
					isValid = true;
				}
			}
			
			if (isForGame) {
				e.setCancelled(true);
				if (e.getDamager() instanceof Player) {
					Player pl = (Player) e.getDamager();
					plugin.getArenaManager().getArena(pl).getGameCrystals().remove(e.getEntity());
					plugin.getArenaManager().getArena(pl).getGameCrystalMap().remove(e.getEntity());
					
					if (isValid) {
						plugin.am.getArena(pl).addScore(plugin.am.getTeam(pl), 3);
						e.getEntity().remove();
						
						Firework f = e.getEntity().getLocation().getWorld().spawn(e.getEntity().getLocation(), Firework.class);
						FireworkMeta fm = f.getFireworkMeta();
						fm.setPower(0);
						FireworkEffect fe = FireworkEffect.builder()
												.flicker(true)
												.withColor(plugin.im.getTeamColour(plugin.am.getTeam(pl)))
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