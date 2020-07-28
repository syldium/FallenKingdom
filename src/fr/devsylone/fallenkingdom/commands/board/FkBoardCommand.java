package fr.devsylone.fallenkingdom.commands.board;

import org.bukkit.ChatColor;

import com.google.common.collect.ImmutableList;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.abstraction.FkCommand;
import fr.devsylone.fallenkingdom.commands.abstraction.FkParentCommand;
import fr.devsylone.fallenkingdom.commands.board.boardcommands.Connect;
import fr.devsylone.fallenkingdom.utils.ChatUtils;
import fr.devsylone.fallenkingdom.utils.Messages;

public class FkBoardCommand extends FkParentCommand
{
	public FkBoardCommand() {
		super("board", ImmutableList.<FkCommand>builder()
				.add(new Connect())
				.build()
		, Messages.CMD_MAP_BOARD);
	}

	@Override
	protected void broadcast(String message) {
		Fk.broadcast(ChatColor.GOLD + message, ChatUtils.BOARD);
	}
}
