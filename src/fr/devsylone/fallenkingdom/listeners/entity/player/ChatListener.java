package fr.devsylone.fallenkingdom.listeners.entity.player;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fkpi.FkPI;
import fr.devsylone.fkpi.rules.Rule;
import fr.devsylone.fkpi.teams.Team;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.UUID;

public class ChatListener implements Listener
{
	private static final boolean MESSAGE_WITH_UUID;

	static {
		boolean method;
		try {
			CommandSender.class.getMethod("sendMessage", UUID.class, String.class);
			method = true;
		} catch (NoSuchMethodException e) {
			method = false;
		}
		MESSAGE_WITH_UUID = method;
	}

	@EventHandler
	public void event(AsyncPlayerChatEvent e)
	{
		if(!Fk.getInstance().getWorldManager().isAffected(e.getPlayer().getWorld()))
			return;

		String msg = e.getMessage();
		Team team = FkPI.getInstance().getTeamManager().getPlayerTeam(e.getPlayer());
		ChatColor teamColor = team == null ? ChatColor.WHITE : team.getChatColor();
		char globalChat = FkPI.getInstance().getRulesManager().getRule(Rule.GLOBAL_CHAT_PREFIX);
		String globalChatStr = String.valueOf(globalChat);

		if(msg.startsWith(globalChatStr) || team == null || globalChat == ' ')
		{
			if(msg.startsWith(globalChatStr))
				e.setMessage(msg.substring(1));
			e.setFormat(Messages.CHAT_GLOBAL.getMessage() + teamColor + "%s : " + ChatColor.WHITE + "%s");
		}
		else
		{
			e.setCancelled(true);
			for(String pl : team.getPlayers())
			{
				Player player = Bukkit.getPlayer(pl);
				if(player == null)
					continue;

				String message = Messages.CHAT_TEAM.getMessage() + teamColor + e.getPlayer().getName() + " : " + ChatColor.WHITE + msg;
				if(MESSAGE_WITH_UUID)
					player.sendMessage(e.getPlayer().getUniqueId(), message);
				else
					player.sendMessage(message);
			}
		}
	}
}
