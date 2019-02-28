package fr.devsylone.fallenkingdom.commands.rules.rulescommands.capcommands;

import org.bukkit.entity.Player;

import fr.devsylone.fallenkingdom.commands.rules.FkCapCommand;
import fr.devsylone.fallenkingdom.players.FkPlayer;

public class EndCap extends FkCapCommand
{
	public EndCap()
	{
		super("endCap", "Définit le jour où l'end devient actif.");
	}

	public void execute(Player sender, FkPlayer fkp, String[] args)
	{
		executeCap(args[0], "L'end est maintenant actif");
	}
}
