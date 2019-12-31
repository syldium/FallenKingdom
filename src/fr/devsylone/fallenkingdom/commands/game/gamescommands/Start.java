package fr.devsylone.fallenkingdom.commands.game.gamescommands;

import org.bukkit.entity.Player;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.game.FkGameCommand;
import fr.devsylone.fallenkingdom.players.FkPlayer;

public class Start extends FkGameCommand
{
	public Start()
	{
		super("start", "Lance la partie.");
		permission = ADMIN_PERMISSION;
	}

	public void execute(Player sender, FkPlayer fkp, String[] args)
	{
		Fk.getInstance().getGame().start();
	}
}
