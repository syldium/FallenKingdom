package fr.devsylone.fallenkingdom.listeners.entity.player;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.players.FkPlayer;
import org.bukkit.World.Environment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class GoToNetherListener implements Listener
{
	@EventHandler
	public void event(PlayerChangedWorldEvent e)
	{
		if(!e.getPlayer().getWorld().getEnvironment().equals(Environment.THE_END))
			Fk.getInstance().getPlayerManager().getPlayer(e.getPlayer()).setPortal(e.getPlayer().getLocation());

		if(Fk.getInstance().getWorldManager().isAffected(e.getPlayer().getWorld()))
		{
			FkPlayer player = Fk.getInstance().getPlayerManager().getPlayer(e.getPlayer());
			player.refreshScoreboard();
		}
		else
		{
			FkPlayer player = Fk.getInstance().getPlayerManager().getPlayerIfExist(e.getPlayer());
			if(player != null)
				player.removeScoreboard();
			Fk.getInstance().getScoreboardManager().refreshNicks();
		}
	}
}