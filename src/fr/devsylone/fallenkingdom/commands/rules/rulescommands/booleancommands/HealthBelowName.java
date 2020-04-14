package fr.devsylone.fallenkingdom.commands.rules.rulescommands.booleancommands;

import fr.devsylone.fallenkingdom.utils.Messages;
import org.bukkit.entity.Player;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.rules.FkBooleanRuleCommand;
import fr.devsylone.fallenkingdom.players.FkPlayer;

public class HealthBelowName extends FkBooleanRuleCommand
{
	public HealthBelowName()
	{
		super("healthBelowName", Messages.CMD_MAP_RULES_HEALTH_BELOW_NAME);
	}

	public void execute(Player sender, FkPlayer fkp, String[] args)
	{
		setRuleValue(args[0]);

		if (Boolean.parseBoolean(args[0]))
			broadcast(Messages.CMD_RULES_HEALTH_BELOW_NAME_VISIBLE.getMessage());
		else
			broadcast(Messages.CMD_RULES_HEALTH_BELOW_NAME_HIDDEN.getMessage());
	}
}
