package fr.devsylone.fallenkingdom.listeners.entity.player;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.game.Game.GameState;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class PauseInteractionListener implements Listener
{
    @EventHandler
    public void onInteract(final PlayerInteractEvent event)
    {
        if (isCancelledDueToPause(event.getPlayer()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onDrop(final PlayerDropItemEvent event)
    {
        if (isCancelledDueToPause(event.getPlayer()))
            event.setCancelled(true);
    }

    public boolean isCancelledDueToPause(final Player player)
    {
        if (Fk.getInstance().getGame().getState().equals(GameState.PAUSE) && (boolean) Fk.getInstance().getFkPI().getRulesManager().getRuleByName("DeepPause").getValue())
            return player.getGameMode().equals(GameMode.SURVIVAL) || player.getGameMode().equals(GameMode.ADVENTURE);
        return false;
    }
}
