package fr.devsylone.fallenkingdom.commands.teams.teamscommands;

import fr.devsylone.fallenkingdom.commands.abstraction.CommandPermission;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandResult;
import fr.devsylone.fallenkingdom.commands.abstraction.FkCommand;
import fr.devsylone.fallenkingdom.utils.Messages;
import org.bukkit.command.CommandSender;

import fr.devsylone.fallenkingdom.Fk;

import java.util.List;

public class Create extends FkCommand
{
	public Create()
	{
		super("create", "<newteam>", Messages.CMD_MAP_TEAM_CREATE, CommandPermission.ADMIN);
	}

	@Override
	public CommandResult execute(Fk plugin, CommandSender sender, List<String> args, String label)
	{
		if(!plugin.getFkPI().getTeamManager().createTeam(args.get(0)))
			sender.sendMessage(Messages.WARNING_UNKNOWN_COLOR.getMessage());

		broadcast("L'équipe " + plugin.getFkPI().getTeamManager().getTeam(args.get(0)).toString() + " §6a été créée !", 1, args);
		plugin.getScoreboardManager().refreshAllScoreboards();
		return CommandResult.SUCCESS;
	}
}
