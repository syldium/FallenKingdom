package fr.devsylone.fallenkingdom.commands.rules;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.exception.FkLightException;
import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fkpi.FkPI;

public abstract class FkCapCommand extends FkRuleCommand
{
	public FkCapCommand(String path, Messages description)
	{
		super(path, "<day>", 1, description);
	}

	protected void executeCap(String arg, Messages msg)
	{
		int day = assertPositiveNumber(arg, false, Messages.CMD_ERROR_DAY_FORMAT);

		if(day <= Fk.getInstance().getGame().getDays())
			throw new FkLightException(Messages.CMD_ERROR_DAY_PASSED);

		if((int) FkPI.getInstance().getRulesManager().getRuleByName(getClass().getSimpleName()) <= Fk.getInstance().getGame().getDays())
			throw new FkLightException(Messages.CMD_ERROR_CAP_PASSED);
		FkPI.getInstance().getRulesManager().setRuleByName(getClass().getSimpleName(), day);
		broadcast(Messages.CMD_RULES_CAP.getMessage()
				.replace("%first%", msg.getMessage())
				.replace("%from%", (day == 1 ? Messages.CMD_RULES_CAP_FROM_DAY_1 : Messages.CMD_RULES_CAP_FROM_DAY).getMessage())
				.replace("%day%", String.valueOf(day))
		);
	}
}
