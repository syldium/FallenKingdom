package fr.devsylone.fallenkingdom.commands.teams.teamscommands;

import fr.devsylone.fallenkingdom.commands.abstraction.CommandPermission;
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
		super("remove", "<team>", Messages.CMD_MAP_TEAM_REMOVE, CommandPermission.ADMIN);
	}

	@Override
	public CommandResult execute(Fk plugin, CommandSender sender, List<String> args, String label)
	{
		Team team = plugin.getFkPI().getTeamManager().getTeam(args.get(0));
		if(team == null)
			throw new FkLightException(Messages.CMD_ERROR_UNKNOWN_TEAM);

		team.getPlayers().forEach(team::removePlayer);
		plugin.getFkPI().getTeamManager().removeTeam(args.get(0));
		broadcast("L'équipe " + team.toString() + " §6a été supprimée !",1, args);
		plugin.getScoreboardManager().refreshAllScoreboards();
		return CommandResult.SUCCESS;
	}
}
