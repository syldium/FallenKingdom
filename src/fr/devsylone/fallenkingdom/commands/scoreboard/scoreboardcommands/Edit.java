package fr.devsylone.fallenkingdom.commands.scoreboard.scoreboardcommands;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import fr.devsylone.fallenkingdom.commands.abstraction.CommandPermission;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandResult;
import fr.devsylone.fallenkingdom.commands.abstraction.FkPlayerCommand;
import fr.devsylone.fallenkingdom.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.exception.FkLightException;
import fr.devsylone.fallenkingdom.players.FkPlayer;

public class Edit extends FkPlayerCommand
{
	private final List<UUID> playersBeingLearningHowToEditTheBeautifulScoreboard = new ArrayList<>();

	public Edit()
	{
		super("edit", Messages.CMD_MAP_SCOREBOARD_EDIT, CommandPermission.ADMIN);
	}

	@Override
	public CommandResult execute(Fk plugin, Player sender, FkPlayer fkp, List<String> args, String label)
	{
		if(playersBeingLearningHowToEditTheBeautifulScoreboard.contains(sender.getUniqueId()))
			throw new FkLightException(Messages.CMD_ERROR_SCOREBOARD_BEING_LEARN_EDIT);

		if(fkp.hasAlreadyLearntHowToEditTheBeautifulScoreboard() && args.size() < 2)
			fkp.newSbDisplayer();

		else
		{
			playersBeingLearningHowToEditTheBeautifulScoreboard.add(sender.getUniqueId());
			fkp.sendMessage(Messages.SCOREBOARD_INTRO_SET_LINE);

			long time = 7;

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
					playersBeingLearningHowToEditTheBeautifulScoreboard.remove(sender.getUniqueId());
					fkp.knowNowSbEdit();
				}
			}, time * 20L);
		}
		return CommandResult.SUCCESS;
	}
}
