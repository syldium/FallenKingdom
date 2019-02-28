package fr.devsylone.fallenkingdom.commands.rules.rulescommands.capcommands;

import org.bukkit.entity.Player;

import fr.devsylone.fallenkingdom.commands.rules.FkCapCommand;
import fr.devsylone.fallenkingdom.players.FkPlayer;

public class TntCap extends FkCapCommand
{
	public TntCap()
	{
		super("tntCap", "Définit le jour où les assauts peuvent commencer.");
	}

	public void execute(Player sender, FkPlayer fkp, String[] args)
	{
		executeCap(args[0], "Les assauts sont maintenant actifs");
	}
}
