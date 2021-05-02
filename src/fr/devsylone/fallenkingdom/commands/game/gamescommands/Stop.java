package fr.devsylone.fallenkingdom.commands.game.gamescommands;

import fr.devsylone.fallenkingdom.commands.abstraction.CommandRole;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandResult;
import fr.devsylone.fallenkingdom.commands.abstraction.Confirmable;
import fr.devsylone.fallenkingdom.commands.abstraction.FkCommand;
import fr.devsylone.fallenkingdom.utils.Messages;
import org.bukkit.command.CommandSender;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.exception.FkLightException;
import fr.devsylone.fallenkingdom.game.Game;

import java.util.List;

public class Stop extends FkCommand implements Confirmable
{
	public Stop()
	{
		super("stop", Messages.CMD_MAP_GAME_STOP, CommandRole.ADMIN);
	}

	@Override
	public CommandResult execute(Fk plugin, CommandSender sender, List<String> args, String label)
	{
		if (Fk.getInstance().getGame().getState().equals(Game.GameState.BEFORE_STARTING)) {
			throw new FkLightException(Messages.CMD_ERROR_GAME_NOT_STARTED);
		}

		if (isConfirmed(sender)) {
			plugin.stop();
			Fk.broadcast(Messages.CMD_GAME_STOP.getMessage());
			return CommandResult.SUCCESS;
		}

		sender.sendMessage(createWarning(Messages.WARNING_STOP, true));
		addConfirmed(sender);
		return CommandResult.SUCCESS;
	}
}
