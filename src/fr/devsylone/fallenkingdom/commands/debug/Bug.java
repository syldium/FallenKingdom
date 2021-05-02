package fr.devsylone.fallenkingdom.commands.debug;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandRole;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandResult;
import fr.devsylone.fallenkingdom.commands.abstraction.FkCommand;
import fr.devsylone.fallenkingdom.utils.ChatUtils;
import fr.devsylone.fallenkingdom.utils.Messages;
import org.bukkit.command.CommandSender;

import java.util.List;

public class Bug extends FkCommand
{
	public Bug()
	{
		super("bug", Messages.CMD_MAP_BUG, CommandRole.PLAYER);
	}

	@Override
	public CommandResult execute(Fk plugin, CommandSender sender, List<String> args, String label)
	{
		ChatUtils.sendMessage(sender, Messages.CMD_MAP_BUG_BUG);
		return CommandResult.SUCCESS;
	}
}
