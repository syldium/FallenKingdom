package fr.devsylone.fallenkingdom.commands.teams.teamscommands;

import fr.devsylone.fallenkingdom.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.teams.FkTeamCommand;
import fr.devsylone.fallenkingdom.players.FkPlayer;

public class AddPlayer extends FkTeamCommand
{
	public AddPlayer()
	{
		super("addPlayer", "<player> <team>", 2, Messages.CMD_MAP_TEAM_SET_COLOR);
	}

	public void execute(Player sender, FkPlayer fkp, String[] args)
	{
		Player player = Bukkit.getPlayer(args[0]);
		if(player != null)
			args[0] = player.getName();

		Fk.getInstance().getFkPI().getTeamManager().addPlayer(args[0], args[1]);
		ChatColor color = Fk.getInstance().getFkPI().getTeamManager().getTeam(args[1]).getChatColor();
		if(player != null)
		{
			Player p = Bukkit.getPlayer(args[0]);
			p.setDisplayName(color + p.getName() + ChatColor.WHITE);
		}
		if(args.length < 3 || !args[2].equalsIgnoreCase("nobroadcast"))
			broadcast(Messages.CMD_TEAM_ADD_PLAYER.getMessage()
					.replace("%player%", color + args[0])
					.replace("%team%", color + args[1])
			);
	}
}
