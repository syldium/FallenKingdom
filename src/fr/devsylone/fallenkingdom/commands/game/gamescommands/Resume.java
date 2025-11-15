package fr.devsylone.fallenkingdom.commands.game.gamescommands;

import fr.devsylone.fallenkingdom.commands.abstraction.CommandRole;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandResult;
import fr.devsylone.fallenkingdom.commands.abstraction.FkCommand;
import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fallenkingdom.version.FkSound;
import fr.devsylone.fkpi.rules.Rule;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.exception.FkLightException;
import fr.devsylone.fallenkingdom.game.Game;

import java.util.List;

import static fr.devsylone.fallenkingdom.version.Environment.setAdvanceTime;

public class Resume extends FkCommand
{
	public Resume()
	{
		super("resume", Messages.CMD_MAP_GAME_RESUME, CommandRole.ADMIN);
	}

	@Override
	public CommandResult execute(Fk plugin, CommandSender sender, List<String> args, String label)
	{
		if(plugin.getGame().isPreStart())
			throw new FkLightException(Messages.CMD_ERROR_GAME_NOT_STARTED);
		if(plugin.getGame().getState().equals(Game.GameState.STARTED))
			throw new FkLightException(Messages.CMD_ERROR_NOT_IN_PAUSE);

		plugin.getGame().startTimer();
		plugin.getGame().setState(Game.GameState.STARTED);

		if(!plugin.getFkPI().getRulesManager().getRule(Rule.ETERNAL_DAY))
		{
			for(World w : Bukkit.getWorlds())
				if (plugin.getWorldManager().isAffected(w))
					setAdvanceTime(w, true);
		}
		plugin.getDeepPauseManager().unfreezePlayers();
		plugin.getDeepPauseManager().resetAIs();
		plugin.getDeepPauseManager().unprotectItems();

		broadcast(Messages.CMD_GAME_RESUME.getMessage(), FkSound.NOTE_HARP);
		return CommandResult.SUCCESS;
	}
}
