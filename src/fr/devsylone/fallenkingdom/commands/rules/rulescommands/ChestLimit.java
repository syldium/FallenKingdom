package fr.devsylone.fallenkingdom.commands.rules.rulescommands;

import org.bukkit.entity.Player;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.rules.FkRuleCommand;
import fr.devsylone.fallenkingdom.exception.FkLightException;
import fr.devsylone.fallenkingdom.players.FkPlayer;

public class ChestLimit extends FkRuleCommand
{
	public ChestLimit()
	{
		super("chestLimit", "<limit> (mettre a 0 pour ne pas en avoir)", 1, "Définit la profondeur/hauteur maximale de la salle des coffres par rapport au centre de la base.");
	}

	public void execute(Player sender, FkPlayer fkp, String[] args)
	{
		try
		{
			Integer.parseInt(args[0]);
		}
		catch(NumberFormatException e)
		{
			throw new FkLightException(args[0] + " n'est pas un nombre valide ! ");
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			throw new FkLightException(usage);
		}

		int limit = Integer.parseInt(args[0]);

		if(limit < 0)
			limit = 0;
		
		Fk.getInstance().getFkPI().getRulesManager().getRuleByName("chestLimit").setValue(Integer.valueOf(limit));

		if(limit == 0)
			broadcast("Les salles des coffres peuvent maintenant être construites à", "n'importe quelle", "profondeur ou hauteur ! ");
		else
			broadcast("Les salles des coffres ne peuvent maintenant plus dépasser", String.valueOf(limit), "blocs de profondeur ou de hauteur ! ");
		
	}
}
