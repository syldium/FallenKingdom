package fr.devsylone.fallenkingdom.commands.scoreboard.scoreboardcommands;

import fr.devsylone.fallenkingdom.commands.abstraction.CommandPermission;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandResult;
import fr.devsylone.fallenkingdom.commands.abstraction.FkCommand;
import fr.devsylone.fallenkingdom.utils.Messages;
import org.bukkit.command.CommandSender;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.exception.FkLightException;

import java.util.List;

public class SetName extends FkCommand
{
	public SetName()
	{
		super("setName", "<text>", Messages.CMD_MAP_SCOREBOARD_SET_NAME, CommandPermission.ADMIN);
	}

	@Override
	public CommandResult execute(Fk plugin, CommandSender sender, List<String> args, String label)
	{
		String line = String.join(" ", args);

		if(line.length() >= 32)
			throw new FkLightException(Messages.CMD_ERROR_SCOREBOARD_TITLE_TOO_LONG);

		Fk.getInstance().getScoreboardManager().setName(line.replace("&", "§"));
		return CommandResult.SUCCESS;
	}
}
