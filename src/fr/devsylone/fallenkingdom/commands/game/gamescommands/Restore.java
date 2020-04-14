package fr.devsylone.fallenkingdom.commands.game.gamescommands;

import fr.devsylone.fallenkingdom.utils.Messages;
import org.bukkit.entity.Player;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.game.FkGameCommand;
import fr.devsylone.fallenkingdom.exception.FkLightException;
import fr.devsylone.fallenkingdom.game.Game;
import fr.devsylone.fallenkingdom.players.FkPlayer;
import fr.devsylone.fallenkingdom.utils.FkSound;

public class Restore extends FkGameCommand
{
	public Restore()
	{
		super("restore", "[pause_id] (Par défaut restaure à la dernière pause)", 0, Messages.CMD_MAP_GAME_RESTORE.getMessage());
		permission = ADMIN_PERMISSION;
	}

	public void execute(Player sender, FkPlayer fkp, String[] args)
	{
		if(!Fk.getInstance().getGame().getState().equals(Game.GameState.PAUSE))
			throw new FkLightException(Messages.CMD_ERROR_NOT_IN_PAUSE);
		
		int id = -1;
		if(args.length > 0)
			try
			{
				id = Integer.parseInt(args[0]);
			}catch(NumberFormatException e)
			{
				throw new FkLightException(Messages.CMD_ERROR_PAUSE_ID.getMessage().replace("%id%", args[0]));
			}
		id = Fk.getInstance().getPauseRestorer().restoreAll(id);//Si l'id était -1 ça remet le bon
		Fk.broadcast(Messages.CMD_GAME_RESTORE.getMessage().replace("%id%", String.valueOf(id)), FkSound.NOTE_PLING);
	}
}
