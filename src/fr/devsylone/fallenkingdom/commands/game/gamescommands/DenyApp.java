package fr.devsylone.fallenkingdom.commands.game.gamescommands;

import org.bukkit.entity.Player;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.game.FkGameCommand;
import fr.devsylone.fallenkingdom.exception.FkLightException;
import fr.devsylone.fallenkingdom.players.FkPlayer;
import fr.devsylone.fallenkingdom.utils.FkSound;

public class DenyApp extends FkGameCommand
{
	public DenyApp()
	{
		super("DenyApp", "Annule la dernière configuration envoyée via le logiciel");
	}

	public void execute(Player sender, FkPlayer fkp, String[] args)
	{
		if (!sender.isOp())
			throw new FkLightException("Vous devez être opérateur pour effectuer cette action");
		Fk.getInstance().getServerSocket().getFkPIArray();
		Fk.broadcast("§4La reconfiguration a été annulée !", FkSound.WITHER_DEATH);
		Fk.getInstance().getServerSocket().clearFkPIArray();
	}
}
