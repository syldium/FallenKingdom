package fr.devsylone.fallenkingdom.commands.teams.teamscommands;

import fr.devsylone.fallenkingdom.commands.ArgumentParser;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandRole;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandResult;
import fr.devsylone.fallenkingdom.commands.abstraction.FkCommand;
import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fkpi.api.ITeam;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.devsylone.fallenkingdom.Fk;

import java.util.List;

public class AddPlayer extends FkCommand
{
	public AddPlayer()
	{
		super("addPlayer", "<player> <team>", Messages.CMD_MAP_TEAM_ADD_PLAYER, CommandRole.ADMIN);
	}

	@Override
	public CommandResult execute(Fk plugin, CommandSender sender, List<String> args, String label)
	{
		Player player = Bukkit.getPlayer(args.get(0));
		if(player != null)
			args.set(0, player.getName());

		List<String> players = ArgumentParser.parsePlayers(sender, args.get(0));
		for (String p : players) {
			ITeam team = plugin.getFkPI().getTeamManager().addPlayer(p, args.get(1));
			ChatColor color = team.getChatColor();
			if (player != null)
				player.setDisplayName(color + player.getName() + ChatColor.WHITE);
			broadcast(Messages.CMD_TEAM_ADD_PLAYER.getMessage()
					.replace("%player%", color + p)
					.replace("%team%", color + args.get(1)),
			2, args);
		}
		if (!players.isEmpty())
			plugin.getDisplayService().updateAll();
		return CommandResult.SUCCESS;
	}
}
