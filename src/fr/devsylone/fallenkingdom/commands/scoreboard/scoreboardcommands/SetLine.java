package fr.devsylone.fallenkingdom.commands.scoreboard.scoreboardcommands;

import fr.devsylone.fallenkingdom.commands.ArgumentParser;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandPermission;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandResult;
import fr.devsylone.fallenkingdom.commands.abstraction.FkCommand;
import fr.devsylone.fallenkingdom.utils.Messages;
import org.bukkit.command.CommandSender;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.exception.FkLightException;

import java.util.List;
import java.util.stream.Collectors;

public class SetLine extends FkCommand
{
	public SetLine()
	{
		super("setLine", "<i1;15:number> <text>", Messages.CMD_MAP_SCOREBOARD_SET_LINE, CommandPermission.ADMIN);
	}

	@Override
	public CommandResult execute(Fk plugin, CommandSender sender, List<String> args, String label)
	{
		int line = ArgumentParser.parseScoreboardLine(args.get(0), Messages.CMD_ERROR_SCOREBOARD_INVALID_LINE);
		String content = args.stream().skip(1).collect(Collectors.joining(" "));
		if (plugin.isNewVersion() && content.length() > 64 || !plugin.isNewVersion() && content.length() > 32) {
			throw new FkLightException("La ligne contient trop de caractères.");
		}
		plugin.getScoreboardManager().setLine(line, content.replace("&", "§"));
		return CommandResult.SUCCESS;
	}
}
