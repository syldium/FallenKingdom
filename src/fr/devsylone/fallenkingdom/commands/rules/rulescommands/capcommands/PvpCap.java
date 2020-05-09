package fr.devsylone.fallenkingdom.commands.rules.rulescommands.capcommands;

import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fkpi.rules.Rule;

import fr.devsylone.fallenkingdom.commands.rules.FkCapCommand;

public class PvpCap extends FkCapCommand
{
	public PvpCap()
	{
		super("pvpCap", Messages.CMD_MAP_RULES_PVP_CAP, Rule.PVP_CAP, Messages.CMD_RULES_CAP_PVP);
	}
}
