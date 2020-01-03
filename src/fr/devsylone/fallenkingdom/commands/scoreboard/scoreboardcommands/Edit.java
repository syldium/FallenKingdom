package fr.devsylone.fallenkingdom.commands.scoreboard.scoreboardcommands;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.scoreboard.FkScoreboardCommand;
import fr.devsylone.fallenkingdom.exception.FkLightException;
import fr.devsylone.fallenkingdom.players.FkPlayer;

public class Edit extends FkScoreboardCommand
{
	private ArrayList<String> playersBeingLearningHowToEditTheBeautifulScoreboard = new ArrayList<String>();

	public Edit()
	{
		super("edit", "", 0, "Ouvre l'interface de configuration du scoreboard");
	}

	public void execute(final Player sender, final FkPlayer fkp, String[] args)
	{
		if(playersBeingLearningHowToEditTheBeautifulScoreboard.contains(sender.getName()))
			throw new FkLightException("Minutes papillon, t'es déjà en train d'apprendre à modifier le scoreboard :p");

		if(fkp.hasAlreadyLearntHowToEditTheBeautifulScoreboard() && args.length < 2)
			fkp.newSbDisplayer();

		else
		{
			playersBeingLearningHowToEditTheBeautifulScoreboard.add(sender.getName());
			long time = 0;

			Bukkit.getScheduler().scheduleSyncDelayedTask(Fk.getInstance(), new Runnable()
			{
				@Override
				public void run()
				{
					Fk.broadcast("§bPour éditer une ligne du scoreboard, utilisez la commande §e/fk scoreboard SetLine <Numéro de la ligne> <Texte>");
				}
			}, time * 20l);

			time += 7;

			Bukkit.getScheduler().scheduleSyncDelayedTask(Fk.getInstance(), new Runnable()
			{
				@Override
				public void run()
				{
					Fk.broadcast("§bLes numéros sont affichés en §crouge §bà droite de chaque ligne");
				}
			}, time * 20l);

			time += 6;

			Bukkit.getScheduler().scheduleSyncDelayedTask(Fk.getInstance(), new Runnable()
			{
				@Override
				public void run()
				{
					Fk.broadcast("");
					Fk.broadcast("§bVoici un exemple :\n -> §r/fk scoreboard SetLine 12 &6Temps actuel &7> &d{H}:{M}");
				}
			}, time * 20l);

			time += 8;

			Bukkit.getScheduler().scheduleSyncDelayedTask(Fk.getInstance(), new Runnable()
			{
				@Override
				public void run()
				{
					Fk.broadcast("");
					Fk.broadcast("§bAffichera à 13h56 dans votre scoreboard \n§bà la ligne §c12 §b:  §6Jour actuel §7> §d13:56");
				}
			}, time * 20l);

			time += 6;

			Bukkit.getScheduler().scheduleSyncDelayedTask(Fk.getInstance(), new Runnable()
			{
				@Override
				public void run()
				{
					Fk.broadcast("");
					Fk.broadcast("§bÀ vous de jouer !");
					Fk.broadcast("§b§m-----------");
					fkp.newSbDisplayer();
					playersBeingLearningHowToEditTheBeautifulScoreboard.remove(sender.getName());
					fkp.knowNowSbEdit();
				}
			}, time * 20l);
		}
	}
}
