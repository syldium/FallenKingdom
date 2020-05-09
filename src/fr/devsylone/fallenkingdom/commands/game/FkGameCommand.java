package fr.devsylone.fallenkingdom.commands.game;

import com.google.common.collect.ImmutableList;
import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.abstraction.FkCommand;
import fr.devsylone.fallenkingdom.commands.abstraction.FkParentCommand;
import fr.devsylone.fallenkingdom.commands.game.gamescommands.*;
import fr.devsylone.fallenkingdom.utils.ChatUtils;
import fr.devsylone.fallenkingdom.utils.Messages;
import org.bukkit.ChatColor;

public class FkGameCommand extends FkParentCommand
{
	public FkGameCommand() {
		super("game", ImmutableList.<FkCommand>builder()
				.add(new Pause())
				.add(new Reset())
				.add(new Restore())
				.add(new Resume())
				.add(new Start())
				.add(new StarterInv())
				.add(new Stop())
				.build()
		, Messages.CMD_MAP_GAME);
	}

	@Override
	protected void broadcast(String message) {
		Fk.broadcast(ChatColor.GOLD + message, ChatUtils.GAME);
	}
}
