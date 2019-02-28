package fr.devsylone.fallenkingdom.commands.rules.rulescommands.capcommands;

import org.bukkit.entity.Player;

import fr.devsylone.fallenkingdom.commands.rules.FkCapCommand;
import fr.devsylone.fallenkingdom.players.FkPlayer;

public class NetherCap extends FkCapCommand
{
	public NetherCap()
	{
		super("netherCap", "Définit le jour où le nether devient actif.");
	}

	public void execute(Player sender, FkPlayer fkp, String[] args)
	{
		executeCap(args[0], "Le nether est maintenant actif");
	}
}
