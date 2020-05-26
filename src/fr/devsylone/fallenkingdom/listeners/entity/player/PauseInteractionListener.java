package fr.devsylone.fallenkingdom.listeners.entity.player;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.game.Game.GameState;
import fr.devsylone.fallenkingdom.utils.ChatUtils;
import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fkpi.FkPI;
import fr.devsylone.fkpi.rules.Rule;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class PauseInteractionListener implements Listener
{
    @EventHandler(ignoreCancelled = true)
    public void onInteract(final PlayerInteractEvent event)
    {
        if (isCancelledDueToPause(event.getPlayer()))
        {
            ChatUtils.sendMessage(event.getPlayer(), Messages.PLAYER_PAUSE);
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onDrop(final PlayerDropItemEvent event)
    {
        if (isCancelledDueToPause(event.getPlayer()))
        {
            ChatUtils.sendMessage(event.getPlayer(), Messages.PLAYER_PAUSE);
            event.setCancelled(true);
        }
    }

    public boolean isCancelledDueToPause(final Player player)
    {
        if (!Fk.getInstance().getWorldManager().isAffected(player.getWorld()))
            return false;
        if (Fk.getInstance().getGame().getState().equals(GameState.PAUSE) && FkPI.getInstance().getRulesManager().getRule(Rule.DEEP_PAUSE))
            return player.getGameMode().equals(GameMode.SURVIVAL) || player.getGameMode().equals(GameMode.ADVENTURE);
        return false;
    }
}
