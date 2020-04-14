package fr.devsylone.fallenkingdom.commands.rules.rulescommands;

import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fkpi.FkPI;
import fr.devsylone.fkpi.rules.Rule;
import org.bukkit.entity.Player;

import fr.devsylone.fallenkingdom.commands.rules.FkRuleCommand;
import fr.devsylone.fallenkingdom.exception.FkLightException;
import fr.devsylone.fallenkingdom.players.FkPlayer;

public class ChargedCreepers extends FkRuleCommand
{
	public ChargedCreepers()
	{
		super("chargedCreepers", "<taux de spawn> <chance de drop> <nombre de tnts>", 3,
				Messages.CMD_MAP_RULES_CHARGED_CREEPER);
	}

	public void execute(Player sender, FkPlayer fkp, String[] args)
	{
		try
		{
			for (int i = 0; i < 3; i++)
			{
				if ((Integer.parseInt(args[i]) > 100) || (Integer.parseInt(args[i]) < 0))
					throw new NumberFormatException();
			}
			fr.devsylone.fkpi.rules.ChargedCreepers rule = FkPI.getInstance().getRulesManager().getRule(Rule.CHARGED_CREEPERS);
			rule.setSpawn(Integer.parseInt(args[0]));
			rule.setDrop(Integer.parseInt(args[1]));
			rule.setTntAmount(Integer.parseInt(args[2]));
		}
		catch (NumberFormatException e)
		{
			throw new FkLightException(
					"Chacun des paramètres de la commandes doit être un nombre de 0 inclus à 100 inclus");
		}

		broadcast(Messages.CMD_RULES_CHARGED_CREEPERS_SPAWN_RATE.getMessage().replace("%spawn%", args[0]));
		broadcast(Messages.CMD_RULES_CHARGED_CREEPERS_DROP_RATE.getMessage().replace("%rate%", args[1]).replace("%amount%", args[2]));
	}
}
