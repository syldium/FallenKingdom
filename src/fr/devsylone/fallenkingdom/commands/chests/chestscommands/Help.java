package fr.devsylone.fallenkingdom.commands.chests.chestscommands;

import org.bukkit.entity.Player;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.chests.FkChestsCommand;
import fr.devsylone.fallenkingdom.players.FkPlayer;

public class Help extends FkChestsCommand
{
	public Help()
	{
		super("help", "Ben c'est ça :D");
	}

	public void execute(Player sender, FkPlayer fkp, String[] args)
	{
		Fk.getInstance().getCommandManager().sendHelp("chests", sender);
	}
}
