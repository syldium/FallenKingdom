package fr.devsylone.fallenkingdom.commands.teams.teamscommands;

import fr.devsylone.fallenkingdom.commands.abstraction.CommandRole;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandResult;
import fr.devsylone.fallenkingdom.commands.abstraction.FkCommand;
import fr.devsylone.fallenkingdom.utils.Messages;
import org.bukkit.command.CommandSender;

import fr.devsylone.fallenkingdom.Fk;

import java.util.List;

import static fr.devsylone.fallenkingdom.commands.teams.teamscommands.SetColor.invalidColor;

public class Create extends FkCommand
{
	public Create()
	{
		super("create", "<newteam>", Messages.CMD_MAP_TEAM_CREATE, CommandRole.ADMIN);
	}

	@Override
	public CommandResult execute(Fk plugin, CommandSender sender, List<String> args, String label)
	{
		try {
			if(!plugin.getFkPI().getTeamManager().createTeam(args.get(0)).getColor().isSame(args.get(0)))
				sender.sendMessage(Messages.WARNING_UNKNOWN_COLOR.getMessage());
		} catch (NumberFormatException ex) {
			// Seulement lors des lectures des couleurs hex
			invalidColor(sender, args.get(0), ex);
			return CommandResult.INVALID_ARGS;
		}

		broadcast(Messages.CMD_TEAM_CREATE.getMessage().replace("%team%", plugin.getFkPI().getTeamManager().getTeam(args.get(0)).toString()), 1, args);
		plugin.getScoreboardManager().refreshAllScoreboards();
		return CommandResult.SUCCESS;
	}
}
