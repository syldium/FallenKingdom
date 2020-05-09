package fr.devsylone.fallenkingdom.commands.rules.rulescommands.capcommands;

import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fkpi.rules.Rule;

import fr.devsylone.fallenkingdom.commands.rules.FkCapCommand;

public class TntCap extends FkCapCommand
{
	public TntCap()
	{
		super("tntCap", Messages.CMD_MAP_RULES_TNT_CAP, Rule.TNT_CAP, Messages.CMD_RULES_CAP_TNT);
	}
}
