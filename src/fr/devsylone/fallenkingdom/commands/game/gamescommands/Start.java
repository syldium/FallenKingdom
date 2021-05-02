package fr.devsylone.fallenkingdom.commands.game.gamescommands;

import fr.devsylone.fallenkingdom.commands.abstraction.CommandRole;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandResult;
import fr.devsylone.fallenkingdom.commands.abstraction.FkCommand;
import fr.devsylone.fallenkingdom.utils.Messages;
import org.bukkit.command.CommandSender;

import fr.devsylone.fallenkingdom.Fk;

import java.util.List;

public class Start extends FkCommand
{
	public Start()
	{
		super("start", Messages.CMD_MAP_GAME_START, CommandRole.ADMIN);
	}

	@Override
	public CommandResult execute(Fk plugin, CommandSender sender, List<String> args, String label)
	{
		plugin.getGame().start();
		plugin.getScoreboardManager().recreateAllScoreboards();
		return CommandResult.SUCCESS;
	}
}
