package fr.devsylone.fallenkingdom.commands.rules;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.exception.FkLightException;
import fr.devsylone.fkpi.rules.Rule;

public abstract class FkCapCommand extends FkRuleCommand
{
	public FkCapCommand(String path, String description)
	{
		super(path, "<day>", 1, description);
	}

	protected void executeCap(String arg, String msg)
	{
		try
		{
			Integer.parseInt(arg);
		}catch(NumberFormatException e)
		{
			throw new FkLightException(arg + " n'est pas un nombre valide !");
		}

		int day = Integer.parseInt(arg);

		if(day < 1)
			day = 1;

		Rule rule = Fk.getInstance().getFkPI().getRulesManager().getRuleByName(getClass().getSimpleName());

		if(day <= Fk.getInstance().getGame().getDays())
			throw new FkLightException("Ce jour est en cours ou déjà passé");

		if((int) rule.getValue() <= Fk.getInstance().getGame().getDays())
			throw new FkLightException("Le cap d'origine a été dépassé");

		rule.setValue(Integer.valueOf(day));
		broadcast(msg + (day == 1 ? " dès le" : " à partir du") + " jour", String.valueOf(day), " ! ");
	}
}
