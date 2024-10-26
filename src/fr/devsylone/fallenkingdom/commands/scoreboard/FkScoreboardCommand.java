package fr.devsylone.fallenkingdom.commands.scoreboard;

import com.google.common.collect.ImmutableList;
import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.abstraction.FkCommand;
import fr.devsylone.fallenkingdom.commands.abstraction.FkParentCommand;
import fr.devsylone.fallenkingdom.commands.scoreboard.scoreboardcommands.*;
import fr.devsylone.fallenkingdom.utils.Messages;
import org.bukkit.ChatColor;

public class FkScoreboardCommand extends FkParentCommand
{
	public FkScoreboardCommand()
	{
		super("scoreboard", ImmutableList.<FkCommand>builder()
				.add(new Edit())
				.add(new LeaveEdit())
				.add(new RemoveLine())
				.add(new Reset())
				.add(new SetLine())
				.add(new SetName())
				.add(new Undo())
				.build()
		, Messages.CMD_MAP_SCOREBOARD);
	}

	@Override
	protected void broadcast(String message)
	{
		Fk.broadcast(ChatColor.GOLD + message, Messages.PREFIX_SCOREBOARD.getMessage());
	}
}
