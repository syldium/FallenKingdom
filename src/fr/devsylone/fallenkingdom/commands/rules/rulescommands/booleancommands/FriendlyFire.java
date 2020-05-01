package fr.devsylone.fallenkingdom.commands.rules.rulescommands.booleancommands;

import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fkpi.rules.Rule;

import fr.devsylone.fallenkingdom.commands.rules.FkBooleanRuleCommand;

public class FriendlyFire extends FkBooleanRuleCommand
{
	public FriendlyFire()
	{
		super("friendlyFire", Messages.CMD_MAP_RULES_FRIENDLY_FIRE, Rule.FRIENDLY_FIRE);
	}

	@Override
	protected void sendMessage(boolean newValue) {
		broadcastOnOff(newValue, Messages.CMD_RULES_FRIENDLY_FIRE);
	}
}
