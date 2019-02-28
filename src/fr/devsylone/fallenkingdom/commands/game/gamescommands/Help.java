package fr.devsylone.fallenkingdom.commands.game.gamescommands;

import org.bukkit.entity.Player;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.game.FkGameCommand;
import fr.devsylone.fallenkingdom.players.FkPlayer;

public class Help extends FkGameCommand
{
	public Help()
	{
		super("help", "Ben c'est ça :D");
	}

	public void execute(Player sender, FkPlayer fkp, String[] args)
	{
		Fk.getInstance().getCommandManager().sendHelp("game", sender);
	}
}
