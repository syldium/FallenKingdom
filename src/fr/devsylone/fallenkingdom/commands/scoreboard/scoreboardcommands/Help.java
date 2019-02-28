package fr.devsylone.fallenkingdom.commands.scoreboard.scoreboardcommands;

import org.bukkit.entity.Player;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.scoreboard.FkScoreboardCommand;
import fr.devsylone.fallenkingdom.players.FkPlayer;

public class Help extends FkScoreboardCommand
{
	public Help()
	{
		super("help", "", 0, "Ben c'est ça :D.");
	}

	public void execute(Player sender, FkPlayer fkp, String[] args)
	{
		Fk.getInstance().getCommandManager().sendHelp("scoreboard", sender);
	}
}
