package fr.devsylone.fkpi.api.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

abstract class FkEvent extends Event
{
    private static final HandlerList HANDLERS = new HandlerList();

    public HandlerList getHandlers()
    {
        return HANDLERS;
    }

    public static HandlerList getHandlerList()
    {
        return HANDLERS;
    }
}
