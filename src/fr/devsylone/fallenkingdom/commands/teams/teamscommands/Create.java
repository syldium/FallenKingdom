package fr.devsylone.fallenkingdom.commands.teams.teamscommands;

import fr.devsylone.fallenkingdom.utils.Messages;
import org.bukkit.entity.Player;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.teams.FkTeamCommand;
import fr.devsylone.fallenkingdom.players.FkPlayer;

public class Create extends FkTeamCommand
{
	public Create()
	{
		super("create", "<team>", 1, Messages.CMD_MAP_TEAM_CREATE);
	}

	public void execute(Player sender, FkPlayer fkp, String[] args)
	{
		if(!Fk.getInstance().getFkPI().getTeamManager().createTeam(args[0]))
			fkp.sendMessage(createWarning(Messages.WARNING_UNKNOWN_COLOR, false));

		broadcast("L'équipe " + Fk.getInstance().getFkPI().getTeamManager().getTeam(args[0]).toString() + " §6a été créée !");
		
	}
}
