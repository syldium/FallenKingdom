package fr.devsylone.fallenkingdom.commands.teams.teamscommands;

import fr.devsylone.fallenkingdom.commands.abstraction.CommandRole;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandResult;
import fr.devsylone.fallenkingdom.commands.abstraction.FkCommand;
import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fkpi.teams.Team;
import org.bukkit.command.CommandSender;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.exception.FkLightException;

import java.util.List;

public class Remove extends FkCommand
{
	public Remove()
	{
		super("remove", "<team>", Messages.CMD_MAP_TEAM_REMOVE, CommandRole.ADMIN);
	}

	@Override
	public CommandResult execute(Fk plugin, CommandSender sender, List<String> args, String label)
	{
		Team team = plugin.getFkPI().getTeamManager().getTeamOrThrow(args.get(0));

		plugin.getFkPI().getTeamManager().removeTeam(args.get(0));
		broadcast(Messages.CMD_TEAM_REMOVE.getMessage().replace("%team%", team.toString()),1, args);
		plugin.getDisplayService().updateAll();
		return CommandResult.SUCCESS;
	}
}
