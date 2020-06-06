package fr.devsylone.fallenkingdom.commands.teams.teamscommands;

import fr.devsylone.fallenkingdom.commands.ArgumentParser;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandPermission;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandResult;
import fr.devsylone.fallenkingdom.commands.abstraction.FkCommand;
import fr.devsylone.fallenkingdom.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.devsylone.fallenkingdom.Fk;

import java.util.List;

public class RemovePlayer extends FkCommand
{
	public RemovePlayer()
	{
		super("removePlayer", "<player>", Messages.CMD_MAP_TEAM_REMOVE_PLAYER, CommandPermission.ADMIN);
	}

	@Override
	public CommandResult execute(Fk plugin, CommandSender sender, List<String> args, String label)
	{
		List<String> players = ArgumentParser.parsePlayers(sender, args.get(0));
		for (String p : players) {
			plugin.getFkPI().getTeamManager().removePlayerOfHisTeam(p);
			Player player = Bukkit.getPlayer(p);
			if (player != null)
				player.setDisplayName(player.getDisplayName());

			broadcast(Messages.CMD_TEAM_REMOVE_PLAYER.getMessage().replace("%player%", p), 1, args);
		}
		if (!players.isEmpty())
			plugin.getScoreboardManager().refreshAllScoreboards();
		return CommandResult.SUCCESS;
	}
}
