package fr.devsylone.fkpi.api.event;

import fr.devsylone.fkpi.api.ITeam;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;

public class TeamUpdateEvent extends TeamEvent
{
    private static final HandlerList HANDLERS = new HandlerList();
    private final TeamUpdate change;

    public TeamUpdateEvent(ITeam team, TeamUpdate change, boolean hasFiredAsync)
    {
        super(team, hasFiredAsync);
        this.change = change;
    }

    public TeamUpdateEvent(ITeam team, TeamUpdate change)
    {
        this(team, change, !Bukkit.isPrimaryThread());
    }

    public TeamUpdate getUpdateType()
    {
        return change;
    }

    public HandlerList getHandlers()
    {
        return HANDLERS;
    }

    public static HandlerList getHandlerList()
    {
        return HANDLERS;
    }

    public enum TeamUpdate
    {
        CREATION,
        DELETION,
        UPDATE,
        SET_BASE
    }
}
