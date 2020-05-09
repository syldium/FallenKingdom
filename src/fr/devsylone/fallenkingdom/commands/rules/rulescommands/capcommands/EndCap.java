package fr.devsylone.fallenkingdom.commands.rules.rulescommands.capcommands;

import fr.devsylone.fallenkingdom.utils.Messages;

import fr.devsylone.fallenkingdom.commands.rules.FkCapCommand;
import fr.devsylone.fkpi.rules.Rule;

public class EndCap extends FkCapCommand
{
	public EndCap()
	{
		super("endCap", Messages.CMD_MAP_RULES_END_CAP, Rule.END_CAP, Messages.CMD_RULES_CAP_END);
	}
}
