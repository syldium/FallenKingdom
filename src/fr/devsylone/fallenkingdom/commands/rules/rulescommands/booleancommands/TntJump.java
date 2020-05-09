package fr.devsylone.fallenkingdom.commands.rules.rulescommands.booleancommands;

import fr.devsylone.fallenkingdom.utils.Messages;

import fr.devsylone.fallenkingdom.commands.rules.FkBooleanRuleCommand;
import fr.devsylone.fkpi.rules.Rule;

public class TntJump extends FkBooleanRuleCommand
{
	public TntJump()
	{
		super("tntJump", Messages.CMD_MAP_RULES_TNT_JUMP, Rule.TNT_JUMP);
	}

	@Override
	protected void sendMessage(boolean newValue) {
		broadcastPossibleImpossible(newValue, Messages.CMD_RULES_TNT_JUMP);
	}
}
