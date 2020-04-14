package fr.devsylone.fallenkingdom.commands.scoreboard.scoreboardcommands;

import java.util.ArrayList;
import java.util.List;

import fr.devsylone.fallenkingdom.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.scoreboard.FkScoreboardCommand;
import fr.devsylone.fallenkingdom.exception.FkLightException;
import fr.devsylone.fallenkingdom.players.FkPlayer;

public class Edit extends FkScoreboardCommand
{
	private final List<String> playersBeingLearningHowToEditTheBeautifulScoreboard = new ArrayList<>();

	public Edit()
	{
		super("edit", "", 0, Messages.CMD_MAP_SCOREBOARD_EDIT);
	}

	public void execute(final Player sender, final FkPlayer fkp, String[] args)
	{
		if(playersBeingLearningHowToEditTheBeautifulScoreboard.contains(sender.getName()))
			throw new FkLightException(Messages.CMD_ERROR_SCOREBOARD_BEING_LEARN_EDIT);

		if(fkp.hasAlreadyLearntHowToEditTheBeautifulScoreboard() && args.length < 2)
			fkp.newSbDisplayer();

		else
		{
			playersBeingLearningHowToEditTheBeautifulScoreboard.add(sender.getName());
			long time = 0;

			Bukkit.getScheduler().scheduleSyncDelayedTask(Fk.getInstance(), () -> fkp.sendMessage(Messages.SCOREBOARD_INTRO_SET_LINE), time * 20L);

			time += 7;

			Bukkit.getScheduler().scheduleSyncDelayedTask(Fk.getInstance(), () -> fkp.sendMessage(Messages.SCOREBOARD_INTRO_NUMBERS), time * 20L);

			time += 6;

			Bukkit.getScheduler().scheduleSyncDelayedTask(Fk.getInstance(), () -> fkp.sendMessage(Messages.SCOREBOARD_INTRO_EXAMPLE), time * 20L);

			time += 8;

			Bukkit.getScheduler().scheduleSyncDelayedTask(Fk.getInstance(), () -> fkp.sendMessage(Messages.SCOREBOARD_INTRO_EXAMPLE_RESULT), time * 20L);

			time += 6;

			Bukkit.getScheduler().scheduleSyncDelayedTask(Fk.getInstance(), new Runnable()
			{
				@Override
				public void run()
				{
					fkp.sendMessage(Messages.SCOREBOARD_INTRO_TRY_YOURSELF);
					fkp.sendMessage("§b§m-----------");
					fkp.newSbDisplayer();
					playersBeingLearningHowToEditTheBeautifulScoreboard.remove(sender.getName());
					fkp.knowNowSbEdit();
				}
			}, time * 20l);
		}
	}
}
