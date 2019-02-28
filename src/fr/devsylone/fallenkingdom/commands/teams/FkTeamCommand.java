package fr.devsylone.fallenkingdom.commands.teams;

import org.bukkit.ChatColor;

import fr.devsylone.fallenkingdom.commands.FkCommand;
import fr.devsylone.fallenkingdom.utils.ChatUtils;

public abstract class FkTeamCommand extends FkCommand
{
	public FkTeamCommand(String name, String args, int nbrArgs, String description)
	{
		super("team " + name, args, nbrArgs, description);
	}

	protected void broadcast(String message)
	{
		super.broadcast(ChatColor.GOLD + message, ChatUtils.TEAM, null);
	}
}
