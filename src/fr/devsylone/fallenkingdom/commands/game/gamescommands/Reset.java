package fr.devsylone.fallenkingdom.commands.game.gamescommands;

import fr.devsylone.fallenkingdom.commands.abstraction.CommandPermission;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandResult;
import fr.devsylone.fallenkingdom.commands.abstraction.Confirmable;
import fr.devsylone.fallenkingdom.commands.abstraction.FkCommand;
import fr.devsylone.fallenkingdom.utils.Messages;
import org.bukkit.command.CommandSender;

import fr.devsylone.fallenkingdom.Fk;

import java.util.List;

public class Reset extends FkCommand implements Confirmable
{
	public Reset()
	{
		super("reset", Messages.CMD_MAP_GAME_RESET, CommandPermission.ADMIN);
	}

	@Override
	public CommandResult execute(Fk plugin, CommandSender sender, List<String> args, String label)
	{
		if (isConfirmed(sender)) {
			broadcast(Messages.CMD_GAME_RESET.getMessage());
			plugin.reset();
			return CommandResult.SUCCESS;
		}

		sender.sendMessage(createWarning(Messages.WARNING_GAME_RESET, true));
		addConfirmed(sender);
		return CommandResult.SUCCESS;
	}
}
