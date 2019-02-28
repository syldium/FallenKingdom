package fr.devsylone.fallenkingdom.commands.teams.teamscommands;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.teams.FkTeamCommand;
import fr.devsylone.fallenkingdom.players.FkPlayer;

public class Random extends FkTeamCommand
{
	public Random()
	{
		super("Random", "", 0, "Crée des équipes aléatoires équilibrées.");
	}

	public void execute(Player sender, FkPlayer fkp, String[] args)
	{
		java.util.List<String> players = new ArrayList<>();
		for(Player p : Bukkit.getOnlinePlayers())
			players.add(p.getName());
		Fk.getInstance().getFkPI().getTeamManager().random(players);

		broadcast("Les équipes ont été formées aléatoirement ! §e/fk team list §6 pour la liste des équipes et de leurs joueurs !");
	}
}
