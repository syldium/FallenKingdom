package fr.devsylone.fallenkingdom.listeners.entity.player;

import fr.devsylone.fallenkingdom.Fk;
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

    public static boolean isCancelledDueToPause(final Player player)
    {
        if (!Fk.getInstance().getWorldManager().isAffected(player.getWorld()))
            return false;
        if (Fk.getInstance().getGame().isPaused() && FkPI.getInstance().getRulesManager().getRule(Rule.DEEP_PAUSE))
            return player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.ADVENTURE;
        return false;
    }
}
