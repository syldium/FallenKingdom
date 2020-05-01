package fr.devsylone.fallenkingdom.commands.rules.rulescommands.booleancommands;

import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fkpi.rules.Rule;

import fr.devsylone.fallenkingdom.commands.rules.FkBooleanRuleCommand;

public class RespawnAtHome extends FkBooleanRuleCommand
{
	public RespawnAtHome()
	{
		super("respawnAtHome", Messages.CMD_MAP_RULES_RESPAWN_AT_HOME, Rule.RESPAWN_AT_HOME);
	}

	@Override
	protected void sendMessage(boolean newValue) {
		if (newValue)
			broadcast(Messages.CMD_RULES_RESPAWN_BASE.getMessage());
		else
			broadcast(Messages.CMD_RULES_RESPAWN_VANILLA.getMessage());
	}
}
