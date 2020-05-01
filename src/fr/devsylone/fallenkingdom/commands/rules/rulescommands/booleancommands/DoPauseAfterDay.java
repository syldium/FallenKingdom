package fr.devsylone.fallenkingdom.commands.rules.rulescommands.booleancommands;

import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fkpi.rules.Rule;

import fr.devsylone.fallenkingdom.commands.rules.FkBooleanRuleCommand;

public class DoPauseAfterDay extends FkBooleanRuleCommand
{
	public DoPauseAfterDay()
	{
		super("doPauseAfterDay", Messages.CMD_MAP_RULES_DO_PAUSE_AFTER_DAY, Rule.DO_PAUSE_AFTER_DAY);
	}

	@Override
	protected void sendMessage(boolean newValue) {
		broadcastOnOff(newValue, Messages.CMD_RULES_DO_PAUSE_AFTER_DAY);
	}
}
