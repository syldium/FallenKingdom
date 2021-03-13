package fr.devsylone.fallenkingdom.commands.scoreboard.scoreboardcommands;

import fr.devsylone.fallenkingdom.commands.ArgumentParser;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandPermission;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandResult;
import fr.devsylone.fallenkingdom.commands.abstraction.FkCommand;
import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fallenkingdom.version.Version;
import org.bukkit.command.CommandSender;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.exception.FkLightException;

import java.util.List;
import java.util.stream.Collectors;

public class SetLine extends FkCommand
{
	public SetLine()
	{
		super("setLine", "<i0;14:number> <text>", Messages.CMD_MAP_SCOREBOARD_SET_LINE, CommandPermission.ADMIN);
	}

	@Override
	public CommandResult execute(Fk plugin, CommandSender sender, List<String> args, String label)
	{
		int line = ArgumentParser.parseScoreboardLine(args.get(0), Messages.CMD_ERROR_SCOREBOARD_INVALID_LINE);
		String content = args.stream().skip(1).collect(Collectors.joining(" "));
		int maxLength = Version.VersionType.V1_13.isHigherOrEqual() ? 64 : 32;
		if (content.length() > maxLength) {
			throw new FkLightException(Messages.CMD_ERROR_SCOREBOARD_TOO_MANY_CHARS);
		}
		plugin.getScoreboardManager().setLine(line, content.replace("&", "§"));
		return CommandResult.SUCCESS;
	}
}
