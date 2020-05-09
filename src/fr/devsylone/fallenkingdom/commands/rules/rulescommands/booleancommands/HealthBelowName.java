package fr.devsylone.fallenkingdom.commands.rules.rulescommands.booleancommands;

import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fkpi.rules.Rule;

import fr.devsylone.fallenkingdom.commands.rules.FkBooleanRuleCommand;

public class HealthBelowName extends FkBooleanRuleCommand
{
	public HealthBelowName()
	{
		super("healthBelowName", Messages.CMD_MAP_RULES_HEALTH_BELOW_NAME, Rule.HEALTH_BELOW_NAME);
	}

	@Override
	protected void sendMessage(boolean newValue) {
		if (newValue)
			broadcast(Messages.CMD_RULES_HEALTH_BELOW_NAME_VISIBLE.getMessage());
		else
			broadcast(Messages.CMD_RULES_HEALTH_BELOW_NAME_HIDDEN.getMessage());
	}
}
