package fr.devsylone.fallenkingdom.listeners.entity.player;

import fr.devsylone.fallenkingdom.players.FkPlayer;
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
		if (!Fk.getInstance().getWorldManager().isAffected(e.getPlayer().getWorld())) {
			return;
		}
		final FkPlayer fkPlayer = Fk.getInstance().getPlayerManager().getPlayerIfExist(e.getPlayer());
		if (fkPlayer == null) {
			return;
		}

		if (fkPlayer.getState() == PlayerState.EDITING_SCOREBOARD && System.currentTimeMillis() - last > 200) {
			fkPlayer.setUseFormattedText(alternate);
			alternate = !alternate;
			last = System.currentTimeMillis();
		}

	}
}
