package fr.devsylone.fallenkingdom.listeners.entity.player;

import fr.devsylone.fallenkingdom.utils.Messages;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;

import fr.devsylone.fallenkingdom.Fk;

public class ChangeGamemodeListener implements Listener
{
	@EventHandler
	public void change(PlayerGameModeChangeEvent e)
	{
		if(!Fk.getInstance().getWorldManager().isAffected(e.getPlayer().getWorld()))
			return;
		if(e.getNewGameMode() == GameMode.CREATIVE)
		{
			Fk.getInstance().getPacketManager().sendTitle(e.getPlayer(), Messages.PLAYER_CREATIVE_TITLE.getMessage(), Messages.PLAYER_CREATIVE_SUBTITLE.getMessage(), 20, 100,20);
			Fk.getInstance().getPlayerManager().getPlayer(e.getPlayer()).sendMessage(Messages.PLAYER_CREATIVE_CHAT);
		}
	}
}
