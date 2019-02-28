package fr.devsylone.fallenkingdom.commands.rules.rulescommands;

import org.bukkit.entity.Player;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.rules.FkRuleCommand;
import fr.devsylone.fallenkingdom.exception.FkLightException;
import fr.devsylone.fallenkingdom.players.FkPlayer;

public class ChargedCreepers extends FkRuleCommand
{
	public ChargedCreepers()
	{
		super("chargedCreepers", "<taux de spawn> <chance de drop> <nombre de tnts>", 3,
				"Le §ctaux de spawn&r défini la chance en pourcentage qu'un creeper qui apparait soit un creeper chargé. La §cchance de drop &rest la chance en pourcentage que le creeper chargé donne de la tnt à sa mort. Le §cnombre de tnts &rsera le nombre de tnt qu'un creeper chargé donnera à sa mort, si il en donne");
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
			fr.devsylone.fkpi.rules.ChargedCreepers rule = (fr.devsylone.fkpi.rules.ChargedCreepers) Fk.getInstance()
					.getFkPI().getRulesManager().getRuleByName("ChargedCreepers");
			rule.setSpawn(Integer.parseInt(args[0]));
			rule.setDrop(Integer.parseInt(args[1]));
			rule.setTntAmount(Integer.parseInt(args[2]));
		}
		catch (NumberFormatException e)
		{
			throw new FkLightException(
					"Chacun des paramètres de la commandes doit être un nombre de 0 inclu à 100 inclu");
		}

		broadcast("§6Désormais, lorsqu'un creeper spawn, il a §c" + args[0]
				+ "%§6 de chance de se transformer en un creeper chargé.");
		broadcast(
				"§6Un creeper chargé a §c" + args[1] + "%§6 de chance de donner §c" + args[2] + " TNT(s)§6 à sa mort");
	}
}
