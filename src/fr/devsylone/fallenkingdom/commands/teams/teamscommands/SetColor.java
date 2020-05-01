package fr.devsylone.fallenkingdom.commands.teams.teamscommands;

import fr.devsylone.fallenkingdom.commands.abstraction.CommandPermission;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandResult;
import fr.devsylone.fallenkingdom.commands.abstraction.FkCommand;
import fr.devsylone.fallenkingdom.utils.Messages;
import org.bukkit.command.CommandSender;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.exception.FkLightException;
import fr.devsylone.fkpi.teams.Team;
import fr.devsylone.fkpi.util.Color;

import java.util.List;

public class SetColor extends FkCommand
{
	public SetColor()
	{
		super("setColor", "<team> <color>", Messages.CMD_MAP_TEAM_SET_COLOR, CommandPermission.ADMIN);
	}

	@Override
	public CommandResult execute(Fk plugin, CommandSender sender, List<String> args, String label)
	{
		Team team;
		if((team = plugin.getFkPI().getTeamManager().getTeam(args.get(0))) == null)
			throw new FkLightException("Cette équipe n'existe pas");
		team.setColor(Color.forName(args.get(0)));
		broadcast("L'équipe " + team.toString() + " §6est maintenant de couleur " + team.getColor().getChatColor() + team.getColor().getGenredName(Color.GENRE_F));
		plugin.getScoreboardManager().refreshNicks();
		return CommandResult.SUCCESS;
	}
}
