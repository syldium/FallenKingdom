package fr.devsylone.fallenkingdom.commands.rules.rulescommands.capcommands;

import fr.devsylone.fallenkingdom.utils.Messages;
import org.bukkit.entity.Player;

import fr.devsylone.fallenkingdom.commands.rules.FkCapCommand;
import fr.devsylone.fallenkingdom.players.FkPlayer;

public class NetherCap extends FkCapCommand
{
	public NetherCap()
	{
		super("netherCap", Messages.CMD_MAP_RULES_NETHER_CAP);
	}

	public void execute(Player sender, FkPlayer fkp, String[] args)
	{
		executeCap(args[0], Messages.CMD_RULES_CAP_NETHER);
	}
}
