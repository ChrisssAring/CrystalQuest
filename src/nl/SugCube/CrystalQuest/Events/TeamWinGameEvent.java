package nl.SugCube.CrystalQuest.Events;

import java.util.List;

import nl.SugCube.CrystalQuest.Game.Arena;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TeamWinGameEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private List<Player> player;
	private Arena arena;
	private int team;
	
	/**
	 * @param void
	 * @return (int) ID of the team the winning players is in
	 */
	public int getTeam() {
		return this.team;
	}
	
	/**
	 * @param void
	 * @return (Arena) Get the arena the players won in
	 */
	public Arena getArena() {
		return this.arena;
	}
	
	/**
	 * @param void
	 * @return (PlayerList) The players who won the game
	 */
	public List<Player> getPlayers() {
		return this.player;
	}
	
	public TeamWinGameEvent(List<Player> p, Arena a, int teamId) {
		this.player = p;
		this.arena = a;
		this.team = teamId;
	}
	
	public HandlerList getHandlers() {
	    return handlers;
	}
	 
	public static HandlerList getHandlerList() {
	    return handlers;
	}
	
}