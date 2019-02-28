package fr.devsylone.fallenkingdom.commands.teams.teamscommands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.exception.FkLightException;
import fr.devsylone.fallenkingdom.players.FkPlayer;

public class Remove extends fr.devsylone.fallenkingdom.commands.teams.FkTeamCommand
{
	public Remove()
	{
		super("Remove", "<team>", 1, "Supprimer une équipe.");
	}

	public void execute(Player sender, FkPlayer fkp, String[] args) throws Exception
	{
		if(Fk.getInstance().getFkPI().getTeamManager().getTeam(args[0]) == null)
			throw new FkLightException("Cette équipe n'existe pas !");
		
		ChatColor color=Fk.getInstance().getFkPI().getTeamManager().getTeam(args[0]).getChatColor();
		Object[] players = Fk.getInstance().getFkPI().getTeamManager().getTeam(args[0]).getPlayers().toArray();
		for(Object player : players)
			Fk.getInstance().getCommandManager().executeCommand(new String[] {"team", "removeplayer", String.valueOf(player)}, sender);
		
		Fk.getInstance().getFkPI().getTeamManager().removeTeam(args[0]);
		broadcast("L'équipe " + color+args[0] + " §6a été supprimée ! ");
	}
}
