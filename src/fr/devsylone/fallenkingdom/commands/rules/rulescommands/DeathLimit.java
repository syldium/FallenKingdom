package fr.devsylone.fallenkingdom.commands.rules.rulescommands;

import org.bukkit.entity.Player;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.rules.FkRuleCommand;
import fr.devsylone.fallenkingdom.exception.FkLightException;
import fr.devsylone.fallenkingdom.players.FkPlayer;

public class DeathLimit extends FkRuleCommand
{
	public DeathLimit()
	{
		super("deathLimit", "<limit>", 1, "Définit le nombre maximal de morts avant l'éliminination d'un joueur.");
	}

	public void execute(Player sender, FkPlayer fkp, String[] args)
	{

		try
		{
			Integer.parseInt(args[0]);
		}
		catch (NumberFormatException e)
		{
			throw new FkLightException(args[0] + " n'est pas un nombre valide ! ");
		}

		int limit = Integer.parseInt(args[0]);

		if (limit < 0) {
			limit = 0;
		}

		Fk.getInstance().getFkPI().getRulesManager().getRuleByName("DeathLimit").setValue(Integer.valueOf(limit));

		if (limit == 0)
		{
			Fk.getInstance().getFkPI().getRulesManager().getRuleByName("deathLimit").setValue(Integer.valueOf(0));
			for (FkPlayer p : Fk.getInstance().getPlayerManager().getConnectedPlayers())
			{
				p.clearDeaths();
			}

			broadcast(org.bukkit.ChatColor.GREEN + "Votre nombre de mort actuel a été remis à 0 !");
			broadcast("La deathLimit est maintenant", "désactivée ", "!");
		}
		else
		{

			broadcast("La deathLimit est désormais fixée à", String.valueOf(limit),
					"mort" + (limit == 1 ? "" : "s") + " !");
		}
	}
}
