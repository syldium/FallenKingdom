package fr.devsylone.fkpi.api.event;

import fr.devsylone.fkpi.lockedchests.LockedChest;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * Déclenché lorsqu'un joueur commence ou termine un crochetaqe de coffre.
 *
 * Noter que cet évènement n'est pas déclenché si le joueur est en créatif.
 * Si annulé, le changement de statut du coffre n'aura pas lieu.
 */
public class PlayerLockedChestInteractEvent extends PlayerEvent implements Cancellable
{
    private static final HandlerList HANDLERS = new HandlerList();
    private final LockedChest chest;
    private boolean cancelled = false;

    public PlayerLockedChestInteractEvent(Player player, LockedChest chest)
    {
        super(player);
        this.chest = chest;
    }

    public LockedChest getChest()
    {
        return chest;
    }

    public boolean isUnlocked()
    {
        return chest.getState().equals(LockedChest.ChestState.UNLOCKED);
    }

    @Override
    public boolean isCancelled()
    {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancel)
    {
        this.cancelled = cancel;
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
