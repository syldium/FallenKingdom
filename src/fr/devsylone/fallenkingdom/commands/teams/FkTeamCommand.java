package fr.devsylone.fallenkingdom.commands.teams;

import fr.devsylone.fallenkingdom.utils.Messages;
import org.bukkit.ChatColor;

import fr.devsylone.fallenkingdom.commands.FkCommand;
import fr.devsylone.fallenkingdom.utils.ChatUtils;

public abstract class FkTeamCommand extends FkCommand
{
	public FkTeamCommand(String path, String args, int nbrArgs, Messages description)
	{
		this(path, args, nbrArgs, description.getMessage());
	}

	public FkTeamCommand(String name, String args, int nbrArgs, String description)
	{
		super("team " + name, args, nbrArgs, description);
		if(!name.equalsIgnoreCase("help") && !name.equalsIgnoreCase("List") && !name.equalsIgnoreCase("ChestsRoom"))
			permission = ADMIN_PERMISSION;
	}

	protected void broadcast(String message)
	{
		super.broadcast(ChatColor.GOLD + message, ChatUtils.TEAM, null);
	}
}
