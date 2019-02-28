package fr.devsylone.fallenkingdom.commands.rules;

import org.bukkit.ChatColor;

import fr.devsylone.fallenkingdom.commands.FkCommand;
import fr.devsylone.fallenkingdom.utils.ChatUtils;
import fr.devsylone.fallenkingdom.utils.FkSound;

public abstract class FkRuleCommand extends FkCommand
{
	public FkRuleCommand(String path, String args, int nbrArgs, String description)
	{
		super("rules " + path, args, nbrArgs, description);
	}

	protected void broadcast(String message, String value, String end, FkSound sound)
	{
		broadcast(message + " " + ChatColor.DARK_PURPLE + value + " " + ChatColor.GOLD + end, sound);
	}

	protected void broadcast(String message, String value, String end)
	{
		broadcast(message, value, end, null);
	}
	
	protected void broadcast(String msg)
	{
		broadcast(msg, null);
	}
	
	protected void broadcast(String msg, FkSound sound)
	{
		super.broadcast(ChatColor.GOLD + msg, ChatUtils.RULES, sound);
	}
}
