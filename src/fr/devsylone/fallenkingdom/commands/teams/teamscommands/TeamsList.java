package fr.devsylone.fallenkingdom.commands.teams.teamscommands;

import java.util.List;

import fr.devsylone.fallenkingdom.commands.abstraction.CommandPermission;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandResult;
import fr.devsylone.fallenkingdom.commands.abstraction.FkCommand;
import fr.devsylone.fallenkingdom.utils.ChatUtils;
import fr.devsylone.fallenkingdom.utils.Messages;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.exception.FkLightException;
import fr.devsylone.fkpi.teams.Team;

public class TeamsList extends FkCommand
{
	public TeamsList()
	{
		super("list", Messages.CMD_MAP_TEAM_LIST, CommandPermission.PLAYER);
	}

	@Override
	public CommandResult execute(Fk plugin, CommandSender sender, List<String> args, String label)
	{
		if(plugin.getFkPI().getTeamManager().getTeams().size() < 1)
			throw new FkLightException("Il n'y a aucune équipe !");

		StringBuilder builder = new StringBuilder(ChatColor.DARK_GREEN + "§m-------------------" + ChatColor.DARK_BLUE + " Liste " + ChatColor.DARK_GREEN + "§m-------------------");

		for(Team team : Fk.getInstance().getFkPI().getTeamManager().getTeams())
		{
			ChatColor color = team.getChatColor();
			builder.append(color + " " + team.getName() + " :");
			builder.append(color + "  Joueurs :");

			if(!team.getPlayers().isEmpty())
				for(String pl : team.getPlayers())
					builder.append(color + "     -" + pl);
			else
				builder.append(color + "   §4/");

			builder.append(Messages.CMD_LIST_POSITION.getMessage()
					.replace("%x%", team.getBase() == null ? "/" : String.valueOf(team.getBase().getCenter().getBlockX()))
					.replace("%y%", team.getBase() == null ? "/" : String.valueOf(team.getBase().getCenter().getBlockY()))
					.replace("%z%", team.getBase() == null ? "/" : String.valueOf(team.getBase().getCenter().getBlockZ()))
			);
			builder.append("§2§m-----");
		}

		builder.append(ChatColor.DARK_GREEN + "§m--------------------------------------------");
		ChatUtils.sendMessage(sender, builder.toString());
		return CommandResult.SUCCESS;
	}
}
