package fr.devsylone.fallenkingdom.listeners.entity.player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.game.Game.GameState;
import fr.devsylone.fallenkingdom.utils.ChatUtils;

public class UsePortalListener implements Listener
{
	@EventHandler
	public void use(PlayerPortalEvent e)
	{
		if(e.getTo() == null || e.getTo().getWorld() == null)
			return;
		
		if(e.getTo().getWorld().getName().endsWith("_nether"))
			if(!Fk.getInstance().getGame().isNetherEnabled() && Fk.getInstance().getGame().getState() != GameState.BEFORE_STARTING)
			{
				e.getPlayer().sendMessage(ChatUtils.PREFIX + "§cLe nether n'est pas encore actif !");
				e.setCancelled(true);
			}
		
		else if(e.getTo().getWorld().getName().endsWith("_end"))
			if(!Fk.getInstance().getGame().isEndEnabled() && Fk.getInstance().getGame().getState() != GameState.BEFORE_STARTING)
			{
				e.getPlayer().sendMessage(ChatUtils.PREFIX + "§cL'end n'est pas encore actif !");
				e.setCancelled(true);
			}

	}
}
