package fr.devsylone.fallenkingdom.commands.scoreboard.scoreboardcommands;

import org.bukkit.entity.Player;

import fr.devsylone.fallenkingdom.commands.scoreboard.FkScoreboardCommand;
import fr.devsylone.fallenkingdom.players.FkPlayer;

public class LeaveEdit extends FkScoreboardCommand
{
	public LeaveEdit()
	{
		super("LeaveEdit", "", 0, "Sortir du mode de configuration");
	}

	public void execute(Player sender, FkPlayer fkp, String[] args)
	{
		fkp.exitSbDisplayer();
	}
}
