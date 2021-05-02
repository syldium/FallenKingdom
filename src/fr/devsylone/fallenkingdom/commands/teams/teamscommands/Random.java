package fr.devsylone.fallenkingdom.commands.teams.teamscommands;

import java.util.List;
import java.util.stream.Collectors;

import fr.devsylone.fallenkingdom.commands.abstraction.CommandRole;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandResult;
import fr.devsylone.fallenkingdom.commands.abstraction.FkCommand;
import fr.devsylone.fallenkingdom.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.devsylone.fallenkingdom.Fk;

public class Random extends FkCommand
{
	public Random()
	{
		super("random", Messages.CMD_MAP_TEAM_RANDOM, CommandRole.ADMIN);
	}

	@Override
	public CommandResult execute(Fk plugin, CommandSender sender, List<String> args, String label)
	{
		List<String> players = Bukkit.getOnlinePlayers().stream()
				.map(Player::getName)
				.collect(Collectors.toList());
		plugin.getFkPI().getTeamManager().random(players);

		broadcast(Messages.CMD_TEAM_RANDOM.getMessage());
		plugin.getScoreboardManager().refreshAllScoreboards();
		return CommandResult.SUCCESS;
	}
}
