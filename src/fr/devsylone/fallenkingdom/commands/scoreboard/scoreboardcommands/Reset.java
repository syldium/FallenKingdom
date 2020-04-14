package fr.devsylone.fallenkingdom.commands.scoreboard.scoreboardcommands;

import fr.devsylone.fallenkingdom.utils.Messages;
import org.bukkit.entity.Player;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.scoreboard.FkScoreboardCommand;
import fr.devsylone.fallenkingdom.players.FkPlayer;

public class Reset extends FkScoreboardCommand
{
	public Reset()
	{
		super("reset", "", 0, Messages.CMD_MAP_SCOREBOARD_RESET);
	}

	public void execute(final Player sender, final FkPlayer fkp, String[] args)
	{
		if(!Fk.getInstance().getCommandManager().isConfirmed("sbreset"))
		{
			fkp.sendMessage(createWarning(Messages.WARNING_SCOREBOARD_RESET, true));

			Fk.getInstance().getCommandManager().setConfirmed("sbreset", true);
		}
		else
		{
			fkp.sendMessage(Messages.CMD_MAP_SCOREBOARD_RESET);
			Fk.getInstance().getScoreboardManager().reset();
			Fk.getInstance().getScoreboardManager().recreateAllScoreboards();
			Fk.getInstance().getScoreboardManager().refreshAllScoreboards();
		}
	}
}
