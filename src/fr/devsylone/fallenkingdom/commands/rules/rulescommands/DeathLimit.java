package fr.devsylone.fallenkingdom.commands.rules.rulescommands;

import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fkpi.FkPI;
import fr.devsylone.fkpi.rules.Rule;
import org.bukkit.entity.Player;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.rules.FkRuleCommand;
import fr.devsylone.fallenkingdom.players.FkPlayer;

public class DeathLimit extends FkRuleCommand
{
	public DeathLimit()
	{
		super("deathLimit", "<limit>", 1, Messages.CMD_MAP_RULES_DEATH_LIMIT);
	}

	public void execute(Player sender, FkPlayer fkp, String[] args)
	{
		int limit = assertPositiveNumber(args[0], true, Messages.CMD_ERROR_NAN);

		FkPI.getInstance().getRulesManager().setRule(Rule.DEATH_LIMIT, limit);

		if (limit == 0)
		{
			for (FkPlayer p : Fk.getInstance().getPlayerManager().getConnectedPlayers())
				p.clearDeaths();

			broadcast(Messages.CMD_RULES_DEATH_LIMIT_RESET.getMessage());
			broadcast(Messages.CMD_RULES_DEATH_LIMIT_REMOVED.getMessage());
		}
		else
		{

			broadcast(Messages.CMD_RULES_CHEST_LIMIT_FIXED.getMessage()
					.replace("%limit%", args[0])
					.replace("%unit%", Messages.Unit.DEATHS.tl(limit))
			);
		}
	}
}
