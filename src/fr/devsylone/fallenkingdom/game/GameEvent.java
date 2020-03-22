package fr.devsylone.fallenkingdom.game;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GameEvent extends Event
{ //Utilisation de l'API bukkit/spigot

	private static final HandlerList HANDLERS = new HandlerList();

	public HandlerList getHandlers()
	{
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

	private final Type type;
	private final int day;

	public GameEvent(Type type, int day)
	{
		this.type = type;
		this.day = day;
	}

	public int getDay()
	{
		return day;
	}

	public Type getType()
	{
		return type;
	}

	public enum Type
	{
		NEW_DAY,
		PVP_ENABLED,
		TNT_ENABLED,
		NETHER_ENABLED,
		END_ENABLED,
		PAUSE_EVENT,
		RESUME_EVENT,
		START_EVENT
	}
}
