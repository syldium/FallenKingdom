package fr.devsylone.fallenkingdom.commands.game.gamescommands;

import fr.devsylone.fallenkingdom.utils.Messages;
import org.bukkit.entity.Player;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.game.FkGameCommand;
import fr.devsylone.fallenkingdom.players.FkPlayer;

public class Reset extends FkGameCommand
{
	public Reset()
	{
		super("reset", Messages.CMD_MAP_GAME_RESET.getMessage());
		permission = ADMIN_PERMISSION;
	}

	public void execute(Player sender, FkPlayer fkp, String[] args)
	{
		if(!Fk.getInstance().getCommandManager().isConfirmed("reset"))
		{
			fkp.sendMessage(createWarning(Messages.WARNING_GAME_RESET, true));
			Fk.getInstance().getCommandManager().setConfirmed("reset", true);
		}
		else
		{
			broadcast(Messages.CMD_GAME_RESET.getMessage());
			Fk.getInstance().reset();
			Fk.getInstance().getCommandManager().setConfirmed("reset", false);
			
		}
	}
}
