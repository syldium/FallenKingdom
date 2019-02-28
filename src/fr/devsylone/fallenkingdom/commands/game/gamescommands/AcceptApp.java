package fr.devsylone.fallenkingdom.commands.game.gamescommands;

import org.bukkit.entity.Player;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.game.FkGameCommand;
import fr.devsylone.fallenkingdom.exception.FkLightException;
import fr.devsylone.fallenkingdom.players.FkPlayer;
import fr.devsylone.fallenkingdom.utils.FkSound;

public class AcceptApp extends FkGameCommand
{
	public AcceptApp()
	{
		super("AcceptApp", "Met en place la dernière configuration envoyée via le logiciel");
	}

	public void execute(Player sender, FkPlayer fkp, String[] args)
	{
		if (!sender.isOp())
			throw new FkLightException("Vous devez être opérateur pour effectuer cette action");
		Fk.getInstance().reset();
		Fk.getInstance().getFkPI().fromStringArray(Fk.getInstance().getServerSocket().getFkPIArray());
		Fk.broadcast("§b§2La partie a été reconfigurée depuis le logiciel de configuration !", FkSound.WITHER_DEATH);
		Fk.getInstance().getServerSocket().clearFkPIArray();
	}
}
