package fr.devsylone.fallenkingdom.listeners.entity.player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.players.FkPlayer.PlayerState;

public class ScrollListener implements Listener
{
	private long last = 0;
	private boolean alternate = false;

	@EventHandler
	public void scroll(PlayerItemHeldEvent e)
	{
		if(Fk.getInstance().getPlayerManager().getPlayer(e.getPlayer()).getState() == PlayerState.EDITING_SCOREBOARD && System.currentTimeMillis() - last > 200)
		{
			Fk.getInstance().getPlayerManager().getPlayer(e.getPlayer()).getScoreboard().setFormatted(alternate);
			alternate = !alternate;
			last = System.currentTimeMillis();
		}

	}
}
