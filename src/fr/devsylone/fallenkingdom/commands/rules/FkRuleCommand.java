package fr.devsylone.fallenkingdom.commands.rules;

import fr.devsylone.fallenkingdom.utils.Messages;
import org.bukkit.ChatColor;

import fr.devsylone.fallenkingdom.commands.FkCommand;
import fr.devsylone.fallenkingdom.utils.ChatUtils;
import fr.devsylone.fallenkingdom.utils.FkSound;

public abstract class FkRuleCommand extends FkCommand
{
	public FkRuleCommand(String path, String args, int nbrArgs, Messages description)
	{
		this(path, args, nbrArgs, description.getMessage());
	}

	public FkRuleCommand(String path, String args, int nbrArgs, String description)
	{
		super("rules " + path, args, nbrArgs, description);
		if(path != "help" && path != "list")
			permission = ADMIN_PERMISSION;
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
