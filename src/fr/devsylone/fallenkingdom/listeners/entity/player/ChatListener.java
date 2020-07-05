package fr.devsylone.fallenkingdom.listeners.entity.player;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fkpi.FkPI;
import fr.devsylone.fkpi.teams.Team;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener
{
	@EventHandler
	public void event(AsyncPlayerChatEvent e)
	{
		if(!Fk.getInstance().getWorldManager().isAffected(e.getPlayer().getWorld()))
			return;

		String msg = e.getMessage();
		Team team = FkPI.getInstance().getTeamManager().getPlayerTeam(e.getPlayer().getName());
		ChatColor teamColor = team == null ? ChatColor.WHITE : team.getChatColor();

		if(msg.startsWith("!") || team == null)
		{
			if(msg.startsWith("!"))
				e.setMessage(msg.substring(1));
			e.setFormat(Messages.CHAT_GLOBAL.getMessage() + teamColor + "%s : " + ChatColor.WHITE + "%s");
		}
		else
		{
			e.setCancelled(true);
			for(String pl : team.getPlayers())
				if(Bukkit.getPlayer(pl) != null)
					Bukkit.getPlayer(pl).sendMessage(Messages.CHAT_TEAM.getMessage() + teamColor + e.getPlayer().getName() + " : " + ChatColor.WHITE + msg);
		}
	}
}
