package nl.SugCube.CrystalQuest.Events;

import nl.SugCube.CrystalQuest.Game.Arena;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerJoinArenaEvent extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();

	private boolean cancelled;
	private Player player;
	private Arena arena; 
	
	/**
	 * @param void
	 * @return Get the arena the player joined
	 */
	public Arena getArena() {
		return this.arena;
	}
	
	/**
	 * @param void
	 * @return The player who joined the arena
	 */
	public Player getPlayer() {
		return this.player;
	}
	
	public PlayerJoinArenaEvent(Player p, Arena a) {
		this.player = p;
		this.arena = a;
	}
	
	public HandlerList getHandlers() {
	    return handlers;
	}
	 
	public static HandlerList getHandlerList() {
	    return handlers;
	}
	
	public boolean isCancelled() {
        return cancelled;
    }
 
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }
	
}