package fr.devsylone.fallenkingdom.commands.teams.teamscommands;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandPermission;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandResult;
import fr.devsylone.fallenkingdom.commands.abstraction.FkCommand;
import fr.devsylone.fallenkingdom.exception.FkLightException;
import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fkpi.teams.Team;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

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
			throw new FkLightException(Messages.CMD_ERROR_NO_TEAM);

		List<String> builder = new ArrayList<>();
		builder.add(ChatColor.DARK_GREEN + "§m-------------------" + ChatColor.DARK_BLUE + " Liste " + ChatColor.DARK_GREEN + "§m-------------------");

		for(Team team : Fk.getInstance().getFkPI().getTeamManager().getTeams())
		{
			ChatColor color = team.getChatColor();
			builder.add(color + " " + team.getName() + " :");
			builder.add(color + "  Joueurs :");

			if(!team.getPlayers().isEmpty())
				for(String pl : team.getPlayers())
					builder.add(color + "     -" + pl);
			else
				builder.add(color + "   §4/");

			builder.add(color + Messages.CMD_TEAM_LIST_POSITION.getMessage()
					.replace("%x%", (team.getBase() == null ? "§4/" : String.valueOf(team.getBase().getCenter().getBlockX())) + color)
					.replace("%y%", (team.getBase() == null ? "§4/" : String.valueOf(team.getBase().getCenter().getBlockY())) + color)
					.replace("%z%", (team.getBase() == null ? "§4/" : String.valueOf(team.getBase().getCenter().getBlockZ())) + color)
			);
			builder.add("§2§m-----");
		}

		builder.set(builder.size() - 1, ChatColor.DARK_GREEN + "§m--------------------------------------------");
		sender.sendMessage(builder.toArray(new String[0]));
		return CommandResult.SUCCESS;
	}
}
