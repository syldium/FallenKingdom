package fr.devsylone.fallenkingdom.commands.teams.teamscommands;

import org.bukkit.entity.Player;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.teams.FkTeamCommand;
import fr.devsylone.fallenkingdom.players.FkPlayer;

public class RemovePlayer extends FkTeamCommand
{
	public RemovePlayer()
	{
		super("RemovePlayer", "<player>", 1, "Enlever un joueur d'une équipe.");
	}

	public void execute(Player sender, FkPlayer fkp, String[] args)
	{
		Fk.getInstance().getFkPI().getTeamManager().removePlayerOfHisTeam(args[0]);
		sender.setDisplayName(sender.getName());

		if(args.length < 2 || !args[1].equalsIgnoreCase("nobroadcast"))
			broadcast(args[0] + " a été exclu de son équipe ! ");
	}
}
