package fr.devsylone.fallenkingdom.commands.teams.teamscommands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.teams.FkTeamCommand;
import fr.devsylone.fallenkingdom.players.FkPlayer;

public class AddPlayer extends FkTeamCommand
{
	public AddPlayer()
	{
		super("addPlayer", "<player> <team>", 2, "Ajoute un joueur à une équipe.");
	}

	public void execute(Player sender, FkPlayer fkp, String[] args)
	{
		@SuppressWarnings("deprecation")
		OfflinePlayer op = Bukkit.getOfflinePlayer(args[0]);
		if(op != null)
			args[0] = op.getName();
		
		Fk.getInstance().getFkPI().getTeamManager().addPlayer(args[0], args[1]);
		ChatColor color = Fk.getInstance().getFkPI().getTeamManager().getTeam(args[1]).getChatColor();
		if(Bukkit.getPlayer(args[0]) != null)
		{
			Player p = Bukkit.getPlayer(args[0]);
			p.setDisplayName(color + p.getName() + ChatColor.WHITE);
		}
		if(args.length < 3 || !args[2].equalsIgnoreCase("nobroadcast"))
			broadcast(color + args[0] + ChatColor.GOLD + " a rejoint l'équipe " + color + args[1] + ChatColor.GOLD + " !");
	}
}
