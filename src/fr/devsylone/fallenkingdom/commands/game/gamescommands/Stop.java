package fr.devsylone.fallenkingdom.commands.game.gamescommands;

import fr.devsylone.fallenkingdom.utils.Messages;
import org.bukkit.entity.Player;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.game.FkGameCommand;
import fr.devsylone.fallenkingdom.exception.FkLightException;
import fr.devsylone.fallenkingdom.game.Game;
import fr.devsylone.fallenkingdom.players.FkPlayer;

public class Stop extends FkGameCommand
{
	public Stop()
	{
		super("stop", Messages.CMD_MAP_GAME_STOP.getMessage());
		permission = ADMIN_PERMISSION;
	}

	public void execute(Player sender, FkPlayer fkp, String[] args)
	{
		if (!Fk.getInstance().getGame().getState().equals(Game.GameState.BEFORE_STARTING))
		{
			if (!Fk.getInstance().getCommandManager().isConfirmed("stop"))
			{
				fkp.sendMessage(createWarning(Messages.WARNING_STOP, true));
				Fk.getInstance().getCommandManager().setConfirmed("stop", true);

			}
			else
			{
				Fk.getInstance().stop();
				super.broadcast(Messages.CMD_GAME_STOP.getMessage());
			}
		}
		else
			throw new FkLightException(Messages.CMD_ERROR_GAME_NOT_STARTED);
		
	}
}
