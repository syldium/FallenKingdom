package fr.devsylone.fallenkingdom.commands.scoreboard;

import fr.devsylone.fallenkingdom.utils.Messages;
import org.bukkit.ChatColor;

import fr.devsylone.fallenkingdom.commands.FkCommand;
import fr.devsylone.fallenkingdom.utils.ChatUtils;

public abstract class FkScoreboardCommand extends FkCommand
{
	public FkScoreboardCommand(String name, String args, int nbrArgs, Messages description)
	{
		this(name, args, nbrArgs, description.getMessage());
	}

	public FkScoreboardCommand(String name, String args, int nbrArgs, String description)
	{
		super("scoreboard " + name, args, nbrArgs, description);
		if(path != "help")
			permission = ADMIN_PERMISSION;
	}

	protected void broadcast(String message)
	{
		super.broadcast(ChatColor.GOLD + message, ChatUtils.SCOREBOARD, null);
	}
}
