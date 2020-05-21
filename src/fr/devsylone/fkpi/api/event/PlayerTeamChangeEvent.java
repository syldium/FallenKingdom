package fr.devsylone.fkpi.api.event;

import fr.devsylone.fkpi.api.ITeam;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;

/**
 * Déclenché lorsqu'un joueur change d'équipe.
 */
public class PlayerTeamChangeEvent extends TeamEvent
{
    private static final HandlerList HANDLERS = new HandlerList();
    private final String playerName;
    private final ITeam from;

    public PlayerTeamChangeEvent(String playerName, ITeam from, ITeam to, boolean hasFiredAsync)
    {
        super(to, hasFiredAsync);
        this.playerName = playerName;
        this.from = from;
    }

    public PlayerTeamChangeEvent(String playerName, ITeam from, ITeam to)
    {
        this(playerName, from, to, !Bukkit.isPrimaryThread());
    }

    public String getPlayerName()
    {
        return this.playerName;
    }

    public ITeam getFrom()
    {
        return this.from;
    }

    public HandlerList getHandlers()
    {
        return HANDLERS;
    }

    public static HandlerList getHandlerList()
    {
        return HANDLERS;
    }
}
