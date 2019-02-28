package fr.devsylone.fallenkingdom.commands.rules.rulescommands.booleancommands;

import org.bukkit.entity.Player;

import fr.devsylone.fallenkingdom.commands.rules.FkBooleanRuleCommand;
import fr.devsylone.fallenkingdom.players.FkPlayer;

public class DoPauseAfterDay extends FkBooleanRuleCommand
{
	public DoPauseAfterDay()
	{
		super("doPauseAfterDay", "Faire une pause après chaque jour");
	}

	public void execute(Player sender, FkPlayer fkp, String[] args)
	{
		setRuleValue(args[0]);
		broadcast("La partie", Boolean.valueOf(args[0]).booleanValue() ? "se met désormais" : "ne se met pas",
				"en pause à la fin de chaque jour ! ");
	}
}
