package fr.devsylone.fallenkingdom.listeners.entity.player;

import fr.devsylone.fallenkingdom.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import fr.devsylone.fallenkingdom.Fk;

public class ChatListener implements Listener
{
	@EventHandler
	public void event(AsyncPlayerChatEvent e)
	{
		Player p = e.getPlayer();
		ChatColor cc = ChatColor.WHITE;

		String msg = e.getMessage();
		
		e.setMessage(msg);
		if(msg.startsWith("!") || Fk.getInstance().getFkPI().getTeamManager().getPlayerTeam(p.getName()) == null)
		{
			e.setMessage(e.getMessage().substring(1));
			e.setFormat(Messages.CHAT_GLOBAL +  "%s : " + cc + "%s");
		}

		else
		{
			e.setCancelled(true);
			for(String pl : Fk.getInstance().getFkPI().getTeamManager().getPlayerTeam(p.getName()).getPlayers())
				if(Bukkit.getPlayer(pl) != null)
					Bukkit.getPlayer(pl).sendMessage(Messages.CHAT_TEAM + p.getDisplayName() + " : " + cc + msg);
		}
	}
}
