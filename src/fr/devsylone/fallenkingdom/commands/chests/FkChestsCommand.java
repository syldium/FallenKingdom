package fr.devsylone.fallenkingdom.commands.chests;

import com.google.common.collect.ImmutableList;
import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.abstraction.FkParentCommand;
import fr.devsylone.fallenkingdom.commands.chests.chestscommands.Add;
import fr.devsylone.fallenkingdom.commands.chests.chestscommands.ChestsList;
import fr.devsylone.fallenkingdom.commands.chests.chestscommands.Remove;
import fr.devsylone.fallenkingdom.commands.abstraction.FkCommand;
import fr.devsylone.fallenkingdom.utils.ChatUtils;
import fr.devsylone.fallenkingdom.utils.Messages;
import org.bukkit.ChatColor;

public class FkChestsCommand extends FkParentCommand
{
	public FkChestsCommand()
	{
		super("chests", ImmutableList.<FkCommand>builder()
				.add(new Add())
				.add(new ChestsList())
				.add(new Remove())
				.build()
		, Messages.CMD_MAP_CHEST);
	}

	@Override
	protected void broadcast(String message)
	{
		Fk.broadcast(ChatColor.GOLD + message, ChatUtils.CHESTS);
	}
}
