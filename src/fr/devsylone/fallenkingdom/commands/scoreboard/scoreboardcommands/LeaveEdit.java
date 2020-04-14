package fr.devsylone.fallenkingdom.commands.scoreboard.scoreboardcommands;

import fr.devsylone.fallenkingdom.utils.Messages;
import org.bukkit.entity.Player;

import fr.devsylone.fallenkingdom.commands.scoreboard.FkScoreboardCommand;
import fr.devsylone.fallenkingdom.players.FkPlayer;

public class LeaveEdit extends FkScoreboardCommand
{
	public LeaveEdit()
	{
		super("leaveEdit", "", 0, Messages.CMD_MAP_SCOREBOARD_LEAVE_EDIT);
	}

	public void execute(Player sender, FkPlayer fkp, String[] args)
	{
		fkp.exitSbDisplayer();
	}
}
