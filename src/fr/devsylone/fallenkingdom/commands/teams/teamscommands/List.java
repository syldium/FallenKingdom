package fr.devsylone.fallenkingdom.commands.teams.teamscommands;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.teams.FkTeamCommand;
import fr.devsylone.fallenkingdom.exception.FkLightException;
import fr.devsylone.fallenkingdom.players.FkPlayer;
import fr.devsylone.fkpi.teams.Team;

public class List extends FkTeamCommand
{
	public List()
	{
		super("List", "", 0, "Donne le detail de toutes les équipes.");
	}

	public void execute(Player sender, FkPlayer fkp, String[] args)
	{
		java.util.List<String> list = new ArrayList<String>();

		list.add(ChatColor.DARK_GREEN + "§m-------------------" + ChatColor.DARK_BLUE + " Liste " + ChatColor.DARK_GREEN + "§m-------------------");

		for(Team team : Fk.getInstance().getFkPI().getTeamManager().getTeams())
		{
			ChatColor color = team.getChatColor();
			list.add(color + " " + team.getName() + " :");
			list.add(color + "  Joueurs :");

			if(!team.getPlayers().isEmpty())
				for(String pl : team.getPlayers())
					list.add(color + "     -" + pl);
			else
				list.add(color + "   §4/");
			
			list.add(color + "  Coordonées :");

			if(team.getBase() != null)
				list.add(color + "     x > §c" + team.getBase().getCenter().getBlockX() + color + " ; y > §c" + team.getBase().getCenter().getBlockY() + color + " ; z > §c" + team.getBase().getCenter().getBlockZ());
			else
				list.add(color + "   §4/");
			
			list.add("§2§m-----");
		}

		if(list.size() == 1)
			throw new FkLightException("Il n'y a aucune équipe !");
		
		list.set(list.size() - 1, ChatColor.DARK_GREEN + "§m--------------------------------------------");
		for(String s : list)
			fkp.sendMessage(s);
		
	}

}
