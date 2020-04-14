package fr.devsylone.fallenkingdom.commands.rules.rulescommands;

import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fkpi.FkPI;
import fr.devsylone.fkpi.rules.Rule;
import org.bukkit.entity.Player;

import fr.devsylone.fallenkingdom.commands.rules.FkRuleCommand;
import fr.devsylone.fallenkingdom.players.FkPlayer;

public class ChestLimit extends FkRuleCommand
{
	public ChestLimit()
	{
		super("chestLimit", "<limit> (mettre a 0 pour ne pas en avoir)", 1, Messages.CMD_MAP_RULES_CHEST_LIMIT);
	}

	public void execute(Player sender, FkPlayer fkp, String[] args)
	{
		int limit = assertPositiveNumber(args[0], true, Messages.CMD_ERROR_NAN);

		if(limit < 0)
			limit = 0;
		FkPI.getInstance().getRulesManager().setRule(Rule.CHEST_LIMIT, limit);

		if(limit == 0)
			broadcast(Messages.CMD_RULES_CHEST_LIMIT_REMOVED.getMessage());
		else
			broadcast(Messages.CMD_RULES_CHEST_LIMIT_FIXED.getMessage().replace("%limit%", args[0]));
		
	}
}
