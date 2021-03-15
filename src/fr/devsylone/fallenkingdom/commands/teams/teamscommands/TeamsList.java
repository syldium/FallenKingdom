package fr.devsylone.fallenkingdom.commands.teams.teamscommands;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandPermission;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandResult;
import fr.devsylone.fallenkingdom.commands.abstraction.FkCommand;
import fr.devsylone.fallenkingdom.exception.FkLightException;
import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fkpi.teams.Team;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
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
		builder.add(ChatColor.DARK_GREEN + "§m------------------- " + ChatColor.DARK_BLUE + Messages.CMD_TEAMS_AND_CHESTS_LIST + ChatColor.DARK_GREEN + " §m-------------------");

		for(Team team : plugin.getFkPI().getTeamManager().getTeams())
		{
			ChatColor color = team.getChatColor();
			builder.add(color + " " + team.getName() + " :");
			builder.add(color + " " + Messages.CMD_TEAM_LIST_PLAYERS.getMessage());

			if(!team.getPlayers().isEmpty())
				for(String pl : team.getPlayers())
					builder.add(color + "     -" + pl);
			else
				builder.add(color + "   §4/");

			if(team.getBase() == null)
				builder.add(color + Messages.CMD_TEAM_LIST_POSITION.getMessage()
						.replace("%x%", "§4/" + color)
						.replace("%y%", "§4/" + color)
						.replace("%z%", "§4/" + color)
						.replace("%dim%", "")
				);
			else
			{
				Location center = team.getBase().getCenter();
				builder.add(color + Messages.CMD_TEAM_LIST_POSITION.getMessage()
						.replace("%x%", String.valueOf(center.getBlockX()) + color)
						.replace("%y%", String.valueOf(center.getBlockY()) + color)
						.replace("%z%", String.valueOf(center.getBlockZ()) + color)
						.replace("%dim%", getEnvironmentName(center) + color)
				);
			}
			builder.add("§2§m-----");
		}

		builder.set(builder.size() - 1, ChatColor.DARK_GREEN + "§m--------------------------------------------");
		sender.sendMessage(builder.toArray(new String[0]));
		return CommandResult.SUCCESS;
	}

	private String getEnvironmentName(Location location) {
		if (location.getWorld().getEnvironment().equals(World.Environment.NORMAL)) {
			return "";
		}
		return " (" + location.getWorld().getEnvironment().name().toLowerCase().replace("_", " ") + ")";
	}
}
