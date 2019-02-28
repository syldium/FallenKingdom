package fr.devsylone.fallenkingdom.commands.teams.teamscommands;

import org.bukkit.entity.Player;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.teams.FkTeamCommand;
import fr.devsylone.fallenkingdom.players.FkPlayer;

public class Help extends FkTeamCommand
{
	public Help()
	{
		super("Help", "", 0, "Ben c'est ça :D.");
	}

	public void execute(Player sender, FkPlayer fkp, String[] args)
	{
		Fk.getInstance().getCommandManager().sendHelp("team", sender);
	}
}
