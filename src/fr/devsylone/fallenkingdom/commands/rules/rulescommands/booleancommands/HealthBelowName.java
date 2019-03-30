package fr.devsylone.fallenkingdom.commands.rules.rulescommands.booleancommands;

import org.bukkit.entity.Player;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.rules.FkBooleanRuleCommand;
import fr.devsylone.fallenkingdom.players.FkPlayer;

public class HealthBelowName extends FkBooleanRuleCommand
{
	public HealthBelowName()
	{
		super("HealthBelowName", "Permet de voir/ne plus voir la vie des joueurs sous leur pseudo");
	}

	public void execute(Player sender, FkPlayer fkp, String[] args) throws ReflectiveOperationException
	{
		setRuleValue(args[0]);

		broadcast("Désormais, la vie des joueurs", Boolean.valueOf(args[0]).booleanValue() ? "est" : "n'est plus", "visible sous leur pseudo !");

		Fk.getInstance().getScoreboardManager().recreateAllScoreboards();
	}
}
