package fr.devsylone.fallenkingdom.commands.scoreboard.scoreboardcommands;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandRole;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandResult;
import fr.devsylone.fallenkingdom.commands.abstraction.FkPlayerCommand;
import fr.devsylone.fallenkingdom.players.FkPlayer;
import fr.devsylone.fallenkingdom.utils.Messages;
import org.bukkit.entity.Player;

import java.util.List;

public class LeaveEdit extends FkPlayerCommand
{
	public LeaveEdit()
	{
		super("leaveEdit", Messages.CMD_MAP_SCOREBOARD_LEAVE_EDIT, CommandRole.ADMIN);
	}

	@Override
	public CommandResult execute(Fk plugin, Player sender, FkPlayer fkp, List<String> args, String label)
	{
		fkp.exitSbDisplayer();
		return CommandResult.SUCCESS;
	}
}
