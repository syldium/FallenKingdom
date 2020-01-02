package fr.devsylone.fallenkingdom.listeners.entity.player;

import fr.devsylone.fkpi.util.BlockDescription;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fkpi.rules.AllowedBlocks;
import fr.devsylone.fkpi.rules.Rule;

public class ChatListener implements Listener
{
	@EventHandler
	public void event(AsyncPlayerChatEvent e)
	{
		Player p = e.getPlayer();
		ChatColor cc = ChatColor.WHITE;

		String msg = e.getMessage();

		if(msg.toLowerCase().matches("\\{[a-zA-Z]+\\}"))
			for(Rule rule : Fk.getInstance().getFkPI().getRulesManager().getRulesList())
			{
				if(msg.toLowerCase().matches("\\{" + rule.getName().toLowerCase() + "\\}"))
				{
					String content = "";

					if(rule instanceof AllowedBlocks)
					{
						for(BlockDescription s : ((AllowedBlocks) rule).getValue())
							content += ", " + s.toString();

						content = content.substring(2);
					}

					else
						content = rule.getValue().toString();

					msg = msg.replaceAll("\\{" + rule.getName().toLowerCase() + "\\}", "§2§l" + content + cc);
				}
			}
		
		e.setMessage(msg);
		if(msg.startsWith("!"))
		{
			e.setMessage(e.getMessage().substring(1));
			e.setFormat("(global)%s : " + cc + "%s");
		}

		else if(Fk.getInstance().getFkPI().getTeamManager().getPlayerTeam(p.getName()) == null)
			e.setFormat("(global)%s : " + cc + "%s");

		else
		{
			e.setCancelled(true);
			for(String pl : Fk.getInstance().getFkPI().getTeamManager().getPlayerTeam(p.getName()).getPlayers())
				if(Bukkit.getPlayer(pl) != null)
					Bukkit.getPlayer(pl).sendMessage(ChatColor.WHITE + "(team)" + p.getDisplayName() + " : " + cc + msg);
		}
	}
}
