package nl.SugCube.CrystalQuest.Events;

import java.util.List;

import nl.SugCube.CrystalQuest.Game.Arena;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.scoreboard.Team;

public class TeamWinGameEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private List<Player> player;
	private Arena arena;
	private int team;
	private int teamCount;
	private Team[] teams;
	private String teamName;
	
	/**
	 * @param void
	 * @return (String) The name of the team who won.
	 */
	public String getTeamName() {
		return this.teamName;
	}
	
	/**
	 * @param void
	 * @return (Team[]) The teams that were in the game.
	 */
	public Team[] getTeams() {
		return this.teams;
	}
	
	/**
	 * @param void
	 * @return (int) The amount of teams that played the game.
	 */
	public int getTeamCount() {
		return this.teamCount;
	}
	
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
	
	public TeamWinGameEvent(List<Player> p, Arena a, int teamId, int tc, Team[] tms, String tn) {
		this.player = p;
		this.arena = a;
		this.team = teamId;
		this.teamCount = tc;
		this.teams = tms;
		this.teamName = tn;
	}
	
	public HandlerList getHandlers() {
	    return handlers;
	}
	 
	public static HandlerList getHandlerList() {
	    return handlers;
	}
	
}