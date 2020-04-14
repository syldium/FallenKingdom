package fr.devsylone.fallenkingdom.commands.rules;

import fr.devsylone.fallenkingdom.exception.FkLightException;
import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fkpi.FkPI;

public abstract class FkBooleanRuleCommand extends FkRuleCommand
{
	public FkBooleanRuleCommand(String path, Messages description)
	{
		super(path, "<true|false>", 1, description);
	}

	protected void setRuleValue(String input)
	{
		if((!input.equalsIgnoreCase("true")) && (!input.equalsIgnoreCase("false")))
			throw new FkLightException(Messages.CMD_ERROR_BOOL_FORMAT);

		if(Boolean.valueOf(input) == FkPI.getInstance().getRulesManager().getRuleByName(getClass().getSimpleName()))
			throw new FkLightException(Messages.CMD_ERROR_RULES_ALREADY_DEFINED);

		FkPI.getInstance().getRulesManager().setRuleByName(getClass().getSimpleName(), Boolean.valueOf(input));
	}

	protected void broadcastOnOff(boolean state, Messages msg)
	{
		broadcastState(state, msg, Messages.CMD_RULES_ACTIVATED, Messages.CMD_RULES_DEACTIVATED);;
	}

	protected void broadcastPossibleImpossible(boolean state, Messages msg)
	{
		broadcastState(state, msg, Messages.CMD_RULES_POSSIBLE, Messages.CMD_RULES_IMPOSSIBLE);;
	}

	protected void broadcastState(boolean state, Messages base, Messages trueMsg, Messages falseMsg)
	{
		Messages value = state ? trueMsg : falseMsg;
		broadcast(base.getMessage().replace("%state%", value.getMessage()));
	}
}
