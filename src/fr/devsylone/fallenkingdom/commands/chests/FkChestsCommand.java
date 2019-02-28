package fr.devsylone.fallenkingdom.commands.chests;

import org.bukkit.ChatColor;

import fr.devsylone.fallenkingdom.commands.FkCommand;
import fr.devsylone.fallenkingdom.utils.ChatUtils;
import fr.devsylone.fallenkingdom.utils.FkSound;

public abstract class FkChestsCommand extends FkCommand
{
	public FkChestsCommand(String name, String description)
	{
		super("chests " + name, "", 0, description);
	}

	public FkChestsCommand(String path, String args, int nbrArgs, String description)
	{
		super("chests " + path, args, nbrArgs, description);
	}

	protected void broadcast(String message, String value, String end)
	{
		broadcast(message, value, end, null);
	}

	protected void broadcast(String message, String value, String end, FkSound sound)
	{
		broadcast(message + " " + ChatColor.DARK_PURPLE + value + ChatColor.GOLD + end, sound);
	}
	
	protected void broadcast(String msg)
	{
		broadcast(msg, null);
	}

	protected void broadcast(String msg, FkSound sound)
	{
		super.broadcast(ChatColor.GOLD + msg, ChatUtils.CHESTS, sound);
	}
}
