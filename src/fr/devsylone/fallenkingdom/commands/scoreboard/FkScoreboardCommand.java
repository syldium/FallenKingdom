package fr.devsylone.fallenkingdom.commands.scoreboard;

import org.bukkit.ChatColor;

import fr.devsylone.fallenkingdom.commands.FkCommand;
import fr.devsylone.fallenkingdom.utils.ChatUtils;

public abstract class FkScoreboardCommand extends FkCommand
{
	public FkScoreboardCommand(String name, String args, int nbrArgs, String description)
	{
		super("scoreboard " + name, args, nbrArgs, description);
	}

	protected void broadcast(String message)
	{
		super.broadcast(ChatColor.GOLD + message, ChatUtils.SCOREBOARD, null);
	}
}
