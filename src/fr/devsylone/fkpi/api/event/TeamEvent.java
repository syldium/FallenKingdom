package fr.devsylone.fkpi.api.event;

import fr.devsylone.fkpi.api.ITeam;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;

abstract class TeamEvent extends Event
{
    protected final boolean hasFiredAsync;
    protected final ITeam team;

    public TeamEvent(ITeam team)
    {
        this(team, !Bukkit.isPrimaryThread());
    }

    public TeamEvent(ITeam team, boolean isAsync)
    {
        super(isAsync);
        this.team = team;
        this.hasFiredAsync = isAsync;
    }

    public ITeam getTeam()
    {
        return team;
    }

    public boolean hasFiredAsync()
    {
        return hasFiredAsync;
    }
}
