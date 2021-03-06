package fr.devsylone.fallenkingdom.listeners.entity.player;

import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.World.Environment;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.game.Game.GameState;
import fr.devsylone.fallenkingdom.utils.ChatUtils;

public class UsePortalListener implements Listener
{
	@EventHandler
	public void use(PlayerPortalEvent e)
	{
		if(e.getTo() == null || e.getTo().getWorld() == null || e.getPlayer().getGameMode().equals(GameMode.CREATIVE))
			return;
		
		if(e.getTo().getWorld().getEnvironment().equals(Environment.NETHER))
		{
			if(!Fk.getInstance().getGame().isNetherEnabled() && Fk.getInstance().getGame().getState() != GameState.BEFORE_STARTING)
			{
				e.getPlayer().sendMessage(ChatUtils.PREFIX + "§cLe nether n'est pas encore actif !");
				e.setCancelled(true);
			}
		}
		else if(e.getTo().getWorld().getEnvironment().equals(Environment.THE_END))
		{
			if(!Fk.getInstance().getGame().isEndEnabled() && Fk.getInstance().getGame().getState() != GameState.BEFORE_STARTING)
			{
				e.getPlayer().sendMessage(ChatUtils.PREFIX + "§cL'end n'est pas encore actif !");
				e.setCancelled(true);
			}
		}
	}
}
