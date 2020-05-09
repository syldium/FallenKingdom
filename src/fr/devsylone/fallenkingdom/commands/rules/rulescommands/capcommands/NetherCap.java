package fr.devsylone.fallenkingdom.commands.rules.rulescommands.capcommands;

import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fkpi.rules.Rule;

import fr.devsylone.fallenkingdom.commands.rules.FkCapCommand;

public class NetherCap extends FkCapCommand
{
	public NetherCap()
	{
		super("netherCap", Messages.CMD_MAP_RULES_NETHER_CAP, Rule.NETHER_CAP, Messages.CMD_RULES_CAP_NETHER);
	}
}
