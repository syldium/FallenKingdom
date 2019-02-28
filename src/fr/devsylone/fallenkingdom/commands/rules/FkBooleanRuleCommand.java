package fr.devsylone.fallenkingdom.commands.rules;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.exception.FkLightException;

public abstract class FkBooleanRuleCommand extends FkRuleCommand
{
	public FkBooleanRuleCommand(String path, String description)
	{
		super(path, "<true|false>", 1, description);
	}

	protected void setRuleValue(String input)
	{
		if((!input.equalsIgnoreCase("true")) && (!input.equalsIgnoreCase("false")))
			throw new FkLightException(input + " n'est pas un boolean valide ! (true - false) ");

		if(Boolean.valueOf(input).booleanValue() == ((Boolean) Fk.getInstance().getFkPI().getRulesManager().getRuleByName(getClass().getSimpleName()).getValue()).booleanValue())
			throw new FkLightException("Cette règle est déjà définie ! ");
		
		Fk.getInstance().getFkPI().getRulesManager().getRuleByName(getClass().getSimpleName()).setValue(Boolean.valueOf(input));
	}
}
