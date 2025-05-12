package fr.devsylone.fallenkingdom.listeners.entity.player;

import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fkpi.FkPI;
import fr.devsylone.fkpi.rules.Rule;
import fr.devsylone.fkpi.teams.Base;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.World.Environment;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.utils.ChatUtils;

public class UsePortalListener implements Listener
{
	@EventHandler
	public void use(PlayerPortalEvent e)
	{
		if(e.getTo() == null || e.getTo().getWorld() == null || e.getPlayer().getGameMode() == GameMode.CREATIVE)
			return;

		if(!Fk.getInstance().getWorldManager().isAffected(e.getTo().getWorld()))
			return;

		if(e.getTo().getWorld().getEnvironment() == Environment.NETHER)
		{
			if(!Fk.getInstance().getGame().isNetherEnabled() && Fk.getInstance().getGame().hasStarted())
			{
				ChatUtils.sendMessage(e.getPlayer(), Messages.PLAYER_NETHER_NOT_ACTIVE);
				e.setCancelled(true);
			}
		}
		else if(e.getTo().getWorld().getEnvironment() == Environment.THE_END)
		{
			if(!Fk.getInstance().getGame().isEndEnabled() && Fk.getInstance().getGame().hasStarted())
			{
				ChatUtils.sendMessage(e.getPlayer(), Messages.PLAYER_END_NOT_ACTIVE);
				e.setCancelled(true);
			}
		}
	}
}
